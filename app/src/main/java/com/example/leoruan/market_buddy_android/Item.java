package com.example.leoruan.market_buddy_android;

public class Item {
    private String item_name;
    private int quantity;
    private String listid;
    private String itemid;
    private String price;

    Item(String item_name, int quantity, String listid, String itemid, String price) {
        this.item_name = item_name;
        this.quantity = quantity;
        this.listid = listid;
        this.itemid = itemid;
        this.price = price;
    }

    public void update_quantity(int amount) {
        quantity += amount;
    }

    public void update_itemid(String id) { itemid = id; }

    public String get_item_name() {
        return item_name;
    }

    public int get_item_quantity() { return quantity; }

    public String get_item_listid() { return listid; }

    public String get_item_itemid() { return itemid; }

    public String get_item_price() { return price; }
}
