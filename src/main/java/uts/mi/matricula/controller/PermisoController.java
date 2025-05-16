package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Permiso;
import uts.mi.matricula.service.PermisoService;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    @Autowired
    private PermisoService permisoService;

    @GetMapping
    public ResponseEntity<List<Permiso>> listarPermisos() {
        return ResponseEntity.ok(permisoService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<?> crearPermiso(@RequestBody Permiso permiso) {
        try {
            return ResponseEntity.ok(permisoService.crear(permiso));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPermiso(@PathVariable String id, @RequestBody Permiso permiso) {
        try {
            permiso.setId(id);
            return ResponseEntity.ok(permisoService.actualizar(permiso));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPermiso(@PathVariable String id) {
        permisoService.eliminar(id);
        return ResponseEntity.ok("Permiso eliminado");
    }
}

