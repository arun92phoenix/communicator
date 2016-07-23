package com.communicator.app.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class related to the user details.
 * 
 * @author pavan
 *
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	Properties users = new Properties();
	private Resource resource;
	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	public UserDetailsService(ResourceLoader resourceLoader) {
		// Load Users into memory
		resource = resourceLoader.getResource("classpath:users.properties");
		loadUsers();

	}

	/**
	 * Loads the users into memory
	 * 
	 * @param resourceLoader
	 */
	private void loadUsers() {
		try {
			// Loading the users into memory

			users.load(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Reading the password from the properties file.
		String password = users.getProperty(username);

		if (password == null || "".equals(password)) {
			// User does not exist. Throwing exception
			throw new UsernameNotFoundException("Username " + username + " not found");
		} else {
			// User found. Returning object.
			return new User(username, password, Arrays.asList(new Authority("PARTICIPANT")));
		}

	}

	/**
	 * Implementation of {@link GrantedAuthority}
	 * 
	 * @author pavan
	 *
	 */
	class Authority implements GrantedAuthority {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2074100296523135319L;
		String authority;

		public Authority(String authority) {
			this.authority = authority;
		}

		@Override
		public String getAuthority() {
			return authority;
		}

	}

	/**
	 * Adds a new user
	 * 
	 * @param params
	 */
	public void addParticipant(Map<String, String> params) {
		String username = params.get("username");
		String password = params.get("password");

		// Check if user already exists
		if (users.containsKey(username)) {
			throw new RuntimeException("User Already Exists!");
		} else if (password == null || password.equals("")) {
			throw new RuntimeException("Password cannot be blank!");
		} else {
			try {
				users.put(username, passwordEncoder.encode(password));
				users.store(new FileOutputStream(resource.getFile()), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
