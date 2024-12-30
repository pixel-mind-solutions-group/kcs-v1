package kcs_service.dto.kcs_v1;

import kcs_service.dto.authorizeParty.AuthorizePartyResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Getter
@Setter
public class KCSUserDTO {
    private Integer idUser;
    private String email;
    private Boolean isEmailVerified;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Boolean active;
    private Short failCount;
    private List<AuthorizePartyResponseDTO> userHasAuthorizeParties = new ArrayList<>();
    private Map<String, String> appScopeWithRole = new HashMap<>();
}
