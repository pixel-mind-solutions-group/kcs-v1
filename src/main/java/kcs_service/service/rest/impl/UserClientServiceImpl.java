package kcs_service.service.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kcs_service.client.PDEVUserServiceClient;
import kcs_service.dto.kcs_v1.KCSUserDTO;
import kcs_service.exception.BaseException;
import kcs_service.exception.FeignCustomException;
import kcs_service.exception.RecordNotFoundException;
import kcs_service.service.rest.UserClientService;
import kcs_service.util.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {

    private final PDEVUserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    public KCSUserDTO getByUserName(String userName) {

        try {
            ResponseEntity<CommonResponse> response = userServiceClient.getByUserName(userName);
            if (response.getStatusCode().equals(HttpStatus.OK) &&
                    Objects.requireNonNull(response.getBody()).getStatus().equals(HttpStatus.OK) &&
                    response.getBody().getData() != null) {

                return objectMapper.convertValue(response.getBody().getData(), KCSUserDTO.class);

            } else {
                throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while fetching user.");
            }

        } catch (RecordNotFoundException e) {
            throw new BaseException(HttpStatus.NOT_FOUND.value(), e.getMessage());

        } catch (FeignCustomException e) {
            throw new BaseException(500, "Error occurred while calling user service to fetch user by username. Error: " + e.getMessage());
        }
    }
}
