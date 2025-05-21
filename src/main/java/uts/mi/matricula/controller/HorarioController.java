package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Horario;
import uts.mi.matricula.service.HorarioService;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @PostMapping
    public ResponseEntity<Horario> crear(@RequestBody Horario horario) {
        Horario creado = horarioService.crearHorario(horario);
        return ResponseEntity.status(201).body(creado);
    }

    @GetMapping
    public List<Horario> listar() {
        return horarioService.listarHorarios();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }
}

