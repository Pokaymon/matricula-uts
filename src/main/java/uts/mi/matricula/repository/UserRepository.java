package uts.mi.matricula.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uts.mi.matricula.model.User;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByCedula(String cedula);
    Optional<User> findByUsername(String username);
    boolean existsByCedula(String cedula);
    boolean existsByUsername(String username);
    List<User> findByRolIgnoreCase(String rol);
}
