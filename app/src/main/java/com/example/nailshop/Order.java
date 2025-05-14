package com.example.nailshop;

import java.util.List;

public class Order {
    private String userId;
    private long timestamp;
    private List<CartItem> items;

    public Order() {}

    public Order(String userId, long timestamp, List<CartItem> items) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
