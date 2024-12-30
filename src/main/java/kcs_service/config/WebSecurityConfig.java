package kcs_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the Spring Security filter chain for the application. This method defines
     * security policies such as endpoint access rules, session management settings,
     * and the inclusion of custom filters.
     *
     * @param httpSecurity {@link HttpSecurity} - the security configuration object provided by Spring Security
     * @return {@link SecurityFilterChain} - the configured security filter chain, which Spring uses to process incoming requests
     * @configuration: 1. Disables CSRF protection for stateless applications.
     * 2. Defines endpoint-specific access rules:
     * - Public access to health check and authentication endpoints.
     * - Role-based access to user-related endpoints requiring the "PERMISSION_USER_AUTH_SERVICE" authority.
     * - Authentication required for all other endpoints.
     * 3. Sets session management to `STATELESS` mode, as JWTs are used for stateless authentication.
     * 4. Adds a custom JWT authentication filter (`jwtAuthenticationFilter`) before the default
     * `UsernamePasswordAuthenticationFilter`.
     * @author maleeshasa
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("WebSecurityConfig.securityFilterChain() => started.");
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/kcs_v1/v1/auth/user/token").permitAll()
                                .requestMatchers("/api/kcs_v1/v1/auth/user/token/validate").permitAll()
                                .anyRequest().authenticated()
                ).sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("WebSecurityConfig.securityFilterChain() => ended.");
        return httpSecurity
                .build();
    }

    /**
     * Configures and provides an {@link AuthenticationProvider} for the application.
     * This provider uses a custom {@link UserDetailsService} and a BCrypt password encoder
     * to authenticate users.
     *
     * @return {@link AuthenticationProvider} - the configured authentication provider
     * @configuration: 1. Uses `DaoAuthenticationProvider` for retrieving user details from the `UserDetailsService`.
     * 2. Configures the password encoder as `BCryptPasswordEncoder` to securely hash and verify passwords.
     * @author maleeshasa
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("WebSecurityConfig.authenticationProvider() => started.");

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());

        log.info("WebSecurityConfig.authenticationProvider() => ended.");
        return provider;
    }

    /**
     * Configures and provides a {@link BCryptPasswordEncoder} with a specified strength.
     * This encoder is used to hash passwords for secure storage and comparison.
     *
     * @return {@link BCryptPasswordEncoder} - the configured BCrypt password encoder
     * @configuration - Initializes the password encoder with a strength of 14, balancing security and performance.
     * @author maleeshasa
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        log.info("WebSecurityConfig.bCryptPasswordEncoder() => started.");
        return new BCryptPasswordEncoder(14);
    }

    /**
     * Provides an {@link AuthenticationManager} by delegating to the Spring Security configuration.
     * The authentication manager handles the authentication process for the application.
     *
     * @param configuration {@link AuthenticationConfiguration} - the Spring Security authentication configuration
     * @return {@link AuthenticationManager} - the authentication manager
     * @configuration - Delegates the retrieval of the authentication manager to the `AuthenticationConfiguration` bean.
     * @author maleeshasa
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("WebSecurityConfig.authenticationManager() => started.");
        return configuration.getAuthenticationManager();
    }
}
