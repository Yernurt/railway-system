package org.example.zernovoz1.repositories;


import org.example.zernovoz1.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    Users findByUsername(String username);
    List<Users> findByRoleIsNullAndRoleRequestIsNotNull(); // өтініш берген, бірақ бекітілмегендер
}