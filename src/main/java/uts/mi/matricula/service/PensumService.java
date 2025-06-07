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

    public Pensum crearPensum(Pensum pensum) {
        if (pensumRepository.existsByCodigo(pensum.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un pensum con ese código.");
        }

        // Validar que las materias existan
        for (String codigo : pensum.getMaterias()) {
            if (!materiaRepository.existsByCodigo(codigo)) {
                throw new IllegalArgumentException("Materia no encontrada: " + codigo);
            }
        }

        // Validar que solo haya un pensum activo por carrera
        if (pensum.isActivo()) {
            Optional<Pensum> activoExistente = pensumRepository.findByCarreraAndActivoTrue(pensum.getCarrera());
            if (activoExistente.isPresent()) {
                throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
            }
        }

        return pensumRepository.save(pensum);
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

        if (!existente.getCodigo().equals(actualizado.getCodigo()) &&
                pensumRepository.existsByCodigo(actualizado.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un pensum con ese código.");
        }

        if (actualizado.isActivo()) {
            Optional<Pensum> otroActivo = pensumRepository.findByCarreraAndActivoTrue(actualizado.getCarrera());
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

        actualizado.setId(id);
        return pensumRepository.save(actualizado);
    }

    public Pensum actualizarEstado(String id, boolean nuevoEstado) {
        Pensum existente = pensumRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Pensum no encontrado."));

        // Si se quiere activar y ya hay otro activo para la misma carrera, error
        if (nuevoEstado) {
            Optional<Pensum> otroActivo = pensumRepository.findByCarreraAndActivoTrue(existente.getCarrera());
            if (otroActivo.isPresent() && !otroActivo.get().getId().equals(id)) {
                throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
            }
        }

        existente.setActivo(nuevoEstado);
        return pensumRepository.save(existente);
    }

    public void eliminarPensum(String id) {
        pensumRepository.deleteById(id);
    }
}
