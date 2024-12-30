package kcs_service.service;


import kcs_service.dto.user.UserRequestDTO;
import kcs_service.util.CommonResponse;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
public interface AuthService {

    /**
     * This method is allowed to authenticate user by username and password and received the access and refresh token
     *
     * @param userRequest {@link UserRequestDTO} - user request dto
     * @return {@link CommonResponse} - authenticated user token response
     * @author maleeshasa
     */
    CommonResponse authenticateUser(UserRequestDTO userRequest);

    /**
     * This method is allowed to validate the token
     *
     * @param token {@link String} - token
     * @return {@link CommonResponse} - validity response of the token
     * @author maleeshasa
     */
    CommonResponse validateToken(String token);
}
