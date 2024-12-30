package kcs_service.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kcs_service.constant.ClaimsConstant;
import kcs_service.dto.authorizePartyRole.AuthorizePartyRoleResponseDTO;
import kcs_service.dto.kcs_v1.KCSUserDTO;
import kcs_service.dto.user.UserRequestDTO;
import kcs_service.service.JwtService;
import kcs_service.service.rest.UserClientService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Service
@RequiredArgsConstructor
@Getter
public class JwtServiceImpl implements JwtService {

    private final UserClientService userClientService;

    @Value("${secret-key}")
    private String secretKey;

    @Override
    public String generateToken(UserRequestDTO userRequest) {
        Map<String, Object> claims = new HashMap<>();

        // Fetch user by uname
        KCSUserDTO user = userClientService.getByUserName(userRequest.getUserName());

        // adding claim details
        claims.put(ClaimsConstant.TOKEN_TYPE, ClaimsConstant.BEARER);
        claims.put(ClaimsConstant.ALLOWED_ORIGINS, Collections.singletonList("http://localhost:8083"));
        claims.put(ClaimsConstant.EMAIL_VERIFIED, user.getIsEmailVerified());

        // set azp and azp roles
        Map<String, List<String>> azpWithRoles = new HashMap<>();
        user.getUserHasAuthorizeParties().forEach(
                userHasAzp -> azpWithRoles.put(userHasAzp.getParty(),
                        userHasAzp.getAuthorizePartyRoles().stream()
                                .map(AuthorizePartyRoleResponseDTO::getRole)
                                .toList())
        );
        claims.put(ClaimsConstant.AUTHORIZE_PARTY, azpWithRoles);

        // User details
        claims.put(ClaimsConstant.EMAIL, user.getEmail());
        claims.put(ClaimsConstant.NAME, user.getFirstName().concat(" ").concat(user.getLastName()));
        claims.put(ClaimsConstant.SUB, user.getUserName());

        claims.put(ClaimsConstant.APP_SCOPE_WITH_ROLE, user.getAppScopeWithRole());

        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(userRequest.getUserName())
                .issuer("MLS")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 20 * 1000))
                .and()
                .signWith(generateKey())
                .compact();
    }

    @Override
    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private SecretKey generateKey() {
        byte[] decode = Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }
}
