package com.AuraMoon.auramoon.masterdata.service;

import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import java.util.List;

public interface MenuItemMasterService {
    List<MenuItem> getAllMenuItems();
    MenuItem getMenuItemById(Integer id);
    MenuItem createMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Integer id, MenuItem menuItem);
    void deleteMenuItem(Integer id);
}
