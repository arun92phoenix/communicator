package com.communicator.app.web;

import java.util.Map;

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

	@RequestMapping("/add")
	public void addUser(@RequestBody Map<String, String> params) {
		userDetailsService.addParticipant(params);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public DefaultRestException handleException(Exception e) {
		return new DefaultRestException("500", e.getMessage());
	}
}
