package com.base.encode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.base.encode.model.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
}
