package com.homework.one.blogger.common.initializers;

import com.homework.one.blogger.domain.entity.Role;
import com.homework.one.blogger.domain.entity.User;
import com.homework.one.blogger.repository.RoleRepository;
import com.homework.one.blogger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
public class MongoDbInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MongoDbInitializer(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... strings) {

        this.roleRepository.deleteAll();
        this.userRepository.deleteAll();

        Role blogger_role = new Role();
        blogger_role.setRole("ROLE_BLOGGER");
        Role admin_role = new Role();
        admin_role.setRole("ROLE_ADMIN");

        this.roleRepository.save(blogger_role);
        this.roleRepository.save(admin_role);

        User admin = new User();
        admin.setEmail("admin@mail.bg");
        admin.setFirstName("Dimitar");
        admin.setLastName("Dimitrov");
        admin.setPassword(bCryptPasswordEncoder.encode("123456789"));
        admin.setRoles(new HashSet<>(Arrays.asList(admin_role)));
        admin.setUrl("http://google.com");

        this.userRepository.save(admin);
    }
}
