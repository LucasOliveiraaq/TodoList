package com.lucas.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lucas.todosimple.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{    
}
