package com.example.demo.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

private static final Logger log = LoggerFactory.getLogger(CustomAuthorizationFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
			filterChain.doFilter(request, response);
		}else {
				String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
				if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
					try {
						String token = authorizationHeader.substring("Bearer ".length());
						Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
						JWTVerifier verifier = JWT.require(algorithm).build();
						DecodedJWT decodedJWT = verifier.verify(token);
						String username = decodedJWT.getSubject();
						String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
						Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
						for(int i  =0; i < roles.length; i++) {
							authorities.add(new SimpleGrantedAuthority(roles[i]));
						}
						UsernamePasswordAuthenticationToken authenticationToken = 
								new UsernamePasswordAuthenticationToken(username, null, authorities);
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
						filterChain.doFilter(request, response);
						
					}catch(Exception e) {
						log.error("Error loggin in {}", e.getMessage());;
						response.setHeader("error", e.getMessage());
						response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value());
						//response.sendError(FORBBIDEN.value() );
						Map<String, String> error =  new HashMap<>();
						error.put("error_message", e.getMessage());
						response.setContentType(MediaType.APPLICATION_JSON_VALUE);
						new ObjectMapper().writeValue(response.getOutputStream(), error);
					}
				}else {
					filterChain.doFilter(request, response);
				}
			}
	}

}
