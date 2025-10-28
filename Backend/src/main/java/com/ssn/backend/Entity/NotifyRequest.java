package com.ssn.backend.Entity;

//import com.ssn.backend.Entity.Item;
//import com.ssn.backend.Entity.User;

public class NotifyRequest 
{
    private Item item;
    private User notifier;

    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    public User getNotifier() {
        return notifier;
    }
    public void setNotifier(User notifier) {
        this.notifier = notifier;
    }
}
