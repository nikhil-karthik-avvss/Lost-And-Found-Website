package com.ssn.backend.Controller;

import java.util.HashMap;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ssn.backend.Entity.User;
import com.ssn.backend.Service.*;

@RestController
@RequestMapping("/api")
public class UserController 
{
	private UserService userService;
	
	public UserController(UserService userService)
	{
		this.userService = userService;
	}
	
	
	
	@PostMapping("/auth/register")
	public ResponseEntity<?> register(@RequestBody User new_user)
	{
		Optional<User> user = userService.findByEmail(new_user.getEmail());
		if(!user.isPresent())
		{
			user = userService.findByUserName(new_user.getUserName());
			
			if(user.isEmpty())
			{
				userService.Register(new_user);
				return new ResponseEntity<String>("Registration Successful !",HttpStatus.CREATED);
			}
			
			return new ResponseEntity<String>("Registration Failed !User Already Exists with Same User Name",HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<String>("Registration Failed !User Already Exists with Same Mail Id",HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@PostMapping("/auth/login")
	public ResponseEntity<?> login(@RequestBody HashMap<String,String> loginCredentials)
	{
		Optional<User> foundUser = userService.findByUserName(loginCredentials.get("userName"));
		
		if(foundUser.isPresent())
		{
			User user = foundUser.get();
			
			if(user.getPassword().equals(loginCredentials.get("password")))
				return new ResponseEntity<User>(user,HttpStatus.OK); 
		}
		return new ResponseEntity<String>("Login Failed ! Wrong Credentials !!",HttpStatus.UNAUTHORIZED);
	}
}
