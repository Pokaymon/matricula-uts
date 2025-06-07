package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.User;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.service.UserService;
import uts.mi.matricula.repository.UserRepository;
import uts.mi.matricula.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profesores")
    public ResponseEntity<List<User>> getProfesores() {
        return ResponseEntity.ok(userService.getProfesores());
    }

    @GetMapping("/estudiantes")
    public ResponseEntity<List<User>> getEstudiantes() {
        return ResponseEntity.ok(userService.getEstudiantes());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profesores/{cedula}")
    public ResponseEntity<?> obtenerProfesorPorCedula(@PathVariable String cedula) {
	return userRepository.findByCedula(cedula)
	    .filter(user -> "PROFESOR".equalsIgnoreCase(user.getRol()))
	    .map(ResponseEntity::ok)
	    .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.createUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token JWT faltante o invalido");
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token JWT invalido o expirado");
        }

        String username = JwtUtil.getUsernameFromToken(token);
            Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    // PATCH para asignar o cambiar pensum solo a estudiantes
    @PatchMapping("/{userId}/pensum/{pensumId}")
    public ResponseEntity<?> actualizarPensum(
            @PathVariable String userId,
            @PathVariable String pensumId) {
        try {
            User updatedUser = userService.actualizarPensumDeEstudiante(userId, pensumId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET para obtener el pensum completo al que pertenece un usuario
    @GetMapping("/{userId}/pensum")
    public ResponseEntity<?> obtenerPensumUsuario(@PathVariable String userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Pensum pensum = user.getPensum();

        if (pensum == null) {
            return ResponseEntity.ok("No tiene un pensum asignado");
        } else {
            return ResponseEntity.ok(pensum);
        }
    }
}

