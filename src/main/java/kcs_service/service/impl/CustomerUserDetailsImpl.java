package kcs_service.service.impl;

import kcs_service.dto.kcs_v1.KCSUserDTO;
import kcs_service.service.CustomerUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Service
public class CustomerUserDetailsImpl implements CustomerUserDetails {

    private KCSUserDTO user;

    public CustomerUserDetailsImpl() {
    }

    public CustomerUserDetailsImpl(KCSUserDTO user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Map<String, SimpleGrantedAuthority> authorityMap = new HashMap<>();

        for (Map.Entry<String, String> entry : user.getAppScopeWithRole().entrySet()) {
            authorityMap.put(entry.getKey(), new SimpleGrantedAuthority(entry.getValue()));
        }
        return authorityMap.values();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
