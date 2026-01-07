package com.tcs.JwtLogin.repository;

import com.tcs.JwtLogin.models.Role;
import com.tcs.JwtLogin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByRole(Role role);
    List<User> findByEnabledFalse();

}

