package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Cacheable(value = "items", key = "#id")
    public Item getItem(Long id) {
        simulateSlowService(); // For cache testing
        return itemRepository.findById(id).orElseThrow();
    }

    public Item save(Item item) {
        simulateLockDelay(); // Simulate long-running save
        return itemRepository.save(item);
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(5000); // Simulate slow DB
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void simulateLockDelay() {
        try {
            Thread.sleep(10000); // Simulate lock testing delay (10 seconds)
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
