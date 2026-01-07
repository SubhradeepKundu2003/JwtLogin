package com.tcs.JwtLogin.repository;

import com.tcs.JwtLogin.models.Hall;
import com.tcs.JwtLogin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Long> {
    List<Hall> findByOwner(User owner);
}

