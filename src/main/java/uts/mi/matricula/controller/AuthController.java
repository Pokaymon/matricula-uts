package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.dto.LoginRequest;
import uts.mi.matricula.dto.LoginResponse;
import uts.mi.matricula.model.User;
import uts.mi.matricula.repository.UserRepository;
import uts.mi.matricula.util.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getRol());
        return ResponseEntity.ok(new LoginResponse(token, "Login exitoso"));
    }
}

