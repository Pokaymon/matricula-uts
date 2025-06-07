package uts.mi.matricula.controller.viewControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.service.CarreraService;

@Controller
@RequestMapping("/carreras")
public class CarreraViewController {

    @Autowired
    private CarreraService carreraService;

    // Vista de todas las carreras
    @GetMapping
    public String listarCarreras(Model model) {
        model.addAttribute("carreras", carreraService.obtenerCarreras());
        return "/coordinator/carreras/carreras";  // Vista Thymeleaf para listar carreras
    }

    // Vista para crear una nueva carrera
    @GetMapping("/nueva")
    public String crearCarreraForm(Model model) {
        model.addAttribute("carrera", new Carrera());
        return "/coordinator/carreras/crear-carrera";  // Vista Thymeleaf para crear carrera
    }

    // Procesar la creación de una nueva carrera
    @PostMapping("/guardar")
    public String guardarCarrera(@ModelAttribute("carrera") Carrera carrera, Model model) {
        try {
            carreraService.crearCarrera(carrera);
            return "redirect:/carreras";
        } catch (Exception e) {
            model.addAttribute("carrera", carrera);
            model.addAttribute("error", e.getMessage());
            return "/coordinator/carreras/crear-carrera";
        }
    }

    // Vista para editar una carrera existente
    @GetMapping("/editar/{id}")
    public String editarCarreraForm(@PathVariable String id, Model model) {
        Carrera carrera = carreraService.obtenerCarreras()
            .stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        model.addAttribute("carrera", carrera);
        return "/coordinator/carreras/editar-carrera";  // Vista Thymeleaf para editar carrera
    }

    // Procesar la actualización de una carrera
    @PostMapping("/actualizar")
    public String actualizarCarrera(@ModelAttribute("carrera") Carrera carrera, Model model) {
        try {
            carreraService.actualizarCarrera(carrera.getId(), carrera);
            return "redirect:/carreras";
        } catch (Exception e) {
            model.addAttribute("carrera", carrera);
            model.addAttribute("error", e.getMessage());
            return "/coordinator/carreras/editar-carrera";
        }
    }

    // Eliminar una carrera
    @GetMapping("/eliminar/{id}")
    public String eliminarCarrera(@PathVariable String id) {
        carreraService.eliminarCarrera(id);
        return "redirect:/carreras";
    }
}

