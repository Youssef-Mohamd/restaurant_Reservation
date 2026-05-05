package com.restaurant.reservation.service;

import com.restaurant.reservation.dto.request.CreateRestaurantRequest;
import com.restaurant.reservation.dto.response.RestaurantResponse;
import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public RestaurantResponse create(CreateRestaurantRequest req, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = Restaurant.builder()
                .name(req.getName())
                .description(req.getDescription())
                .address(req.getAddress())
                .city(req.getCity())
                .phone(req.getPhone())
                .cuisineType(req.getCuisineType())
                .imageUrl(req.getImageUrl())
                .openingTime(LocalTime.parse(req.getOpeningTime()))
                .closingTime(LocalTime.parse(req.getClosingTime()))
                .isActive(true)
                .admin(admin)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToResponse(saved);
    }

    /**
     * Upload image for restaurant
     */
    public RestaurantResponse uploadImage(Long restaurantId, MultipartFile imageFile) throws IOException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Delete old image if exists
        if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
            fileStorageService.deleteRestaurantImage(restaurant.getImageUrl());
        }

        // Save new image
        String imagePath = fileStorageService.saveRestaurantImage(imageFile);
        restaurant.setImageUrl(imagePath);

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToResponse(updated);
    }

    public List<RestaurantResponse> getAll(String city, String cuisine) {
        List<Restaurant> restaurants;

        if (city != null && cuisine != null) {
            restaurants = restaurantRepository.findByCityAndCuisineType(city, cuisine);
        } else if (city != null) {
            restaurants = restaurantRepository.findByCity(city);
        } else if (cuisine != null) {
            restaurants = restaurantRepository.findByCuisineType(cuisine);
        } else {
            restaurants = restaurantRepository.findByIsActiveTrue();
        }

        return restaurants.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return mapToResponse(restaurant);
    }

    private RestaurantResponse mapToResponse(Restaurant r) {
        return RestaurantResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .address(r.getAddress())
                .city(r.getCity())
                .phone(r.getPhone())
                .cuisineType(r.getCuisineType())
                .imageUrl(r.getImageUrl())
                .openingTime(r.getOpeningTime().toString())
                .closingTime(r.getClosingTime().toString())
                .isActive(r.getIsActive())
                .build();
    }
}