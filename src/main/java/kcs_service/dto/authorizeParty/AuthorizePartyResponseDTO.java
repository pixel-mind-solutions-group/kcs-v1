package kcs_service.dto.authorizeParty;

import kcs_service.dto.authorizePartyRole.AuthorizePartyRoleResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Getter
@Setter
public class AuthorizePartyResponseDTO {
    private Integer authorizePartyId;
    private String party;
    private Boolean active;
    private List<AuthorizePartyRoleResponseDTO> authorizePartyRoles = new ArrayList<>();
}
