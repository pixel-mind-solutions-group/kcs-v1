package kcs_service.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Getter
@Setter
public class JwtAuthenticationTokenResponseDTO {
    private PrincipalDTO principal;
}
