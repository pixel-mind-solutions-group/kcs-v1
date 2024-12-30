package kcs_service.service.rest;

import kcs_service.dto.kcs_v1.KCSUserDTO;

public interface UserClientService {

    KCSUserDTO getByUserName(String userName);
}
