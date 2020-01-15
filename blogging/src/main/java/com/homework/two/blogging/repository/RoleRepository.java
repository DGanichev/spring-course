package com.homework.two.blogging.repository;

import com.homework.two.blogging.domain.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRole(String role);
}
