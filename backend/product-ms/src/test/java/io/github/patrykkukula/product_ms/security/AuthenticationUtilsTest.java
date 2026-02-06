package io.github.patrykkukula.product_ms.security;

import io.github.patrykkukula.product_ms.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationUtilsTest {
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private AuthenticationUtils authenticationUtils;

    private Authentication authentication;
    private Jwt jwt;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        jwt = new Jwt(
                "123456",
                Instant.now(),
                Instant.now().plusSeconds(99L),
                Map.of("headerName", "headerValue"),
                Map.of("preferred_username", "admin")
        );
        authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    }

    @AfterEach
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should get JwtAuthenticationToken correctly")
    public void shouldGetJwtAuthenticationTokenCorrectly() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        JwtAuthenticationToken token = authenticationUtils.getJwtAuthenticationToken();

        assertNotNull(token);
        assertEquals(jwt, token.getToken());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when get jwt authentication token and token is different type")
    public void shouldThrowAccessDeniedExceptionWhenGetJwtAuthenticationTokenAndTokenIsDifferentType() {
        when(securityContext.getAuthentication()).thenReturn(new TestingAuthenticationToken(
                new Object(), new Object())
        );

        assertThrows(AccessDeniedException.class, () -> authenticationUtils.getJwtAuthenticationToken());
    }

    @Test
    @DisplayName("Should get authenticated username correctly")
    public void shouldGetAuthenticatedUserUsernameCorrectly() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String username = authenticationUtils.getAuthenticatedUserUsername();

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("Should return true if user is admin")
    public void shouldReturnTrueIfUserIsAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean isAdmin = authenticationUtils.isAdmin();

        assertTrue(isAdmin);
    }

    @Test
    @DisplayName("Should return false if user is user")
    public void shouldReturnFalseIfUserIsUser() {
        authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean isAdmin = authenticationUtils.isAdmin();

        assertFalse(isAdmin);
    }

    @Test
    @DisplayName("Should return true when can user modify product with ROLE_ADMIN and public product")
    public void shouldReturnTrueWhenCanUserModifyProductWithRoleAdminAndPublicProduct() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean canModify = authenticationUtils.canUserModifyProduct(Product.builder().build());

        assertTrue(canModify);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when can user modify product with ROLE_USER and public product")
    public void shouldThrowAccessDeniedExceptionWhenCanUserModifyProductWithRoleUserAndPublicProduct() {
        authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList("ROLE_USER"));

        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(AccessDeniedException.class, () -> authenticationUtils.canUserModifyProduct(Product.builder().build()));
    }

    @Test
    @DisplayName("Should return true when can user modify product with ROLE_USER and private product")
    public void shouldReturnTrueWhenCanUserModifyProductWithRoleUserAndPrivateProduct() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean canModify = authenticationUtils.canUserModifyProduct(Product.builder().ownerUsername("admin").build());

        assertTrue(canModify);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when can user modify product with ROLE_USER and product have different owner")
    public void shouldThrowAccessDeniedExceptionWhenCanUserModifyProductWithRoleUserAndProductHaveDifferentOwner() {
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(AccessDeniedException.class, () -> authenticationUtils.canUserModifyProduct(Product.builder().ownerUsername("different").build()));
    }
}
