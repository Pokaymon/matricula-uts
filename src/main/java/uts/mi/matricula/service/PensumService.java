package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.CarreraRepository;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.repository.PensumRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PensumService {

    @Autowired
    private PensumRepository pensumRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    public Pensum crearPensum(Pensum pensum) {
        if (pensumRepository.existsByCodigo(pensum.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un pensum con ese código.");
        }

        Carrera carrera = validarYObtenerCarrera(pensum.getCarrera());
        if (carrera.getPensum() != null) {
            throw new IllegalStateException("Esta carrera ya tiene un pensum asignado.");
        }

        if (pensum.isActivo()) {
            validarUnicoActivo(carrera.getId(), null);
        }

        validarMaterias(pensum.getMaterias());

        Pensum guardado = pensumRepository.save(pensum);
        vincularCarreraConPensum(carrera, guardado);
        return guardado;
    }

    public List<Pensum> obtenerTodos() {
        return pensumRepository.findAll();
    }

    public Optional<Pensum> obtenerPorCodigo(String codigo) {
        return pensumRepository.findByCodigo(codigo);
    }

    public List<Materia> obtenerMateriasDePensum(String codigoPensum) {
        Pensum pensum = pensumRepository.findByCodigo(codigoPensum)
                .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        return pensum.getMaterias().stream()
                .map(codigo -> materiaRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new NoSuchElementException("Materia no encontrada: " + codigo)))
                .collect(Collectors.toList());
    }

    public Pensum actualizarPensum(String id, Pensum actualizado) {
        Pensum existente = pensumRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        if (!existente.getCodigo().equals(actualizado.getCodigo()) &&
                pensumRepository.existsByCodigo(actualizado.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un pensum con ese código.");
        }

        Carrera nuevaCarrera = validarYObtenerCarrera(actualizado.getCarrera());
        validarUnicoPensumPorCarrera(nuevaCarrera.getId(), id);

        if (actualizado.isActivo()) {
            validarUnicoActivo(nuevaCarrera.getId(), id);
        }

        validarMaterias(actualizado.getMaterias());
        vincularCarreraConPensum(nuevaCarrera, actualizado);

        existente.setCarrera(nuevaCarrera);
        existente.setCodigo(actualizado.getCodigo());
        existente.setFechaInicio(actualizado.getFechaInicio());
        existente.setActivo(actualizado.isActivo());
        existente.setMaterias(actualizado.getMaterias());

        return pensumRepository.save(existente);
    }

    public Pensum actualizarEstado(String id, boolean nuevoEstado) {
        Pensum existente = pensumRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        if (nuevoEstado) {
            validarUnicoActivo(existente.getCarrera().getId(), id);
        }

        existente.setActivo(nuevoEstado);
        return pensumRepository.save(existente);
    }

    public void eliminarPensum(String id) {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        Carrera carrera = pensum.getCarrera();
        if (carrera != null) {
            carrera.setPensum(null);
            carreraRepository.save(carrera);
        }

        pensumRepository.deleteById(id);
    }

    // ==== MÉTODOS PRIVADOS AUXILIARES ====

    private void validarMaterias(List<String> codigos) {
        for (String codigo : codigos) {
            if (!materiaRepository.existsByCodigo(codigo)) {
                throw new IllegalArgumentException("Materia no encontrada: " + codigo);
            }
        }
    }

    private Carrera validarYObtenerCarrera(Carrera carreraRef) {
        Objects.requireNonNull(carreraRef, "Carrera requerida.");
        Objects.requireNonNull(carreraRef.getId(), "ID de carrera requerido.");
        return carreraRepository.findById(carreraRef.getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada."));
    }

    private void validarUnicoActivo(String carreraId, String idActual) {
        Optional<Pensum> activo = pensumRepository.findByCarrera_IdAndActivoTrue(carreraId);
        if (activo.isPresent() && (idActual == null || !activo.get().getId().equals(idActual))) {
            throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
        }
    }

    private void validarUnicoPensumPorCarrera(String carreraId, String idActual) {
        Optional<Pensum> otro = pensumRepository.findByCarrera_Id(carreraId);
        if (otro.isPresent() && !otro.get().getId().equals(idActual)) {
            throw new IllegalStateException("Esta carrera ya tiene un pensum asignado.");
        }
    }

    private void vincularCarreraConPensum(Carrera carrera, Pensum pensum) {
        carrera.setPensum(pensum);
        carreraRepository.save(carrera);
    }
}

