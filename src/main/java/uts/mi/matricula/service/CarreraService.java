package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uts.mi.matricula.model.Carrera;
import uts.mi.matricula.repository.CarreraRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {

    @Autowired
    private CarreraRepository carreraRepository;

    public Carrera crearCarrera(Carrera carrera) throws Exception {
        if (carreraRepository.existsByCod(carrera.getCod())) {
            throw new Exception("Ya existe una carrera con ese código");
        }

        if (carreraRepository.existsByNombre(carrera.getNombre())) {
            throw new Exception("Ya existe una carrera con ese nombre");
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

        return carreraRepository.save(existente);
    }

    public void eliminarCarrera(String id) {
        carreraRepository.deleteById(id);
    }
}
