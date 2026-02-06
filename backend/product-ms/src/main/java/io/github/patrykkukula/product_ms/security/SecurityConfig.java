package io.github.patrykkukula.product_ms.security;

import io.github.patrykkukula.product_ms.exception.AccessDeniedHandlerImpl;
import io.github.patrykkukula.product_ms.exception.AuthenticationEntryPointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

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

        httpSecurity.csrf(csrf -> csrf.disable());  // remove when frontend is available
        httpSecurity.exceptionHandling( ehc -> {
            ehc.accessDeniedHandler(new AccessDeniedHandlerImpl());
            ehc.authenticationEntryPoint(new AuthenticationEntryPointImpl());
        });

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRolesConverter());

        return jwtAuthenticationConverter;
    }
}
