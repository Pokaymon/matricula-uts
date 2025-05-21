package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Grupo;
import uts.mi.matricula.service.GrupoService;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @PostMapping
    public ResponseEntity<Grupo> crear(@RequestBody Grupo grupo) {
        Grupo creado = grupoService.crearGrupo(grupo);
        return ResponseEntity.status(201).body(creado);
    }

    @GetMapping
    public List<Grupo> listar() {
        return grupoService.listarGrupos();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        grupoService.eliminarGrupo(id);
        return ResponseEntity.noContent().build();
    }
}

