package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uts.mi.matricula.model.Grupo;
import uts.mi.matricula.model.Horario;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.User;
import uts.mi.matricula.repository.GrupoRepository;
import uts.mi.matricula.repository.HorarioRepository;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public void limpiarGruposDeProfesor(String cedulaProfesor) {
        List<Grupo> grupos = grupoRepository.findByProfesorId(cedulaProfesor);
	for (Grupo grupo : grupos) {
	    grupo.setProfesorId(null);
	    grupoRepository.save(grupo);
	}
    }

    private void validarProfesorPorCedula(String cedula) {
	if (cedula == null || cedula.isEmpty()) {
	    throw new RuntimeException("Debe especificar la cédula del profesor.");
	}
	User user = userRepository.findByCedula(cedula)
	    .orElseThrow(() -> new RuntimeException("No existe un usuario con la cédula ingresada."));
	if (!"PROFESOR".equalsIgnoreCase(user.getRol())) {
	     throw new RuntimeException("El usuario con la cédula ingresada no tiene rol de PROFESOR.");
	}
    }

    public Grupo crearGrupo(Grupo grupo) {
        if (grupoRepository.findByCodigo(grupo.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un grupo con ese código.");
        }

        Optional<Materia> materia = materiaRepository.findByCodigo(grupo.getCodMateria());
        if (materia.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe la materia con código: " + grupo.getCodMateria());
        }

        validarProfesorPorCedula(grupo.getProfesorId()); // Validamos antes

        List<Horario> horariosValidados = new ArrayList<>();
        for (Horario h : grupo.getHorarios()) {
            h.setCodGrupo(grupo.getCodigo());
            horariosValidados.add(validarYGuardarHorario(h, grupo)); // ¡aquí pasas el grupo!
        }

        grupo.setHorarios(horariosValidados);
        return grupoRepository.save(grupo);
    }

    private Horario validarYGuardarHorario(Horario horario, Grupo grupo) {
        if (horarioRepository.findByCodigo(horario.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de horario duplicado: " + horario.getCodigo());
        }

	validarProfesorPorCedula(grupo.getProfesorId());

        // Validaciones de cruce y duración ya están en el servicio de Horario
        return horarioService.crearHorario(horario);
    }

    public List<Grupo> listarGrupos() {
        return grupoRepository.findAll();
    }

    public void eliminarGrupo(String id) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(id);
	if (grupoOpt.isEmpty()) return;

	Grupo grupo = grupoOpt.get();

	// Eliminar todos los horarios asociados al grupo
	List<Horario> horariosDelGrupo = horarioRepository.findByCodGrupo(grupo.getCodigo());
	for (Horario horario : horariosDelGrupo) {
	    horarioRepository.deleteById(horario.getId());
	}

	// Eliminar el grupo
	grupoRepository.deleteById(id);
    }
}

