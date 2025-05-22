package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uts.mi.matricula.model.Horario;
import uts.mi.matricula.model.Grupo;
import uts.mi.matricula.repository.HorarioRepository;
import uts.mi.matricula.repository.GrupoRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    public Horario crearHorario(Horario horario) {
        if (horarioRepository.findByCodigo(horario.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un horario con ese código.");
        }

        validarDuracionClase(horario);
        verificarCruceHorario(horario);

        return horarioRepository.save(horario);
    }

    private void validarDuracionClase(Horario nuevo) {
        List<Horario> existentes = horarioRepository.findByCodGrupo(nuevo.getCodGrupo());
        Duration duracion = Duration.between(nuevo.getHoraInicio(), nuevo.getHoraFin());

        if (existentes.size() >= 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un grupo no puede tener más de 2 clases por semana.");
        }

        if (existentes.size() == 1) {
            // Segunda clase: cada una debe durar 1h30min
            if (!duracion.equals(Duration.ofMinutes(90))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Si el grupo tiene dos clases semanales, cada clase debe durar 1h 30min.");
            }
        } else {
            // Primera clase: puede ser 3h
            if (!duracion.equals(Duration.ofMinutes(90)) && !duracion.equals(Duration.ofMinutes(180))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Una clase semanal debe durar exactamente 3h o 1h 30min.");
            }
        }
    }

    private void verificarCruceHorario(Horario nuevo) {
        List<Horario> horariosEnEseDia = horarioRepository.findByDia(nuevo.getDia());

        for (Horario existente : horariosEnEseDia) {
            if (nuevo.getHoraInicio().isBefore(existente.getHoraFin()) &&
                nuevo.getHoraFin().isAfter(existente.getHoraInicio())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cruce de horario con el grupo: " + existente.getCodGrupo()
                );
            }
        }
    }

    public List<Horario> listarHorarios() {
        return horarioRepository.findAll();
    }

    public void eliminarHorario(String id) {
        Optional<Horario> optionalHorario = horarioRepository.findById(id);
	if (optionalHorario.isEmpty()) return;

	Horario horario = optionalHorario.get();

	// Eliminar Horario
	horarioRepository.deleteById(id);

	// Eliminar Grupo asociado
	Grupo grupo = grupoRepository.findByCodigo(horario.getCodGrupo()).orElse(null);
	if (grupo != null) {
	    grupo.getHorarios().removeIf(h -> h.getCodigo().equals(horario.getCodigo()));
	    grupoRepository.save(grupo);
	}
    }
}

