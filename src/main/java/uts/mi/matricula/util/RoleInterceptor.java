package uts.mi.matricula.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.Cookie;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

    String token = null;

    // Intentar extraer el token del encabezado Authorization
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
    }

    // Si no hay token en el header, buscar en las cookies
    if (token == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
    }

    if (token == null || !JwtUtil.isTokenValid(token)) {
        response.sendRedirect("/login");
        return false;
    }

    String role = JwtUtil.getRoleFromToken(token);
    String uri = request.getRequestURI();

    // Validación de rol
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

