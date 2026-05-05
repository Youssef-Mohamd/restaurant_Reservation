package com.restaurant.reservation.controller;

import com.restaurant.reservation.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam Long restaurantId,
            @RequestParam String date,
            @RequestParam int guests) {
        return ResponseEntity.ok(
                availabilityService.getAvailableSlots(
                        restaurantId, LocalDate.parse(date), guests));
    }
}