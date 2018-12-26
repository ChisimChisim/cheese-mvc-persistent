package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CheeseDao cheeseDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu menu,
                                         Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int id, Model model) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(@PathVariable int id, Model model) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", new AddMenuItemForm(menu, cheeseDao.findAll()));
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute  @Valid AddMenuItemForm newAddMenuItemForm,
                          @RequestParam int menuId, Errors errors, Model model){

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add item to menu: " + newAddMenuItemForm.getMenu().getName());
            return "menu/add-item";
        }
        Cheese newCheese = cheeseDao.findOne(newAddMenuItemForm.getCheeseId());
        Menu theMenu = menuDao.findOne(menuId);
        theMenu.addItem(newCheese);
        menuDao.save(theMenu);
        return "redirect:view/" + theMenu.getId();
    }

}
