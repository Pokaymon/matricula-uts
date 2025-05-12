package uts.mi.matricula.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token JWT faltante o invalido");
            return;
        }

        String token = authHeader.substring(7);

        if (!JwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token JWT inválido o expirado");
            return;
        }

        String method = request.getMethod();
	String role = JwtUtil.getRoleFromToken(token);
	String path = request.getRequestURI();

	if (path.startsWith("/api/users")) {
            // Si el método no es GET, validar rol ADMIN
            if (!method.equalsIgnoreCase("GET")) {
                if (!"ADMIN".equalsIgnoreCase(role)) {
                   response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                   response.getWriter().write("Acceso denegado: Se requiere rol ADMIN para esta operacion");
                   return;
                }
            }
	}

	// Reglas para /api/materias/
	if (path.startsWith("/api/materias")){
	    if (method.equalsIgnoreCase("GET")) {
	        if (!"ADMIN".equalsIgnoreCase(role)) {
		    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		    response.getWriter().write("Acceso denegado: Se requiere rol ADMIN para ver materias");
		    return;
                }
	    } else {
		if (!"COORDINADOR".equalsIgnoreCase(role)) {
		    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		    response.getWriter().write("Acceso denegado: Se requiere rol COORDINADOR para modificar materias");
		    return;
		}
	    }
	}

        // Continúa la cadena del filtro si el token es válido
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return path.startsWith("/auth"); // No filtrar /auth/*
    }
}

