package com.example.demo.controller;

import com.example.demo.annotation.Locked;
import com.example.demo.annotation.RateLimited;
import com.example.demo.model.Item;
import com.example.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @PostMapping
    @Locked
    public Item saveItem(@RequestBody Item item) {
        return itemService.save(item);
    }

    @GetMapping("/ratelimited")
    @RateLimited
    public String rateLimitedEndpoint() {
        return "This is a rate-limited endpoint!";
    }
}
