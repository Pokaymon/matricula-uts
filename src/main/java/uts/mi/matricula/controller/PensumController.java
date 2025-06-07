package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.service.PensumService;

import uts.mi.matricula.dto.PensumDTO;

import java.util.*;

@RestController
@RequestMapping("/api/pensums")
public class PensumController {

    @Autowired
    private PensumService pensumService;

    @PostMapping
    public Pensum crear(@RequestBody Pensum pensum) {
        return pensumService.crearPensum(pensum);
    }

    @GetMapping
    public List<Pensum> listar() {
        return pensumService.obtenerTodos();
    }

    @GetMapping("/{codigo}")
    public Pensum obtenerPorCodigo(@PathVariable String codigo) {
        return pensumService.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Pensum no encontrado"));
    }

    @GetMapping("/{codigo}/materias")
    public List<Materia> materiasDePensum(@PathVariable String codigo) {
        return pensumService.obtenerMateriasDePensum(codigo);
    }

    @PutMapping("/{id}")
    public Pensum actualizar(@PathVariable String id, @RequestBody Pensum pensum) {
        return pensumService.actualizarPensum(id, pensum);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pensum> actualizarEstado(@PathVariable String id, @RequestBody Map<String, Boolean> estadoRequest) {
        if (!estadoRequest.containsKey("activo")) {
            return ResponseEntity.badRequest().build();
        }

        boolean nuevoEstado = estadoRequest.get("activo");

        try {
            Pensum actualizado = pensumService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        pensumService.eliminarPensum(id);
    }
}
