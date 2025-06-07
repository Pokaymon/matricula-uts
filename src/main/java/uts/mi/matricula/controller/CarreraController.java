package uts.mi.matricula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.service.CarreraService;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "*")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @PostMapping
    public Carrera crearCarrera(@RequestBody Carrera carrera) throws Exception {
        return carreraService.crearCarrera(carrera);
    }

    @GetMapping
    public List<Carrera> obtenerCarreras() {
        return carreraService.obtenerCarreras();
    }

    @GetMapping("/{cod}")
    public Carrera obtenerCarreraPorCodigo(@PathVariable String cod) throws Exception {
        return carreraService.obtenerPorCodigo(cod)
                .orElseThrow(() -> new Exception("Carrera no encontrada"));
    }

    @PutMapping("/{id}")
    public Carrera actualizarCarrera(@PathVariable String id, @RequestBody Carrera carrera) throws Exception {
        return carreraService.actualizarCarrera(id, carrera);
    }

    @DeleteMapping("/{id}")
    public void eliminarCarrera(@PathVariable String id) {
        carreraService.eliminarCarrera(id);
    }
}

