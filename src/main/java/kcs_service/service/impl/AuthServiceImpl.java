package kcs_service.service.impl;

import kcs_service.constant.ClaimsConstant;
import kcs_service.constant.CommonConstants;
import kcs_service.dto.auth.AuthResponseDTO;
import kcs_service.dto.authorizePartyRole.AuthorizePartyRoleResponseDTO;
import kcs_service.dto.kcs_v1.KCSUserDTO;
import kcs_service.dto.user.UserRequestDTO;
import kcs_service.exception.UnauthorizedException;
import kcs_service.service.AuthService;
import kcs_service.service.JwtService;
import kcs_service.service.rest.UserClientService;
import kcs_service.util.CommonResponse;
import kcs_service.util.CommonValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserClientService userClientService;

    /**
     * This method is allowed to authenticate user by username and password and received the access and refresh token
     *
     * @param userRequest {@link UserRequestDTO} - user request dto
     * @return {@link CommonResponse} - authenticated user token response
     * @author maleeshasa
     */
    @Override
    public CommonResponse authenticateUser(UserRequestDTO userRequest) {
        log.info("AuthServiceImpl.authenticateUser() => started.");
        CommonResponse commonResponse = new CommonResponse();

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getUserName(), userRequest.getPassword())
        );
        if (authenticate.isAuthenticated()) {
            commonResponse.setMessage("Authentication accepted.");
            commonResponse.setData(new AuthResponseDTO(jwtService.generateToken(userRequest), null));
            commonResponse.setStatus(HttpStatus.ACCEPTED);

        } else {
            commonResponse.setMessage("Unauthorized user!");
            commonResponse.setData(null);
            commonResponse.setStatus(HttpStatus.UNAUTHORIZED);
        }
        log.info("AuthServiceImpl.authenticateUser() => ended.");
        return commonResponse;
    }

    /**
     * This method is allowed to validate the token
     *
     * @param token {@link String} - token
     * @return {@link CommonResponse} - validity response of the token
     * @author maleeshasa
     */
    @Override
    public CommonResponse validateToken(String token) {
        log.info("AuthServiceImpl.validateToken() => started.");
        CommonResponse commonResponse = new CommonResponse();
        if (!CommonValidation.stringNullValidation(token) && token.startsWith(CommonConstants.BEARER)) {
            token = token.substring(7);

            String username = jwtService.extractUserName(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                KCSUserDTO user = userClientService.getByUserName(username);

                Map<String, Object> map = new HashMap<>();
                map.put("username", username);
                map.put("valid", Boolean.TRUE);

                // set azp and azp roles
                Map<String, List<String>> azpWithRoles = new HashMap<>();
                user.getUserHasAuthorizeParties().forEach(
                        userHasAzp -> azpWithRoles.put(userHasAzp.getParty(),
                                userHasAzp.getAuthorizePartyRoles().stream()
                                        .map(AuthorizePartyRoleResponseDTO::getRole)
                                        .toList())
                );
                map.put(ClaimsConstant.AUTHORIZE_PARTY, azpWithRoles);

                // Set granted authorities
                map.put(ClaimsConstant.APP_SCOPE_WITH_ROLE, user.getAppScopeWithRole());

                commonResponse.setData(map);
                commonResponse.setStatus(HttpStatus.ACCEPTED);
                commonResponse.setMessage("Authorization accepted.");

                log.info("AuthServiceImpl.validateToken() => ended.");
                return commonResponse;

            } else {
                log.warn("Unauthorized user!");
                throw new UnauthorizedException("Unauthorized user!");
            }

        } else {
            log.warn("Token is invalid!");
            throw new UnauthorizedException("Token is invalid!");
        }
    }
}
