package uts.mi.matricula.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.Cookie;
import uts.mi.matricula.service.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Autowired
    private PermisoService permisoService;

    private static final String TOKEN_COOKIE_NAME = "token";

    private static final Map<String, String> roleRoutes = Map.ofEntries(
        Map.entry("/admin", "ADMIN"),
        Map.entry("/materias", "COORDINADOR"),
        Map.entry("/materias/", "COORDINADOR"),
        Map.entry("/pensums", "COORDINADOR"),
        Map.entry("/perms", "ADMIN"),
	Map.entry("/grupos", "PROFESOR"),
        Map.entry("/info/admin", "ADMIN"),
        Map.entry("/info/coordinator", "COORDINADOR"),
	Map.entry("/info/teacher", "PROFESOR")
    );

    // Permisos requeridos por ruta
    private static final Map<String, String> permisoRoutes = Map.ofEntries(
        Map.entry("/materias", "GESTION_MATERIAS"),
        Map.entry("/materias/", "GESTION_MATERIAS"),
        Map.entry("/pensums", "GESTION_PENSUMS"),
	Map.entry("/grupos", "GESTION_GRUPOS")
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = extractToken(request);

        if (token == null || !JwtUtil.isTokenValid(token)) {
            response.sendRedirect("/login");
            return false;
        }

        String role;
        try {
            role = JwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            response.sendRedirect("/login");
            return false;
        }

        String uri = request.getRequestURI();

        for (Map.Entry<String, String> entry : roleRoutes.entrySet()) {
            if (uri.startsWith(entry.getKey()) && !entry.getValue().equalsIgnoreCase(role)) {
                response.sendRedirect("/unauthorized");
                return false;
            }
        }

        for (Map.Entry<String, String> entry : permisoRoutes.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                String permisoCodigo = entry.getValue();
                boolean permitido = permisoService.isPermisoActivo(permisoCodigo);
                if (!permitido) {
                    response.sendRedirect("/unauthorized");
                    return false;
                }
            }
        }

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
