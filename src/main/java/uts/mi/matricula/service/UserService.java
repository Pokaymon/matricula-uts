package uts.mi.matricula.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uts.mi.matricula.model.User;
import uts.mi.matricula.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getProfesores() {
        return userRepository.findByRolIgnoreCase("PROFESOR");
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
        return userRepository.save(user);
    }

    public User updateUser(String id, User user) throws Exception {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        User existingUser = existingUserOpt.get();
	String rolAnterior = existingUser.getRol();
	String nuevaCedula = user.getCedula();
	String cedulaActual = existingUser.getCedula();

        // Solo se actualizan campos distintos a username y cedula
        existingUser.setNombre(user.getNombre());
        existingUser.setApellido(user.getApellido());
        existingUser.setPassword(user.getPassword());
        existingUser.setRol(user.getRol());

	User actualizado = userRepository.save(existingUser);

        return userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}

