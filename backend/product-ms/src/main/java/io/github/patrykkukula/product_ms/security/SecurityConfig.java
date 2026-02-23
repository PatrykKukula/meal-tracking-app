package io.github.patrykkukula.product_ms.security;

import io.github.patrykkukula.mealtrackingapp_common.exception.AccessDeniedHandlerImpl;
import io.github.patrykkukula.mealtrackingapp_common.exception.AuthenticationEntryPointImpl;
import io.github.patrykkukula.mealtrackingapp_common.security.KeycloakRealmRolesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String ROLE_ADMIN = "ADMIN";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

        // Add CORS when frontend is available
        httpSecurity.sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(scc -> scc.requireExplicitSave(false))
                .authorizeHttpRequests(authorize -> authorize.
                        requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/products/**").authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        httpSecurity.exceptionHandling( ehc -> {
            ehc.accessDeniedHandler(accessDeniedHandler());
            ehc.authenticationEntryPoint(authenticationEntryPoint());
        });

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(keycloakRealmRolesConverter());

        return jwtAuthenticationConverter;
    }

    @Bean
    public KeycloakRealmRolesConverter keycloakRealmRolesConverter(){
        return new KeycloakRealmRolesConverter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }
}
