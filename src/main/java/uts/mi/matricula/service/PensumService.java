package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.model.Materia;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.CarreraRepository;
import uts.mi.matricula.repository.MateriaRepository;
import uts.mi.matricula.repository.PensumRepository;

import uts.mi.matricula.dto.PensumDTO;

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

        // Validar carrera existente
        Carrera carrera = carreraRepository.findById(pensum.getCarrera().getId())
            .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada."));

        pensum.setCarrera(carrera);

        // Validar materias
        for (String codigo : pensum.getMaterias()) {
            if (!materiaRepository.existsByCodigo(codigo)) {
                throw new IllegalArgumentException("Materia no encontrada: " + codigo);
            }
        }

        if (pensum.isActivo()) {
            Optional<Pensum> activoExistente = pensumRepository.findByCarreraAndActivoTrue(carrera);
            if (activoExistente.isPresent()) {
                throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
            }
        }

        return pensumRepository.save(pensum);
    }

    public List<Pensum> obtenerTodos() {
        return pensumRepository.findAll();
    }

public List<PensumDTO> obtenerPensumsConNombreCarrera() {
    List<Pensum> pensums = pensumRepository.findAll();
    List<PensumDTO> dtos = new ArrayList<>();

    for (Pensum p : pensums) {
        Optional<Carrera> carreraOpt = carreraRepository.findById(p.getCarrera().getId());
        if (carreraOpt.isPresent()) {
            Carrera c = carreraOpt.get();
            List<Materia> materias = obtenerMateriasDePensum(p.getCodigo());
            PensumDTO dto = new PensumDTO(
                p.getId(),
                p.getCodigo(),
                p.getFechaInicio(),
                p.isActivo(),
                c.getId(),
                c.getNombre(),
                materias
            );
            dtos.add(dto);
        }
    }

    return dtos;
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

        Carrera carrera = carreraRepository.findById(actualizado.getCarrera().getId())
                .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada."));

        actualizado.setCarrera(carrera);

        if (!existente.getCodigo().equals(actualizado.getCodigo()) &&
                pensumRepository.existsByCodigo(actualizado.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un pensum con ese código.");
        }

        if (actualizado.isActivo()) {
            Optional<Pensum> otroActivo = pensumRepository.findByCarreraAndActivoTrue(carrera);
            if (otroActivo.isPresent() && !otroActivo.get().getId().equals(id)) {
                throw new IllegalStateException("Ya existe un pensum activo para esta carrera.");
            }
        }

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

