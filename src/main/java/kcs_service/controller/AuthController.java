package kcs_service.controller;

import kcs_service.constant.CommonConstants;
import kcs_service.dto.user.UserRequestDTO;
import kcs_service.service.AuthService;
import kcs_service.util.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kcs_v1/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * This method is allowed to authenticate user by username and password and received the access and refresh token
     *
     * @param userRequest {@link UserRequestDTO} - user request dto
     * @return {@link ResponseEntity<CommonResponse>} - authenticated user token response
     * @author maleeshasa
     */
    @PostMapping(value = "/user/token")
    public ResponseEntity<CommonResponse> authenticateUser(@RequestBody UserRequestDTO userRequest) {
        log.info("AuthController.authenticate() => started.");
        return ResponseEntity.ok(authService.authenticateUser(userRequest));
    }

    /**
     * This method is allowed to validate the token
     *
     * @param token {@link String} - token
     * @return {@link ResponseEntity<CommonResponse>} - validity response of the token
     * @author maleeshasa
     */
    @GetMapping(value = "/user/token/validate")
    public ResponseEntity<CommonResponse> validateToken(@RequestHeader(CommonConstants.AUTHORIZATION) String token) {
        log.info("AuthController.validateToken() => started.");
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
