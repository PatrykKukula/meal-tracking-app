package io.github.patrykkukula.diet_ms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Utility class for Authentication related actions
 */
@Component
@Slf4j
public class AuthenticationUtils {

    // Returns JwtAuthenticationToken for authorization purposes
    public JwtAuthenticationToken getJwtAuthenticationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        return token;
    }

    // returns authenticated user username
    public String getAuthenticatedUserUsername() {
        JwtAuthenticationToken token = getJwtAuthenticationToken();

        return (String) token.getTokenAttributes().get("preferred_username");
    }
}
