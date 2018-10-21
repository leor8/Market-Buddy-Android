package com.example.leoruan.market_buddy_android;

public class Item {
    private String item_name;
    private int quantity;

    Item(String item_name, int quantity) {
        this.item_name = item_name;
        this.quantity = quantity;
    }

    public void update_quantity(int amount) {
        quantity += amount;
    }

    public String get_item_name() {
        return item_name;
    }

    public int get_item_quantity() { return quantity; }
}
