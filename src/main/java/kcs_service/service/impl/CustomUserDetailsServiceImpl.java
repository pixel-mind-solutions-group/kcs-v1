package kcs_service.service.impl;

import kcs_service.dto.kcs_v1.KCSUserDTO;
import kcs_service.service.CustomUserDetailsService;
import kcs_service.service.rest.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserClientService userClientService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        KCSUserDTO user = userClientService.getByUserName(username);
        if (Objects.isNull(user)) {
            log.error("User not available.");
            throw new UsernameNotFoundException("User not available.");
        }
        return new CustomerUserDetailsImpl(user);
    }

}
