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

    public Materia createMateria(Materia materia) throws Exception {
        if (materiaRepository.existsByCodigo(materia.getCodigo())) {
            throw new Exception("Ya existe una materia con el código: " + materia.getCodigo());
        }
        return materiaRepository.save(materia);
    }

    public Materia updateMateria(String id, Materia materia) throws Exception {
        Optional<Materia> existente = materiaRepository.findById(id);
        if (existente.isEmpty()) {
            throw new Exception("La materia no existe.");
        }

        Materia actual = existente.get();

        if (!actual.getCodigo().equals(materia.getCodigo()) && materiaRepository.existsByCodigo(materia.getCodigo())) {
            throw new Exception("Ya existe otra materia con el código: " + materia.getCodigo());
        }

        materia.setId(id);
        return materiaRepository.save(materia);
    }

    public void deleteMateria(String id) {
        materiaRepository.deleteById(id);
    }
}

