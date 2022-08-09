package com.example.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("api") @RequiredArgsConstructor
public class UserController {

private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userSrv;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers(){
		return ResponseEntity.ok().body(userSrv.getUsers());
	} 

	@PostMapping("/user/save")
	public ResponseEntity<User> saveUser(@RequestBody User user	){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
		return ResponseEntity.created(uri).body(userSrv.saveUser(user));
	}
	@PostMapping("/role/save")
	public ResponseEntity<Role> saveRole(@RequestBody Role role	){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userSrv.saveRole(role));
	}
	@PostMapping("/role/addtouser")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
		userSrv.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	@PostMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{
        String authorizationHeader = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				User user = userSrv.getUser(username);   
		        String access_token = JWT.create()
		                .withSubject(user.getuserName())
		                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
		                .withIssuer(request.getRequestURL().toString())
		                .withArrayClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()).toArray(new String[0]) ) 
		                .sign(algorithm);
 
		        Map<String, String> tokens = new HashMap<>();
		        tokens.put("access_token", access_token	);
		        tokens.put("refresh_token", refresh_token);
		        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			}catch(Exception e) { 
				response.setHeader("error", e.getMessage());
				response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value()); 
				Map<String, String> error =  new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}else {
			throw new RuntimeException("Refresh token");
		}
	}

	
}	
	@Data
	class RoleToUserForm{
		private String username;
		private String roleName;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getRoleName() {
			return roleName;
		}
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
	}
 