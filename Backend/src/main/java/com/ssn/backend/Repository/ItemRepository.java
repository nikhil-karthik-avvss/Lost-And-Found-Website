package com.ssn.backend.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ssn.backend.Entity.Item;

@Repository
public interface ItemRepository extends MongoRepository<Item,String>{
	List<Item> findByStatus(String status);
}
