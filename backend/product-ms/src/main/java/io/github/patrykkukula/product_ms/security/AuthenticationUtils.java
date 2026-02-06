package io.github.patrykkukula.product_ms.security;

import io.github.patrykkukula.product_ms.model.Product;
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

    // returns true if user has ADMIN role
    public boolean isAdmin() {
        JwtAuthenticationToken token = getJwtAuthenticationToken();

        return token.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> {
                    if (grantedAuthority.getAuthority() == null) {
                        return false;
                    }
                    return grantedAuthority.getAuthority().equals("ROLE_ADMIN");
                });
    }

    // returns true if user is allowed to modify product
    public boolean canUserModifyProduct(Product product) {
        if (product.getOwnerUsername() == null) {
            if (!isAdmin())
                throw new AccessDeniedException("Access denied");                                // only ADMIN can modify global product
        } else {
            if (!product.getOwnerUsername().equals(getAuthenticatedUserUsername())) {            // only owner can modify product he added
                throw new AccessDeniedException("You do not have permission to access this resource");
            }
        }
        return true;
    }
}
