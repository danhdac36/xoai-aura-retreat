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
public class SecurityConfig {

        @Autowired
        private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/",
                        "/home",
                        "/about-us",
                        "/auth/login",
                        "/package",
                        "/auth/register",
                        "/auth/forgot-password",
                        "/auth/login-admin",
                        "/auth/verify-email",
                        "/wellness",
                        "/spa",
                        "/culinary",
                        "/villas",
                        "/css/**",
                        "/js/**",
                        "/images/**"
        };

        private static final String[] GUEST_ENDPOINTS = {
                        // "/user/**",
                        // "/orders/**",
                        // "/cart/**",
                        // "/profile/**"
        };

        private static final String[] ADMIN_ENDPOINTS = {
                        "/admin/**",
                        "/dashboard/**",
                        "/manage/**"
        };

        private static final String[] RECEPTIONIST_ENDPOINTS = {
                        // "/receptionist/**"
                        // "/receptionist/**"
        };

        private static final String[] THERAPIST_ENDPOINTS = {
                        // "/therapist/**" };
        };

        private static final String[] CHEFF_ENDPOINTS = {
                        "/F&B/**" };

        private static final String[] MANAGER_ENDPOINTS = {
                        "/management/**",
                        "/manager/housekeeping/**"
        };

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
                                                .requestMatchers(MANAGER_ENDPOINTS).hasAnyRole("MANAGER", "ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/aut" +
                                                                "h/login")
                                                .loginProcessingUrl("/auth/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/", false)
                                                .failureUrl("/auth/login?error=true")
                                                .permitAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/auth/login")
                                                .successHandler(oAuth2LoginSuccessHandler))
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .logoutSuccessUrl("/")
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
