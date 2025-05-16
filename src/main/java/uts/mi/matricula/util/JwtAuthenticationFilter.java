package uts.mi.matricula.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Map<String, Predicate<String>> accessRules = Map.of(
        "/api/users_GET", role -> role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("COORDINADOR"),
        "/api/users_MODIFY", role -> role.equalsIgnoreCase("ADMIN"),
        "/api/materias_GET", role -> role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("COORDINADOR"),
        "/api/materias_MODIFY", role -> role.equalsIgnoreCase("COORDINADOR"),
        "/api/pensums_GET", role -> role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("COORDINADOR"),
        "/api/pensums_MODIFY", role -> role.equalsIgnoreCase("COORDINADOR"),
	"/api/permisos_ALL", role -> role.equalsIgnoreCase("ADMIN")
    );

    private boolean matchesAnyPath(String path, String... prefixes) {
        for (String prefix : prefixes) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT faltante o invalido");
            return;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT invalido o expirado");
            return;
        }

        String method = request.getMethod();
        String role = JwtUtil.getRoleFromToken(token);
        String path = request.getRequestURI();

        // Lógica de autorización basada en ruta y método
        if (path.equals("/api/users/me") && method.equalsIgnoreCase("GET")) {
            filterChain.doFilter(request, response);
            return;
        } else if (path.startsWith("/api/users")) {
            String key = method.equalsIgnoreCase("GET") ? "/api/users_GET" : "/api/users_MODIFY";
            if (!accessRules.get(key).test(role)) {
                String msg = method.equalsIgnoreCase("GET")
                    ? "Acceso denegado: Se requiere rol ADMIN o COORDINADOR para consultar usuarios"
                    : "Acceso denegado: Se requiere rol ADMIN para modificar usuarios";
                sendError(response, HttpServletResponse.SC_FORBIDDEN, msg);
                return;
            }
        } else if (matchesAnyPath(path, "/api/materias")) {
            String key = method.equalsIgnoreCase("GET") ? "/api/materias_GET" : "/api/materias_MODIFY";
            if (!accessRules.get(key).test(role)) {
                String msg = method.equalsIgnoreCase("GET")
                        ? "Acceso denegado: Se requiere rol ADMIN para ver materias"
                        : "Acceso denegado: Se requiere rol COORDINADOR para modificar materias";
                sendError(response, HttpServletResponse.SC_FORBIDDEN, msg);
                return;
            }
        } else if (path.startsWith("/api/pensums")) {
	    String key = method.equalsIgnoreCase("GET") ? "/api/pensums_GET" : "/api/pensums_MODIFY";
	    if (!accessRules.get(key).test(role)) {
                String msg = method.equalsIgnoreCase("GET")
			? "Acceso denegado: Se requiere rol ADMIN para ver pensums"
			: "Acceso denegado: Se requiere rol COORDINADOR para modificar pensums";
		sendError(response, HttpServletResponse.SC_FORBIDDEN, msg);
		return;
            }
        } else if (path.startsWith("/api/permisos")) {
	    if (!accessRules.get("/api/permisos_ALL").test(role)) {
		sendError(response, HttpServletResponse.SC_FORBIDDEN,
			"Acceso denegado: Se requiere rol ADMIN para gestionar permisos");
		return;
	    }
	}

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth");
    }
}

