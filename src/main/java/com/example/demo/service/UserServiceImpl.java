package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepo.findByUserName(username);
		if(user == null) {
			log.error("User not found in the database");
			throw new UsernameNotFoundException("User not found in the data base");
		}else {
			log.info("User found in the database {}", username);
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new org.springframework.security.core.userdetails.User(user.getuserName(), user.getPassword(), authorities);
	}
	
	@Override
	public User saveUser(User user) {
		// TODO Auto-generated method stub
		log.info("Saving new user {} to the database");
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		// TODO Auto-generated method stub
		log.info("Saving new role {} to the database");
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String userName, String roleName) {
		// TODO Auto-generated method stub
		log.info("Adding role {} to user {} ", roleName, userName);
		User user = userRepo.findByUserName(userName);
		Role role = roleRepo.findByName(roleName);
		user.getRoles().add(role);
	}

	@Override
	public User getUser(String userName) {
		// TODO Auto-generated method stub
		log.info("Fetching user {}", userName);
		return userRepo.findByUserName(userName);
	}

	@Override
	public List<User> getUsers() {
		// TODO Auto-generated method stub
		log.info("Fetching users");
		return userRepo.findAll();
	}

}
