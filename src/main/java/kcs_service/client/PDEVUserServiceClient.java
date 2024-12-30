package kcs_service.client;

import feign.Headers;
import kcs_service.util.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@FeignClient(name = "iam-service")
@Headers("Content-Type: application/json")
public interface PDEVUserServiceClient {

    /**
     * This method is allowed to get user by username calling iam service
     *
     * @param userName {@link String} - user name
     * @return {@link ResponseEntity<CommonResponse>} - user details response by username
     * @author maleesahsa
     */
    @GetMapping(value = "/api/iam/v1/user/get-by-username/{userName}")
    ResponseEntity<CommonResponse> getByUserName(@PathVariable String userName);
}
