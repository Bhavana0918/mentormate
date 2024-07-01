package com.mentormate.mentormate.services;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mentormate.mentormate.entities.User;

//Custom implementation of UserDetails interface for user authentication
public class CustomUserDetails implements UserDetails 	 {

	private static final long serialVersionUID = 1L;
	private User users;

	// Constructor that takes a Users object
	public CustomUserDetails(User users) {
		this.users = users;
	}

	public String getFirstName() {
		return users.getFirstName();
	}

	 @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return users.getRoles().stream().map(SimpleGrantedAuthority::new).toList();
	    }

	// Get the user's password
	@Override
	public String getPassword() {
		return users.getPassword();
	}

	// Get the user's username as email
	@Override
	public String getUsername() {
		return users.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
