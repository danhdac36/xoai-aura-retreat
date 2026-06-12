package com.AuraMoon.auramoon.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Allow public endpoints and static resources
        if (path.startsWith("/login") || path.startsWith("/auth") || path.startsWith("/register")
                || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")
                || path.startsWith("/webjars") || path.startsWith("/error") || path.equals("/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            return true;
        }

        // Redirect to login if not authenticated
        response.sendRedirect("/login");
        return false;
    }
}

