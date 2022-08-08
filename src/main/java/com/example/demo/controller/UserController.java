package com.example.demo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("api") @RequiredArgsConstructor
public class UserController {

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
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form){
		userSrv.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
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
 