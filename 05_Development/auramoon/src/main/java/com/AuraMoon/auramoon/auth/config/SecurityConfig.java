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

        @Autowired
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

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
                        "/booking/itinerary/**",
                        "/guest/booking-spa/**",
                        "/fnb/meal-selection/**",
                        "/fnb/alacarte-order/**",
                        "/profile/health/**",
                        "/packges/**"
        };

        private static final String[] ADMIN_ENDPOINTS = {
                        "/admin/**",
                        "/manager/dashboard/**"
        };

        private static final String[] RECEPTIONIST_ENDPOINTS = {
                        "/receptionist/bookings/**",
                        "/receptionist/booking-spa/**",
                        "/receptionist/home"
        };

        private static final String[] THERAPIST_ENDPOINTS = {
                        "/therapist/schedules/**",
                        "/therapist/home"
        };

        private static final String[] CHEFF_ENDPOINTS = {
                        "/fnb/chef/**"
        };

        private static final String[] MANAGER_ENDPOINTS = {
                        "/manager/housekeeping/**",
                        "/billing/night-audit/**",
                        "/manager/spa/**",
                        "/manager/home"
        };

        private static final String[] MANAGER_ADMIN_ENDPOINTS = {
                        "/manager/report/**",
                        "/packages/**",
                        "/manager/reviews/**"
        };

        private static final String[] RECEPTIONIST_ADMIN_ENDPOINTS = {
                        "/receptionist/villa/**"
        };

        private static final String[] YOGA_INSTRUCTOR_ENDPOINTS = {
                        "/instructor/yoga/**"
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
                                                .requestMatchers(MANAGER_ENDPOINTS).hasRole("MANAGER")
                                                .requestMatchers(MANAGER_ADMIN_ENDPOINTS).hasAnyRole("MANAGER", "ADMIN", "GUEST")
                                                .requestMatchers(YOGA_INSTRUCTOR_ENDPOINTS).hasRole("YOGA_INSTRUCTOR")

                                                .requestMatchers(RECEPTIONIST_ADMIN_ENDPOINTS).hasAnyRole("RECEPTIONIST", "ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/auth/login")
                                                .loginProcessingUrl("/auth/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .successHandler(customAuthenticationSuccessHandler)
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
