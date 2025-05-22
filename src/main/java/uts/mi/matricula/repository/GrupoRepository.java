package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Grupo;

import java.util.Optional;
import java.util.List;

public interface GrupoRepository extends MongoRepository<Grupo, String> {
    Optional<Grupo> findByCodigo(String codigo);
    List<Grupo> findByProfesorId(String profesorId);
}

