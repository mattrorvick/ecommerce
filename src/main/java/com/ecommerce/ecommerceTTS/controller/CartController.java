package com.ecommerce.ecommerceTTS.controller;

import com.ecommerce.ecommerceTTS.model.Product;
import com.ecommerce.ecommerceTTS.model.User;
import com.ecommerce.ecommerceTTS.service.ProductService;
import com.ecommerce.ecommerceTTS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@ControllerAdvice
public class CartController {

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @ModelAttribute(value = "loggedInUser")
    public User loggedInUser() {
        return userService.getLoggedInUser();
    }

    @ModelAttribute(value = "cart")
    public Map<Product, Integer> cart() {
        User user = loggedInUser();
        if (user == null)
            return null;
        System.out.println("Getting cart.");
        return user.getCart();
    }

    // puts empty list in model that thymeleaf can use to sum up the cart total
    @ModelAttribute(value = "list")
    public List<Double> list() {
        return new ArrayList<>();
    }

    @GetMapping(value = "/cart")
    public String showCart() {
        return "cart";
    }

    @PostMapping(value = "/cart")
    public String addToCart(@RequestParam long id) {
        Product p = productService.findById(id);
        setQuantity(p, cart().getOrDefault(p, 0) + 1);
        return "cart";
    }

    @PatchMapping(value = "/cart")
    public String updateQuantities(@RequestParam long[] id, @RequestParam int[] quantity) {

        for (int i = 0; i < id.length; i++) {
            Product p = productService.findById(id[i]);
            setQuantity(p, quantity[i]);
        }
        return "cart";
    }

    @DeleteMapping(value = "/cart")
    public String removeFromCart(@RequestParam long id) {
        Product p = productService.findById(id);
        setQuantity(p, 0);
        return "cart";
    }

    private void setQuantity(Product p, int quantity) {
        if (quantity > 0)
            cart().put(p, quantity);
        else
            cart().remove(p);

        userService.updateCart(cart());
    }

}
