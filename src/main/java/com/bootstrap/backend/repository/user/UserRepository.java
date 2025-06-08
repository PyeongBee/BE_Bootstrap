package com.bootstrap.backend.repository.user;

import com.bootstrap.backend.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
}
