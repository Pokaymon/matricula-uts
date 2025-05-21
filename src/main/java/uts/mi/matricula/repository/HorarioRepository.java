package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uts.mi.matricula.model.Horario;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends MongoRepository<Horario, String> {
    Optional<Horario> findByCodigo(String codigo);
    List<Horario> findByCodGrupo(String codGrupo);
    List<Horario> findByDia(DayOfWeek dia);
}

