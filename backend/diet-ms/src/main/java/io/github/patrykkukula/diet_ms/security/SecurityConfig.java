package io.github.patrykkukula.diet_ms.security;

import io.github.patrykkukula.mealtrackingapp_common.exception.AccessDeniedHandlerImpl;
import io.github.patrykkukula.mealtrackingapp_common.exception.AuthenticationEntryPointImpl;
import io.github.patrykkukula.mealtrackingapp_common.security.KeycloakRealmRolesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) {

        httpSecurity.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/diets/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(scc -> scc.requireExplicitSave(false))
                .csrf(csrf -> csrf.disable());

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
