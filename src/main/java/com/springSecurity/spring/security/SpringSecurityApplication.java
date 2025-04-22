package com.springSecurity.spring.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}



	@GetMapping("/hello")
	public String hello() {
		return "Hello World";
	}
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user")
	public String users() {
		return "Hello User";
	}


	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public String admins() {
		return "Hello Admin";
	}
}


