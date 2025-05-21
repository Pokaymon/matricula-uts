package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uts.mi.matricula.model.Grupo;
import uts.mi.matricula.model.Horario;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.repository.GrupoRepository;
import uts.mi.matricula.repository.HorarioRepository;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.service.HorarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private HorarioService horarioService;

    public Grupo crearGrupo(Grupo grupo) {
        if (grupoRepository.findByCodigo(grupo.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un grupo con ese código.");
        }

        Optional<Materia> materia = materiaRepository.findByCodigo(grupo.getCodMateria());
        if (materia.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe la materia con código: " + grupo.getCodMateria());
        }

        // Validar y guardar horarios
        List<Horario> horariosValidados = new ArrayList<>();
        for (Horario h : grupo.getHorarios()) {
            h.setCodGrupo(grupo.getCodigo());
            horariosValidados.add(validarYGuardarHorario(h));
        }

        grupo.setHorarios(horariosValidados);
        return grupoRepository.save(grupo);
    }

    private Horario validarYGuardarHorario(Horario horario) {
        if (horarioRepository.findByCodigo(horario.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de horario duplicado: " + horario.getCodigo());
        }

        // Validaciones de cruce y duración ya están en el servicio de Horario
        return horarioService.crearHorario(horario); // ¡IMPORTANTE! Evitar usar `new` directamente si no es bean.
    }

    public List<Grupo> listarGrupos() {
        return grupoRepository.findAll();
    }

    public void eliminarGrupo(String id) {
        grupoRepository.deleteById(id);
    }
}

