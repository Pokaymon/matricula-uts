package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Materia;

import java.util.Optional;

public interface MateriaRepository extends MongoRepository<Materia, String> {
    Optional<Materia> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByNombre(String nombre);
    Optional<Materia> findByNombre(String nombre);
}
