package com.ssn.backend.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssn.backend.Entity.Item;
import com.ssn.backend.Entity.NotifyRequest;
import com.ssn.backend.Entity.User;
import com.ssn.backend.Enum.Status;
import com.ssn.backend.Service.ItemService;
import com.ssn.backend.Service.MailService;
import com.ssn.backend.Service.UserService;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173")
public class ItemController
{
    @Autowired
    private ItemService itemService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @PostMapping("/post")
    public ResponseEntity<?> addItem(@RequestBody Item item)
    {
        itemService.addItem(item);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/lost")
    public ResponseEntity<Map<String, List<Item>>> lostItems()
    {
        Map<String, List<Item>> response = itemService.findLost();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/found")
    public ResponseEntity<Map<String, List<Item>>> foundItems()
    {
        Map<String, List<Item>> response = itemService.findFound();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/claimed")
    public ResponseEntity<Map<String, List<Item>>> claimedItems()
    {
        Map<String, List<Item>> response = itemService.findClaimed();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/claim/{itemId}")
    public ResponseEntity<?> claim(@PathVariable String itemId, @RequestBody User user)
    {
        try
        {
            Item updatedItem = itemService.claim(itemId, user);
            return ResponseEntity.ok(updatedItem);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found with ID: " + itemId);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠ Something went wrong");
        }
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<?> delete(@PathVariable String itemId)
    {
        try
        {
            itemService.delete(itemId);
            return ResponseEntity.ok("Item deleted successfully!");
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found with ID: " + itemId);
        }
    }

    @PostMapping("/notify/{username}")
    public ResponseEntity<Map<String, String>> notifyOwner(@PathVariable String username, @RequestBody NotifyRequest request)
    {
        try
        {
            Item item = request.getItem();
            User notifier = request.getNotifier();

            Optional<User> ownerOpt = userService.findByUserName(username);
            if (ownerOpt.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Owner not found"));
            }

            User owner = ownerOpt.get();

            String subject;
            String bodyIntro;

            if (item.getStatus() == Status.FOUND)
            {
                subject = "Someone is interested in your found item!";
                bodyIntro = "User \"" + notifier.getName() + "\" (" + notifier.getUserName() + ") has shown interest in your found item.";
            }
            else
            {
                subject = "Someone might have found your lost item!";
                bodyIntro = "Good news! User \"" + notifier.getName() + "\" (" + notifier.getUserName() + ") believes they found your lost item.";
            }

            String body = "Hello " + owner.getName() + ",\n\n"
                    + bodyIntro + "\n\n"
                    + "📦 Item: " + item.getItemName() + "\n"
                    + "📍 Location: " + item.getLocation() + "\n"
                    + "📝 Description: " + item.getDescription() + "\n\n"
                    + "You can contact them directly:\n"
                    + "📧 Email: " + notifier.getEmail() + "\n"
                    + "📱 Mobile: " + notifier.getMobile() + "\n\n"
                    + "Please log in to the platform to mark the item as claimed once verified/Claimed.\n\n"
                    + "Regards,\nLost & Found Team";

            mailService.sendMail(owner.getEmail(), subject, body);
            return ResponseEntity.ok(Map.of("message", "Notification sent to " + owner.getEmail()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send notification"));
        }
    }
}