package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Grupo;

import java.util.Optional;

public interface GrupoRepository extends MongoRepository<Grupo, String> {
    Optional<Grupo> findByCodigo(String codigo);
}

