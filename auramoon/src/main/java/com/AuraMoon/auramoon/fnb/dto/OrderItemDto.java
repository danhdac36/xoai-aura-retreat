package com.AuraMoon.auramoon.fnb.dto;

public class OrderItemDto {
    private Integer menuItemId;
    private String itemName;
    private Integer quantity;

    public OrderItemDto() {}

    public OrderItemDto(Integer menuItemId, String itemName, Integer quantity) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
