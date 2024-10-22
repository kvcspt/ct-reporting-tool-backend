package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean findByEmail(String email);
}
