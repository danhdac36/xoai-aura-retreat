package com.AuraMoon.auramoon.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String role = grantedAuthority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/home";
                break;
            } else if (role.equals("ROLE_MANAGER")) {
                redirectUrl = "/manager/home";
                break;
            } else if (role.equals("ROLE_RECEPTIONIST")) {
                redirectUrl = "/receptionist/home";
                break;
            } else if (role.equals("ROLE_THERAPIST")) {
                redirectUrl = "/therapist/home";
                break;
            } else if (role.equals("ROLE_CHEFF")) {
                redirectUrl = "/fnb/chef/home";
                break;
            } else if (role.equals("ROLE_YOGA_INSTRUCTOR")) {
                redirectUrl = "/instructor/yoga/home";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
