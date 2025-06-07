package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.CarreraRepository;
import uts.mi.matricula.repository.PensumRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private PensumRepository pensumRepository;

    public Carrera crearCarrera(Carrera carrera) throws Exception {
        if (carreraRepository.existsByCod(carrera.getCod())) {
            throw new Exception("Ya existe una carrera con ese código");
        }

        if (carreraRepository.existsByNombre(carrera.getNombre())) {
            throw new Exception("Ya existe una carrera con ese nombre");
        }

        // Validar que el pensum existe
        if (carrera.getPensum() != null) {
            String pensumId = carrera.getPensum().getId();
            Pensum pensum = pensumRepository.findById(pensumId)
                .orElseThrow(() -> new Exception("El pensum con ID " + pensumId + " no existe"));

        // Validar que el pensum no esté asignado a otra carrera
        Optional<Carrera> existente = carreraRepository.findByPensum_Id(pensumId);
        if (existente.isPresent()) {
            throw new Exception("El pensum ya está asignado a otra carrera");
        }

            carrera.setPensum(pensum);
        }

        return carreraRepository.save(carrera);
    }

    public List<Carrera> obtenerCarreras() {
        return carreraRepository.findAll();
    }

    public Optional<Carrera> obtenerPorCodigo(String cod) {
        return carreraRepository.findByCod(cod);
    }

    public Carrera actualizarCarrera(String id, Carrera carrera) throws Exception {
        Carrera existente = carreraRepository.findById(id)
            .orElseThrow(() -> new Exception("Carrera no encontrada"));

        if (!existente.getCod().equals(carrera.getCod()) && carreraRepository.existsByCod(carrera.getCod())) {
            throw new Exception("Ya existe una carrera con ese código");
        }

        if (!existente.getNombre().equals(carrera.getNombre()) && carreraRepository.existsByNombre(carrera.getNombre())) {
            throw new Exception("Ya existe una carrera con ese nombre");
        }

        existente.setCod(carrera.getCod());
        existente.setNombre(carrera.getNombre());

        // Validar el pensum
        if (carrera.getPensum() != null) {
            String pensumId = carrera.getPensum().getId();
            Pensum pensum = pensumRepository.findById(pensumId)
                .orElseThrow(() -> new Exception("El pensum con ID " + pensumId + " no existe"));

        Optional<Carrera> otraCarreraConPensum = carreraRepository.findByPensum_Id(pensumId);
        if (otraCarreraConPensum.isPresent() && !otraCarreraConPensum.get().getId().equals(id)) {
            throw new Exception("El pensum ya está asignado a otra carrera");
        }

            existente.setPensum(pensum);
        } else {
            existente.setPensum(null);
        }

        return carreraRepository.save(existente);
    }

    public void eliminarCarrera(String id) {
        carreraRepository.deleteById(id);
    }
}
