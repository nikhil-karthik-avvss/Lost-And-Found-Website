package com.ssn.backend.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.ssn.backend.Entity.User;
import com.ssn.backend.Repository.*;

@Service
public class UserService 
{
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}
	
	public Optional<User> findByEmail(String email)
	{
		return userRepository.findByemail(email);
	}
	
	public Optional<User> findByUserName(String userName)
	{
		return userRepository.findByuserName(userName);
	}
	
	public User Register(User new_user)
	{
		return userRepository.save(new_user);
	}
	
	public List<User> findAll(){
		return userRepository.findAll();
	}
 }
