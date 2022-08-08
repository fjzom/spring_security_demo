package com.example.demo.service;

import java.util.List;


import com.example.demo.domain.Role;
import com.example.demo.domain.User; 
public interface UserService {
	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String userName, String roleName);
	User getUser(String userName);
	List<User> getUsers();
}
