package com.AuraMoon.auramoon.masterdata.service.impl;

import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.fnb.repository.MenuItemRepository;
import com.AuraMoon.auramoon.masterdata.service.MenuItemMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemMasterServiceImpl implements MenuItemMasterService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Món ăn không tồn tại với ID: " + id));
    }

    @Override
    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        menuItem.setIsDelete(false);
        return menuItemRepository.save(menuItem);
    }

    @Override
    @Transactional
    public MenuItem updateMenuItem(Integer id, MenuItem menuItem) {
        MenuItem existing = getMenuItemById(id);
        existing.setItemName(menuItem.getItemName());
        existing.setPrice(menuItem.getPrice());
        existing.setIngredient(menuItem.getIngredient());
        existing.setIsAvailable(menuItem.getIsAvailable());
        existing.setImageUrl(menuItem.getImageUrl());
        existing.setCategory(menuItem.getCategory());
        return menuItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Integer id) {
        MenuItem existing = getMenuItemById(id);
        existing.setIsDelete(true);
        menuItemRepository.save(existing);
    }
}
