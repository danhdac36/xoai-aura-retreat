package com.AuraMoon.auramoon.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableJpaAuditing
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/home",
            "/about-us",
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/login-admin",
            "/auth/verify-email",
            "/fnb/uc19-alacarte-order",
            "/packages",
            "/css/**",
            "/js/**",
            "/images/**"
    };

    private static final String[] GUEST_ENDPOINTS = {
            "/user/**",
            "/orders/**",
            "/cart/**",
            "/profile/**"
    };

    private static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/dashboard/**",
            "/manage/**"
    };

    private static final String[] RECEPTIONIST_ENDPOINTS = {
            "/receptionist/**"
    };

    private static final String[] THERAPIST_ENDPOINTS = {
            "/therapist/**" };

    private static final String[] CHEFF_ENDPOINTS = {
            "/F&B/**" };

    private static final String[] MANAGER_ENDPOINTS = {
            "/management/**" };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(GUEST_ENDPOINTS).hasRole("GUEST")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(RECEPTIONIST_ENDPOINTS).hasRole("RECEPTIONIST")
                        .requestMatchers(THERAPIST_ENDPOINTS).hasRole("THERAPIST")
                        .requestMatchers(CHEFF_ENDPOINTS).hasRole("CHEFF")
                        .requestMatchers(MANAGER_ENDPOINTS).hasRole("MANAGER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .successHandler(oAuth2LoginSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(sm -> sm
                        .maximumSessions(2))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/register", "/auth/forgot-password",
                        "/auth/verify-email"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
