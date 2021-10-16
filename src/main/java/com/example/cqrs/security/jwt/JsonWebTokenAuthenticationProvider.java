package com.example.cqrs.security.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@AllArgsConstructor
public class JsonWebTokenAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Authentication authenticatedUser = null;
        // Only process the PreAuthenticatedAuthenticationToken
        if (authentication.getClass().isAssignableFrom(PreAuthenticatedAuthenticationToken.class)
                && authentication.getPrincipal() != null) {
            String tokenHeader = (String) authentication.getPrincipal();
            JwtUser userDetails = jwtTokenUtil.parseAndValidate(tokenHeader);
            if (userDetails != null) {
                // Prevent user from injecting user than what's on token
                if (!userDetails.getUsername().equalsIgnoreCase(((String) authentication.getCredentials()))) {
                    throw new BadCredentialsException("Janus and Token Auth do not match");
                }
                authenticatedUser = new JsonWebTokenAuthentication(userDetails, tokenHeader);
            }
        } else {
            // It is already a JsonWebTokenAuthentication
            authenticatedUser = authentication;
        }
        return authenticatedUser;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(PreAuthenticatedAuthenticationToken.class)
                || authentication.isAssignableFrom(JsonWebTokenAuthentication.class);
    }
}