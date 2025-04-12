package sh.abijith.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sh.abijith.userservice.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAndEnabledTrue(String email);
    Optional<User> findByIdAndEnabledTrue(String id);
    Page<User> findAllByEnabledTrue(Pageable pageable);

    List<User> findByFirstNameRegex(String regex);
    List<User> findByLastNameRegex(String regex);
    List<User> findByEmailRegex(String regex);
}
