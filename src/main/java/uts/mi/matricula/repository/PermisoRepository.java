package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Permiso;

import java.util.Optional;

public interface PermisoRepository extends MongoRepository<Permiso, String> {
    Optional<Permiso> findByCodigo(String codigo);
    Optional<Permiso> findByNombre(String nombre);
    boolean existsByCodigo(String codigo);
    boolean existsByNombre(String nombre);
}

