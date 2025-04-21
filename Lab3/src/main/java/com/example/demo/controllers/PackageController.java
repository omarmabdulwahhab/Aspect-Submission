package com.example.demo.controllers;

import com.example.demo.entities.Package;
import com.example.demo.repositories.PackageRepository;
import com.example.demo.repositories.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable Long id) {
        return packageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<Package> createPackage(@PathVariable Long customerId, @RequestBody Package pack) {
        return customerRepository.findById(customerId).map(customer -> {
            pack.setCustomer(customer);
            return ResponseEntity.ok(packageRepository.save(pack));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Package> updatePackage(@PathVariable Long id, @RequestBody Package packDetails) {
        return packageRepository.findById(id).map(pack -> {
            pack.setTrackingNumber(packDetails.getTrackingNumber());
            pack.setStatus(packDetails.getStatus());
            pack.setDestination(packDetails.getDestination());
            return ResponseEntity.ok(packageRepository.save(pack));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        return packageRepository.findById(id).map(pack -> {
            packageRepository.delete(pack);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
