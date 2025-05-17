package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.PensumRepository;
import uts.mi.matricula.model.User;
import uts.mi.matricula.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private PensumRepository pensumRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Materia> getAllMaterias() {
        return materiaRepository.findAll();
    }

    public Optional<Materia> getMateriaById(String id) {
        return materiaRepository.findById(id);
    }

    public void limpiarMateriasDeProfesor(String cedulaProfesor) {
        List<Materia> materias = materiaRepository.findByProfesorId(cedulaProfesor);
        for (Materia materia : materias) {
            materia.setProfesorId(null);
            materiaRepository.save(materia);
        }
    }

    private void validarProfesorPorCedula(String cedula) {
	if (cedula == null || cedula.isEmpty()) {
	    throw new RuntimeException("Debe especificar la cédula del profesor.");
	}
	User user = userRepository.findByCedula(cedula)
	    .orElseThrow(() -> new RuntimeException("No existe un usuario con la cédula ingresada."));
	if (!"PROFESOR".equalsIgnoreCase(user.getRol())) {
	     throw new RuntimeException("El usuario con la cédula ingresada no tiene rol de PROFESOR.");
	}
    }

    public Materia createMateria(Materia materia) {
        if (materiaRepository.existsByCodigo(materia.getCodigo())) {
	    throw new RuntimeException("Ya existe una materia con ese código.");
	}
	if (materiaRepository.existsByNombre(materia.getNombre())) {
	    throw new RuntimeException("Ya existe una materia con ese nombre.");
	}
	validarProfesorPorCedula(materia.getProfesorId());
	return materiaRepository.save(materia);
    }

    public Materia updateMateria(String id, Materia updatedMateria) {
        return materiaRepository.findById(id).map(materia -> {

	    if (!materia.getCodigo().equals(updatedMateria.getCodigo())
		&& materiaRepository.existsByCodigo(updatedMateria.getCodigo())) {
	      throw new RuntimeException("Ya existe otra materia con ese código.");
	    }

	    if (!materia.getNombre().equals(updatedMateria.getNombre())
		&& materiaRepository.existsByNombre(updatedMateria.getNombre())) {
	      throw new RuntimeException("Ya existe otra materia con ese nombre.");
	    }

	    validarProfesorPorCedula(updatedMateria.getProfesorId());

            materia.setCodigo(updatedMateria.getCodigo());
            materia.setNombre(updatedMateria.getNombre());
            materia.setCreditos(updatedMateria.getCreditos());
            materia.setSemestre(updatedMateria.getSemestre());
            materia.setTipo(updatedMateria.getTipo());
            materia.setPrerequisitos(updatedMateria.getPrerequisitos());
            materia.setDescripcion(updatedMateria.getDescripcion());
            materia.setProfesorId(updatedMateria.getProfesorId());
            return materiaRepository.save(materia);
        }).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }

    public void deleteMateria(String id) {
        // Buscar la materia primero
        Materia materia = materiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        String codigo = materia.getCodigo(); // ← lo que guarda el Pensum

        // Eliminar el código de materia en todos los pensums
        List<Pensum> pensums = pensumRepository.findAll();
        for (Pensum pensum : pensums) {
            boolean removed = pensum.getMaterias().removeIf(c -> c.equals(codigo));
            if (removed) {
                pensumRepository.save(pensum);
            }
        }

        // Ahora sí, elimina la materia
        materiaRepository.deleteById(id);
    }
}
