package uts.mi.matricula.security;

import java.util.List;

public enum EndpointAccessRule {

    USERS_GET("/api/users", "GET", List.of("ADMIN", "COORDINADOR")),
    USERS_MODIFY("/api/users", "*", List.of("ADMIN")),
    MATERIAS_GET("/api/materias", "GET", List.of("ADMIN", "COORDINADOR")),
    MATERIAS_MODIFY("/api/materias", "*", List.of("COORDINADOR")),
    PENSUMS_GET("/api/pensums", "GET", List.of("ADMIN", "COORDINADOR", "AUDITOR")),
    PENSUMS_MODIFY("/api/pensums", "*", List.of("COORDINADOR", "AUDITOR")),
    PERMISOS_ALL("/api/permisos", "*", List.of("ADMIN"));

    private final String pathPrefix;
    private final String method; // Puede ser GET, POST, *, etc.
    private final List<String> allowedRoles;

    EndpointAccessRule(String pathPrefix, String method, List<String> allowedRoles) {
        this.pathPrefix = pathPrefix;
        this.method = method;
        this.allowedRoles = allowedRoles;
    }

    public boolean matches(String uri, String httpMethod, String userRole) {
        return uri.startsWith(pathPrefix)
            && (method.equals("*") || method.equalsIgnoreCase(httpMethod))
            && allowedRoles.stream().anyMatch(r -> r.equalsIgnoreCase(userRole));
    }

    public static boolean isAuthorized(String uri, String method, String role) {
        for (EndpointAccessRule rule : values()) {
            if (rule.matches(uri, method, role)) {
                return true;
            }
        }
        return false;
    }
}
