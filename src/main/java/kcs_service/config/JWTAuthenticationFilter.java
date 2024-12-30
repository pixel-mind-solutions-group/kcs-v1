package kcs_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kcs_service.service.JwtService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author @maleeshasa
 * @Date 2024/11/15
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Intercepts and processes incoming HTTP requests to authenticate users based on JWT tokens.
     * This filter checks for a valid Authorization header, extracts and validates the JWT, and
     * sets the authentication context if the token is valid.
     *
     * @param request     {@link HttpServletRequest} - the HTTP request containing the Authorization header
     * @param response    {@link HttpServletResponse} - the HTTP response for sending status or error messages
     * @param filterChain {@link FilterChain} - the chain of filters to process the request and response
     * @logic: 1. Checks if the Authorization header is present and starts with "Bearer".
     * - If not, it proceeds with the filter chain without performing any authentication.
     * 2. Extracts the JWT from the header and retrieves the username from the token using `jwtService`.
     * 3. Verifies if the user is not already authenticated in the `SecurityContextHolder`.
     * - Loads the user's details using `userDetailsService`.
     * - Validates the token using `jwtService`.
     * - If valid, creates an `UsernamePasswordAuthenticationToken` and sets it in the `SecurityContextHolder`.
     * 4. Proceeds with the remaining filters in the chain.
     * @author maleeshasa
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        logger.info("JWTAuthenticationFilter.doFilterInternal() => started.");
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userName = jwtService.extractUserName(jwt);

        Authentication authentication
                = SecurityContextHolder.getContext().getAuthentication();

        if (userName != null && authentication == null) {
            // Authenticate
            UserDetails userDetails
                    = userDetailsService.loadUserByUsername(userName);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        authHeader.substring(7),
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        logger.info("JWTAuthenticationFilter.doFilterInternal() => ended.");
        filterChain.doFilter(request, response);
    }
}
