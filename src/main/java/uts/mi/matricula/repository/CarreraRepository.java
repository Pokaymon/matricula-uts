package uts.mi.matricula.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Carrera;

public interface CarreraRepository extends MongoRepository<Carrera, String> {
    Optional<Carrera> findByCod(String cod);
    Optional<Carrera> findByNombre(String nombre);
    boolean existsByCod(String cod);
    boolean existsByNombre(String nombre);
}

