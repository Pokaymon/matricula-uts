package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.service.CarreraService;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "*")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @PostMapping
    public ResponseEntity<?> crearCarrera(@RequestBody Carrera carrera) {
        try {
            Carrera creada = carreraService.crearCarrera(carrera);
            return ResponseEntity.ok(creada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Carrera> obtenerCarreras() {
        return carreraService.obtenerCarreras();
    }

    @GetMapping("/{cod}")
    public ResponseEntity<?> obtenerCarreraPorCodigo(@PathVariable String cod) {
        try {
            Carrera carrera = carreraService.obtenerPorCodigo(cod)
                    .orElseThrow(() -> new Exception("Carrera no encontrada"));
            return ResponseEntity.ok(carrera);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable String id, @RequestBody Carrera carrera) {
        try {
            Carrera actualizada = carreraService.actualizarCarrera(id, carrera);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCarrera(@PathVariable String id) {
        carreraService.eliminarCarrera(id);
        return ResponseEntity.ok().build();
    }
}
