package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.Permiso;
import uts.mi.matricula.repository.PermisoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    public List<Permiso> obtenerTodos() {
        return permisoRepository.findAll();
    }

    public Optional<Permiso> obtenerPorId(String id) {
        return permisoRepository.findById(id);
    }

    public boolean isPermisoActivo(String codigo) {
	return permisoRepository.findByCodigo(codigo)
		.map(Permiso::isEstado)
		.orElse(false);
    }

    public Permiso crear(Permiso permiso) throws IllegalArgumentException {
        if (permisoRepository.existsByCodigo(permiso.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un permiso con ese código.");
        }
        if (permisoRepository.existsByNombre(permiso.getNombre())) {
            throw new IllegalArgumentException("Ya existe un permiso con ese nombre.");
        }
        return permisoRepository.save(permiso);
    }

    public Permiso actualizar(Permiso permiso) throws IllegalArgumentException {
        Optional<Permiso> existentePorCodigo = permisoRepository.findByCodigo(permiso.getCodigo());
        Optional<Permiso> existentePorNombre = permisoRepository.findByNombre(permiso.getNombre());

        if (existentePorCodigo.isPresent() && !existentePorCodigo.get().getId().equals(permiso.getId())) {
            throw new IllegalArgumentException("Código de permiso ya en uso.");
        }

        if (existentePorNombre.isPresent() && !existentePorNombre.get().getId().equals(permiso.getId())) {
            throw new IllegalArgumentException("Nombre de permiso ya en uso.");
        }

        return permisoRepository.save(permiso);
    }

    public void eliminar(String id) {
        permisoRepository.deleteById(id);
    }
}

