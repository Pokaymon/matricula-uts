package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.repository.PensumRepository;
import uts.mi.matricula.repository.CarreraRepository;

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

        // Validar carrera no nula y existente
        if (pensum.getCarrera() == null || pensum.getCarrera().getId() == null) {
            throw new IllegalArgumentException("Carrera requerida.");
        }

        Carrera carrera = carreraRepository.findById(pensum.getCarrera().getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada."));

        // Validar que la carrera no tenga ya un pensum
        if (carrera.getPensum() != null) {
            throw new IllegalStateException("Esta carrera ya tiene un pensum asignado.");
        }

        // Validar pensum activo único
        if (pensum.isActivo()) {
            Optional<Pensum> activoExistente = pensumRepository.findByCarrera_IdAndActivoTrue(carrera.getId());
            if (activoExistente.isPresent()) {
                throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
            }
        }

        // Validar materias
        for (String codigo : pensum.getMaterias()) {
            if (!materiaRepository.existsByCodigo(codigo)) {
                throw new IllegalArgumentException("Materia no encontrada: " + codigo);
            }
        }

        Pensum guardado = pensumRepository.save(pensum);

        // Relacionar la carrera con el pensum
        carrera.setPensum(guardado);
        carreraRepository.save(carrera);

        return guardado;
    }

    public List<Pensum> obtenerTodos() {
        return pensumRepository.findAll();
    }

    public Optional<Pensum> obtenerPorCodigo(String codigo) {
        return pensumRepository.findByCodigo(codigo);
    }

    public List<Materia> obtenerMateriasDePensum(String codigoPensum) {
        Optional<Pensum> pensum = pensumRepository.findByCodigo(codigoPensum);
        if (pensum.isEmpty()) throw new NoSuchElementException("Pensum no encontrado.");

        return pensum.get().getMaterias().stream()
                .map(codigo -> materiaRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new NoSuchElementException("Materia no encontrada: " + codigo)))
                .collect(Collectors.toList());
    }

public Pensum actualizarPensum(String id, Pensum actualizado) {
    Pensum existente = pensumRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

    // Validar código
    if (!existente.getCodigo().equals(actualizado.getCodigo()) &&
            pensumRepository.existsByCodigo(actualizado.getCodigo())) {
        throw new IllegalArgumentException("Ya existe un pensum con ese código.");
    }

    // Validar carrera
    Carrera nuevaCarrera = carreraRepository.findById(actualizado.getCarrera().getId())
            .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada."));

    Optional<Pensum> pensumExistenteEnCarrera = pensumRepository.findByCarrera_Id(nuevaCarrera.getId());
    if (pensumExistenteEnCarrera.isPresent() && !pensumExistenteEnCarrera.get().getId().equals(id)) {
        throw new IllegalStateException("Esta carrera ya tiene un pensum asignado.");
    }

    // Validar activo
    if (actualizado.isActivo()) {
        Optional<Pensum> otroActivo = pensumRepository.findByCarrera_IdAndActivoTrue(nuevaCarrera.getId());
        if (otroActivo.isPresent() && !otroActivo.get().getId().equals(id)) {
            throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
        }
    }

    // Validar materias
    for (String codigo : actualizado.getMaterias()) {
        if (!materiaRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Materia no encontrada: " + codigo);
        }
    }

    // Actualizar carrera referenciada
    nuevoVinculoCarrera(nuevaCarrera, actualizado);

    // Actualizar datos del pensum existente
    existente.setCarrera(nuevaCarrera);
    existente.setCodigo(actualizado.getCodigo());
    existente.setFechaInicio(actualizado.getFechaInicio());
    existente.setActivo(actualizado.isActivo());
    existente.setMaterias(actualizado.getMaterias());

    return pensumRepository.save(existente);
}

// Método auxiliar para mantener consistencia en el vínculo carrera <-> pensum
private void nuevoVinculoCarrera(Carrera carrera, Pensum pensum) {
    carrera.setPensum(pensum);
    carreraRepository.save(carrera);
}

public Pensum actualizarEstado(String id, boolean nuevoEstado) {
    Pensum existente = pensumRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

    if (nuevoEstado) {
        String carreraId = existente.getCarrera().getId();
        Optional<Pensum> otroActivo = pensumRepository.findByCarrera_IdAndActivoTrue(carreraId);
        if (otroActivo.isPresent() && !otroActivo.get().getId().equals(id)) {
            throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
        }
    }

    existente.setActivo(nuevoEstado);
    return pensumRepository.save(existente);
}

    public void eliminarPensum(String id) {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        // Limpiar relación en carrera
        Carrera carrera = pensum.getCarrera();
        if (carrera != null) {
            carrera.setPensum(null);
            carreraRepository.save(carrera);
        }

        pensumRepository.deleteById(id);
    }
}

