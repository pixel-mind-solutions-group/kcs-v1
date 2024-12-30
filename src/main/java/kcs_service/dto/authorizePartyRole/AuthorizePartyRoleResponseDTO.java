package kcs_service.dto.authorizePartyRole;

import lombok.Getter;
import lombok.Setter;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Getter
@Setter
public class AuthorizePartyRoleResponseDTO {
    private Integer authorizePartyRoleId;
    private String role;
    private Boolean active;
}
