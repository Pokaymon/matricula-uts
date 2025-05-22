package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Grupo;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.service.GrupoService;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.dto.GrupoDTO;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private MateriaRepository materiaRepository;

    @PostMapping
    public ResponseEntity<Grupo> crear(@RequestBody Grupo grupo) {
        Grupo creado = grupoService.crearGrupo(grupo);
        return ResponseEntity.status(201).body(creado);
    }

    @GetMapping
    public List<Grupo> listar() {
        return grupoService.listarGrupos();
    }

    @GetMapping("/vista")
    public List<GrupoDTO> listarConMateria() {
	List<Grupo> grupos = grupoService.listarGrupos();
	return grupos.stream().map(grupo -> {
	    String nombreMateria = materiaRepository.findByCodigo(grupo.getCodMateria())
		.map(Materia::getNombre)
		.orElse("Materia no encontrada");
	    return new GrupoDTO(grupo.getCodigo(), nombreMateria);
	}).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupo> obtenerPorId(@PathVariable String id) {
        Grupo grupo = grupoService.obtenerPorId(id);
        return ResponseEntity.ok(grupo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupo> actualizar(@PathVariable String id, @RequestBody Grupo grupo) {
        Grupo actualizado = grupoService.actualizarGrupo(id, grupo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        grupoService.eliminarGrupo(id);
        return ResponseEntity.noContent().build();
    }
}

