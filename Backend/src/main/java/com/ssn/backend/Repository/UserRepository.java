package com.ssn.backend.Repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.ssn.backend.Entity.User;

public interface UserRepository extends MongoRepository<User, String> 
{
	public Optional<User> findByemail(String email);
	
	public Optional<User> findByuserName(String userName);
}
