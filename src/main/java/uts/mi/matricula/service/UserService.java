package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.User;
import uts.mi.matricula.model.Pensum;
import uts.mi.matricula.repository.UserRepository;
import uts.mi.matricula.repository.PensumRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PensumRepository pensumRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getProfesores() {
        return userRepository.findByRolIgnoreCase("PROFESOR");
    }

    public List<User> getEstudiantes() {
	return userRepository.findByRolIgnoreCase("ESTUDIANTE");
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) throws Exception {
        if (userRepository.existsByCedula(user.getCedula())) {
            throw new Exception("La cédula ya existe en el sistema");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("El username ya existe en el sistema");
        }
        // Si no es estudiante, pensum es null
        if (!"ESTUDIANTE".equalsIgnoreCase(user.getRol())) {
            user.setPensum(null);
        }
        return userRepository.save(user);
    }

    public User updateUser(String id, User user) throws Exception {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        User existingUser = existingUserOpt.get();

        // Actualizar campos permitidos
        existingUser.setNombre(user.getNombre());
        existingUser.setApellido(user.getApellido());
        existingUser.setPassword(user.getPassword());
        existingUser.setRol(user.getRol());

        // Si no es estudiante, pensum es null
        if (!"ESTUDIANTE".equalsIgnoreCase(user.getRol())) {
            existingUser.setPensum(null);
        }
        // Nota: Si quieres actualizar pensum aquí solo si es estudiante, podrías añadir lógica extra.

        return userRepository.save(existingUser);
    }

    // Nuevo método para actualizar el pensum de un estudiante
    public User actualizarPensumDeEstudiante(String userId, String pensumId) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        User user = userOpt.get();

        if (!"ESTUDIANTE".equalsIgnoreCase(user.getRol())) {
            throw new Exception("Solo usuarios con rol ESTUDIANTE pueden tener un pensum asignado");
        }

        Optional<Pensum> pensumOpt = pensumRepository.findById(pensumId);
        if (pensumOpt.isEmpty()) {
            throw new Exception("Pensum no encontrado");
        }

        Pensum pensum = pensumOpt.get();

        // Contar cantidad de estudiantes asignados a este pensum
        long estudiantesAsignados = userRepository.countByRolIgnoreCaseAndPensum("ESTUDIANTE", pensum);

        if (estudiantesAsignados >= 5) {
            throw new Exception("Este pensum ya tiene el máximo de 5 estudiantes asignados");
        }

        user.setPensum(pensum);
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
