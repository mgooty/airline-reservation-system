package com.crossover.airline.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.crossover.airline.entity.User.Role;

public class UserAuthentication implements Authentication {

	private static final long serialVersionUID = 6744662980915711828L;
	
	private String email;
	
	private Role role;
	
	private String name;
	
	public UserAuthentication(String email, String name, Role role) {
		this.email = email;
		this.role = role;
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		GrantedAuthority authority = new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return role.name();
			}
		};
		
		authorities.add(authority);
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return email;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean arg0) throws IllegalArgumentException {

	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
