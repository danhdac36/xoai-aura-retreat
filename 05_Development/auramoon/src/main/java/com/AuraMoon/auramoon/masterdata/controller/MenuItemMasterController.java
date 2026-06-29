package com.AuraMoon.auramoon.masterdata.controller;

import com.AuraMoon.auramoon.fnb.entity.MenuItem;
import com.AuraMoon.auramoon.masterdata.service.MenuItemMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/master-data/menu-items")
@RequiredArgsConstructor
public class MenuItemMasterController {

    private final MenuItemMasterService menuItemService;

    @ModelAttribute("newMenuItem")
    public MenuItem defaultNewMenuItem() { return new MenuItem(); }
    @ModelAttribute("editMenuItem")
    public MenuItem defaultEditMenuItem() { return new MenuItem(); }

    @PostMapping("/create")
    public String createMenuItem(
            @ModelAttribute("newMenuItem") MenuItem newItem,
            BindingResult bindingResult, Model model) {
        validateMenuItem(newItem, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "menu-items");
            model.addAttribute("menuItems", menuItemService.getAllMenuItems());
            model.addAttribute("newMenuItem", newItem);
            model.addAttribute("showCreateModal", true);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        menuItemService.createMenuItem(newItem);
        model.addAttribute("activeTab", "menu-items");
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        model.addAttribute("successMsg", "Thêm món ăn thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/update")
    public String updateMenuItem(
            @PathVariable("id") Integer id,
            @ModelAttribute("editMenuItem") MenuItem editItem,
            BindingResult bindingResult, Model model) {
        validateMenuItem(editItem, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "menu-items");
            model.addAttribute("menuItems", menuItemService.getAllMenuItems());
            model.addAttribute("editMenuItem", editItem);
            model.addAttribute("showEditModal", true);
            model.addAttribute("editId", id);
            model.addAttribute("hasError", true);
            return "admin/master-data/fragments :: tabContent";
        }
        menuItemService.updateMenuItem(id, editItem);
        model.addAttribute("activeTab", "menu-items");
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        model.addAttribute("successMsg", "Cập nhật món ăn thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/delete")
    public String deleteMenuItem(@PathVariable("id") Integer id, Model model) {
        menuItemService.deleteMenuItem(id);
        model.addAttribute("activeTab", "menu-items");
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        model.addAttribute("successMsg", "Xóa món ăn thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleMenuItemStatus(@PathVariable("id") Integer id, Model model) {
        MenuItem item = menuItemService.getMenuItemById(id);
        item.setIsAvailable(!item.getIsAvailable());
        menuItemService.updateMenuItem(id, item);
        model.addAttribute("activeTab", "menu-items");
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        model.addAttribute("successMsg", "Thay đổi trạng thái khả dụng thành công!");
        return "admin/master-data/fragments :: tabContent";
    }

    private void validateMenuItem(MenuItem item, BindingResult bindingResult) {
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            bindingResult.rejectValue("itemName", "error.itemName", "Tên món ăn không được để trống");
        }
        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            bindingResult.rejectValue("price", "error.price", "Đơn giá phải lớn hơn hoặc bằng 0");
        }
    }
}
