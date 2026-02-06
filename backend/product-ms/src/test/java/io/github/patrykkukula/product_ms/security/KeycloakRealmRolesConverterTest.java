package io.github.patrykkukula.product_ms.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class KeycloakRealmRolesConverterTest {
    @Autowired
    private KeycloakRealmRolesConverter keycloakRealmRolesConverter;
    private Jwt jwt;

    @Test
    @DisplayName("Should return authorities correctly")
    public void shouldReturnAuthoritiesCorrectly() {
        jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(10),
                Map.of("header", "value"),
                Map.of(
                        "realm_access", Map.of("roles", List.of("ADMIN", "USER"))
                )
        );

        Collection<GrantedAuthority> authorities = keycloakRealmRolesConverter.convert(jwt);

        assertEquals(2, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.stream().toList().getFirst().getAuthority());
        assertEquals("ROLE_USER", authorities.stream().toList().get(1).getAuthority());
    }

    @Test
    @DisplayName("Should return empty collection when realm access is null")
    public void shouldReturnEmptyCollectionWhenRealmAccessIsNull() {
        jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(10),
                Map.of("header", "value"),
                Map.of(
                        "not_realm_access", "")
                );

        Collection<GrantedAuthority> authorities = keycloakRealmRolesConverter.convert(jwt);

        assertEquals(0, authorities.size());
    }

    @Test
    @DisplayName("Should return empty collection when realm access is empty")
    public void shouldReturnEmptyCollectionWhenRealmAccessIsEmpty() {
        jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(10),
                Map.of("header", "value"),
                Map.of(
                        "realm_access", Collections.emptyMap())
        );

        Collection<GrantedAuthority> authorities = keycloakRealmRolesConverter.convert(jwt);

        assertEquals(0, authorities.size());
    }

    @Test
    @DisplayName("Should return empty collection when roles are null")
    public void shouldReturnEmptyCollectionWhenRolesAreNull() {
        jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(10),
                Map.of("header", "value"),
                Map.of(
                        "realm_access", Map.of("not_roles", ""))
        );

        Collection<GrantedAuthority> authorities = keycloakRealmRolesConverter.convert(jwt);

        assertEquals(0, authorities.size());
    }

    @Test
    @DisplayName("Should return empty collection when roles are empty")
    public void shouldReturnEmptyCollectionWhenRolesAreEmpty() {
        jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(10),
                Map.of("header", "value"),
                Map.of(
                        "realm_access", Map.of("roles", Collections.emptyList()))
        );

        Collection<GrantedAuthority> authorities = keycloakRealmRolesConverter.convert(jwt);

        assertEquals(0, authorities.size());
    }
}
