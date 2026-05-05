package com.restaurant.reservation.controller;

import com.restaurant.reservation.dto.request.CreateRestaurantRequest;
import com.restaurant.reservation.dto.response.RestaurantResponse;
import com.restaurant.reservation.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> create(
            @Valid @RequestBody CreateRestaurantRequest req,
            Authentication auth) {
        return ResponseEntity.status(201)
                .body(restaurantService.create(req, auth.getName()));
    }

    /**
     * Upload image for a restaurant
     * POST /api/restaurants/{id}/image
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<RestaurantResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RestaurantResponse response = restaurantService.uploadImage(id, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String cuisine) {
        return ResponseEntity.ok(restaurantService.getAll(city, cuisine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }
}