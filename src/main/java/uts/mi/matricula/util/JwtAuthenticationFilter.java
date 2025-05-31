package uts.mi.matricula.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import uts.mi.matricula.security.EndpointAccessRule;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT faltante o inválido");
            return;
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.isTokenValid(token)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
            return;
        }

        String role = JwtUtil.getRoleFromToken(token);
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/auth") || path.equals("/api/users/me")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!EndpointAccessRule.isAuthorized(path, method, role)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: Rol no autorizado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return false; // se valida internamente por URI específica
    }
}

