package com.example.social_network_api;

import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
public class SocialNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
		return args -> {
			if (roleRepo.findByName("ROLE_ADMIN").getName().isEmpty()) {
				Role adminRole = Role.builder().name("ROLE_ADMIN").build();
				roleRepo.save(adminRole);

				User admin = User.builder()
						.username("admin")
						.password(encoder.encode("admin"))
						.email("admin@gmail.com")
						.firstName("admin")
						.lastName("admin")
						.build();
				admin.getRoles().add(adminRole);
				userRepo.save(admin);
			}
		};
	}
}
