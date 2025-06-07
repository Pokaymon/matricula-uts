package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Pensum;

import java.util.List;
import java.util.Optional;

public interface PensumRepository extends MongoRepository<Pensum, String> {
    Optional<Pensum> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    Optional<Pensum> findByCarrera_IdAndActivoTrue(String carreraId);
    List<Pensum> findByCarrera_Id(String carreraId);
}
