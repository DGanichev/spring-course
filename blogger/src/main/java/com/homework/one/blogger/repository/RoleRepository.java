package com.homework.one.blogger.repository;

import com.homework.one.blogger.domain.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRole(String role);
}
