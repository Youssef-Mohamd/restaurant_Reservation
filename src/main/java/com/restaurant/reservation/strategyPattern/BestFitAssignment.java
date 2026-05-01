package com.restaurant.reservation.strategyPattern;

import com.restaurant.reservation.entity.RestaurantTable;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;

@Component
public class BestFitAssignment implements TableAssignmentStrategy {

    @Override
    public RestaurantTable assign(List<RestaurantTable> availableTables, int guestCount) {
        return availableTables.stream()
                .filter(t -> t.getCapacity() >= guestCount)
                .min(Comparator.comparingInt(RestaurantTable::getCapacity))
                .orElseThrow(() -> new RuntimeException("No available table for " + guestCount + " guests"));
    }
}