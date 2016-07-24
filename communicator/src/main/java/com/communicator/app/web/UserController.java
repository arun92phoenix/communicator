package com.communicator.app.web;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.communicator.app.services.UserDetailsService;

/**
 * Controller having methods for user related services
 * 
 * @author pavan
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserDetailsService userDetailsService;

	/**
	 * Add user mapping
	 * 
	 * @param params
	 */
	@RequestMapping("/add")
	public void addUser(@RequestBody Map<String, String> params) {
		String username = params.get("username");
		String password = params.get("password");
		userDetailsService.addParticipant(username, password);
	}

	/**
	 * Default Exception Handler
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public DefaultRestException handleException(Exception e) {
		return new DefaultRestException("500", e.getMessage());
	}

	/**
	 * Change Password Mapping
	 * 
	 * @param params
	 */
	@RequestMapping("/changepassword")
	public void changePassword(@RequestBody Map<String, String> params) {
		String oldPassword = params.get("oldPassword");
		String newPassword = params.get("newPassword");
		userDetailsService.changePassword(oldPassword, newPassword);
	}

	/**
	 * Returns list of users logged into the system.
	 * @return 
	 */
	@RequestMapping("/list")
	public Set<Object> getParticipants() {
		return userDetailsService.getParticipants();
	}
}
