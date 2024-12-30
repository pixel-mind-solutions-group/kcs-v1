package kcs_service.dto.user;

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
public class UserRequestDTO {
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String uuid;
    private Boolean active;
    private List<Integer> userHasAuthorizePartyIds = new ArrayList<>();
}
