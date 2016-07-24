package com.communicator.app.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
	 * @param username
	 * @param password
	 */
	public void addParticipant(String username, String password) {

		// Check if user already exists
		if (users.containsKey(username)) {
			throw new RuntimeException("User Already Exists!");
		} else if (password == null || password.equals("")) {
			// heck if Password is null or blank
			throw new RuntimeException("Password cannot be blank!");
		} else {

			// All validations passed. Add the user
			try {
				users.put(username, passwordEncoder.encode(password));
				users.store(new FileOutputStream(resource.getFile()), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Changes Password of the logged in User
	 * 
	 * @param oldPassword
	 * @param newPassword
	 */
	public void changePassword(String oldPassword, String newPassword) {

		String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String loggedInUserPassword = users.getProperty(loggedInUser);

		// Check if old password is matching with the logged in user's password
		if (passwordEncoder.matches(oldPassword, loggedInUserPassword)) {
			try {
				// Change the password if the user matches.
				users.put(loggedInUser, passwordEncoder.encode(newPassword));
				users.store(new FileOutputStream(resource.getFile()), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Else, throw an exception.
			throw new RuntimeException("Invalid Old Password!");
		}
	}

	/**
	 * Returns the list of users in the system apart from the logged in one.
	 * 
	 * @return
	 */
	public Set<Object> getParticipants() {
		Set<Object> participants = new HashSet<>(users.keySet());

		// Remove logged in user from the list of participants.
		participants.remove(SecurityContextHolder.getContext().getAuthentication().getName());

		return participants;

	}

}
