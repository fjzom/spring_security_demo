package com.example.demo;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserDetailsService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Primary
	UserDetailsService userDetailsService(){ return new UserServiceImpl();}

	
	@Bean
	CommandLineRunner run(UserService userSrv) {
		return args ->{
			userSrv.saveRole(new Role(null, "ROLE_USER"));
			userSrv.saveRole(new Role(null, "ROLE_MANAGER"));
			userSrv.saveRole(new Role(null, "ROLE_ADMIN"));
			userSrv.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userSrv.saveUser(new User(null, "Jhon Travolta", "jhon", "1234", new ArrayList<>() ));
			userSrv.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>() ));
			userSrv.saveUser(new User(null, "Jim Carry", "jim", "1234", new ArrayList<>() ));
			userSrv.saveUser(new User(null, "Arnold Schawarzenegger", "arnold", "1234", new ArrayList<>() ));

			userSrv.addRoleToUser("jhon", "ROLE_USER");
			userSrv.addRoleToUser("jhon", "ROLE_MANAGER");
			userSrv.addRoleToUser("will", "ROLE_MANAGER");
			userSrv.addRoleToUser("jim", "ROLE_ADMIN");
			userSrv.addRoleToUser("arnold", "ROLE_SUPER_ADMIN");
			userSrv.addRoleToUser("arnold", "ROLE_ADMIN");
			userSrv.addRoleToUser("arnold", "ROLE_USER");
		};
	}

}
