package uts.mi.matricula.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendRedirect("/login");
            return false;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            response.sendRedirect("/login");
            return false;
        }

        String role = JwtUtil.getRoleFromToken(token);
        String uri = request.getRequestURI();

        // Validación por ruta
        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect("/unauthorized");
            return false;
        } else if (uri.startsWith("/student") && !"ESTUDIANTE".equalsIgnoreCase(role)) {
            response.sendRedirect("/unauthorized");
            return false;
        } else if (uri.startsWith("/coordinator") && !"COORDINADOR".equalsIgnoreCase(role)) {
            response.sendRedirect("/unauthorized");
            return false;
        } else if (uri.startsWith("/teacher") && !"PROFESOR".equalsIgnoreCase(role)) {
            response.sendRedirect("/unauthorized");
            return false;
        } else if (uri.startsWith("/audit") && !"AUDITOR".equalsIgnoreCase(role)) {
            response.sendRedirect("/unauthorized");
            return false;
        }

        return true;
    }
}

