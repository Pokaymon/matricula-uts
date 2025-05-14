package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.repository.MateriaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    public List<Materia> getAllMaterias() {
        return materiaRepository.findAll();
    }

    public Optional<Materia> getMateriaById(String id) {
        return materiaRepository.findById(id);
    }

    public Materia createMateria(Materia materia) {
        return materiaRepository.save(materia);
    }

    public Materia updateMateria(String id, Materia updatedMateria) {
        return materiaRepository.findById(id).map(materia -> {
            materia.setCodigo(updatedMateria.getCodigo());
            materia.setNombre(updatedMateria.getNombre());
            materia.setCreditos(updatedMateria.getCreditos());
            materia.setSemestre(updatedMateria.getSemestre());
            materia.setTipo(updatedMateria.getTipo());
            materia.setPrerequisitos(updatedMateria.getPrerequisitos());
            materia.setDescripcion(updatedMateria.getDescripcion());
            materia.setProfesorId(updatedMateria.getProfesorId()); // <- aquí
            return materiaRepository.save(materia);
        }).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
    }

    public void deleteMateria(String id) {
        materiaRepository.deleteById(id);
    }
}
