package com.example.cqrs.security.jwt;

import com.example.cqrs.security.AuthoritiesConstants;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class SecurityUtils {
    public static final String SYSTEM = "SYSTEM";

    public static Optional<UUID> getCurrentWorkGroupId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && isAuthenticated()) {
            JwtUser springSecurityUser = (JwtUser) authentication.getPrincipal();
            return Optional.ofNullable(springSecurityUser.getWorkGroupId());
        }
        return Optional.empty();
    }

    public static String getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && isAuthenticated()) {
            JwtUser springSecurityUser = (JwtUser) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else {
            return SYSTEM;
        }
    }

    public static boolean isAuthenticated() {
        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ROLE_ANONYMOUS)) {
                    return false;
                }
            }
        }
        return true;
    }
}
