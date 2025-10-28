package com.ssn.backend.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssn.backend.Entity.Item;
import com.ssn.backend.Entity.User;
import com.ssn.backend.Service.ItemService;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins="http://localhost:5173")
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@PostMapping("/post")
	public ResponseEntity<?> addItem(@RequestBody Item item){
		itemService.addItem(item);
		return ResponseEntity.ok(null);
	}
	
	@GetMapping("/lost")
	public ResponseEntity<Map<String,List<Item>>> lostItems(){
		Map<String,List<Item>> response = itemService.findLost();
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/found")
	public ResponseEntity<Map<String,List<Item>>> foundItems(){
		Map<String,List<Item>> response = itemService.findFound();
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/claimed")
	public ResponseEntity<Map<String,List<Item>>> claimedItems(){
		Map<String,List<Item>> response = itemService.findClaimed();
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/claim/{itemId}")
	public ResponseEntity<?> claim(@PathVariable String itemId, @RequestBody User user) {
	    try {
	        Item updatedItem = itemService.claim(itemId, user);
	        return ResponseEntity.ok(updatedItem); // Return updated item with ID
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("Item not found with ID: " + itemId);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("⚠Something went wrong");
	    }
	}
	
	@DeleteMapping("/delete/{itemId}")
	public ResponseEntity<?> delete(@PathVariable String itemId) {
	    try {
	        itemService.delete(itemId);
	        return ResponseEntity.ok("Item deleted successfully!");
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Item not found with ID: " + itemId);
	    }
	}


	
}
