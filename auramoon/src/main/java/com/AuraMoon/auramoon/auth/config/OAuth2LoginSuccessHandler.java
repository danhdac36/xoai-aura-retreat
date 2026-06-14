package com.AuraMoon.auramoon.auth.config;

import com.AuraMoon.auramoon.auth.entity.User;
import com.AuraMoon.auramoon.auth.service.IAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private IAuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email != null) {
            User user = authService.createGoogleUser(email, name != null ? name : "Google User");
            
            // Map database authority
            String roleName = user.getRole().getRoleName();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            
            // Re-authenticate session with custom DB authorities, keeping OAuth2 token type
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
            OAuth2AuthenticationToken customAuth = new OAuth2AuthenticationToken(
                    oAuth2User, Collections.singletonList(authority), "google"
            );
            SecurityContextHolder.getContext().setAuthentication(customAuth);
        }

        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
