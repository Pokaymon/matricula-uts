package uts.mi.matricula.controller.viewControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.service.MateriaService;
import uts.mi.matricula.repository.UserRepository;

@Controller
@RequestMapping("/materias")
public class MateriaViewController {

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private UserRepository userRepository;

    // Vista de todas las materias
    @GetMapping
    public String listarMaterias(Model model) {
        model.addAttribute("materias", materiaService.getAllMaterias());
        return "/coordinator/materias/materias";  // Nombre de la vista Thymeleaf para listar materias
    }

    // Vista para crear una nueva materia
    @GetMapping("/nueva")
    public String crearMateriaForm(Model model) {
        model.addAttribute("materia", new Materia());  // Crear un objeto vacío para el formulario
	model.addAttribute("materiasExistentes", materiaService.getAllMaterias());
        return "/coordinator/materias/crear-materia";  // Nombre de la vista Thymeleaf para crear materia
    }

    // Procesar la creación de una nueva materia
    @PostMapping("/guardar")
    public String guardarMateria(@ModelAttribute("materia") Materia materia) {
        materiaService.createMateria(materia);  // Guardar la materia en la base de datos
        return "redirect:/materias";  // Redirigir al listado de materias después de guardar
    }

    // Vista para editar una materia existente
    @GetMapping("/editar/{id}")
    public String editarMateriaForm(@PathVariable String id, Model model) {
        Materia materia = materiaService.getMateriaById(id)
	    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

	model.addAttribute("materia", materia);
	model.addAttribute("materiasExistentes", materiaService.getAllMaterias());

        return "/coordinator/materias/editar-materia";  // Nombre de la vista Thymeleaf para editar materia
    }

    // Procesar la actualización de una materia
    @PostMapping("/actualizar")
    public String actualizarMateria(@ModelAttribute("materia") Materia materia) {
        materiaService.updateMateria(materia.getId(), materia);  // Actualizar la materia
        return "redirect:/materias";  // Redirigir al listado de materias después de actualizar
    }

    // Eliminar una materia
    @GetMapping("/eliminar/{id}")
    public String eliminarMateria(@PathVariable String id) {
        materiaService.deleteMateria(id);  // Eliminar la materia
        return "redirect:/materias";  // Redirigir al listado de materias después de eliminar
    }
}
