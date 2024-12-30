package kcs_service.service;

import kcs_service.dto.user.UserRequestDTO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
public interface JwtService {

    String generateToken(UserRequestDTO userRequest);

    String extractUserName(String jwt);

    boolean isTokenValid(String jwt, UserDetails userDetails);
}
