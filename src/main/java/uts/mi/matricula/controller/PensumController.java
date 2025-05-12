package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.service.PensumService;

import java.util.List;

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

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        pensumService.eliminarPensum(id);
    }
}

