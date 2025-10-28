package com.ssn.backend.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssn.backend.Entity.Item;
import com.ssn.backend.Entity.User;
import com.ssn.backend.Enum.Status;
import com.ssn.backend.Repository.ItemRepository;

@Service
public class ItemService {
	@Autowired
	private ItemRepository itemRepository;
	
	public void addItem(Item item) {
		itemRepository.save(item);
	}
	
	public Map<String,List<Item>> findLost(){
		Map<String,List<Item>> response = new HashMap<>();
		response.put("items",itemRepository.findByStatus("LOST"));
		return response;
	}
	
	public Map<String,List<Item>> findFound(){
		Map<String,List<Item>> response = new HashMap<>();
		response.put("items",itemRepository.findByStatus("FOUND"));
		return response;
	}
	
	public Map<String,List<Item>> findClaimed(){
		Map<String,List<Item>> response = new HashMap<>();
		response.put("items",itemRepository.findByStatus("CLAIMED"));
		return response;
	}
	
	public Item claim(String itemId, User user) 
	{
		System.out.println(user.getMobile());
	    return itemRepository.findById(itemId)
	        .map(item -> {
	            item.setStatus(Status.CLAIMED);
	            item.setClosedBy(user);
	            return itemRepository.save(item); // ✅ Save changes to DB
	        })
	        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
	}
	
	public void delete(String itemId) {
	    if (!itemRepository.existsById(itemId)) {
	        throw new RuntimeException("Item not found with ID: " + itemId);
	    }
	    itemRepository.deleteById(itemId); 
	}
	
}
