package com.restaurant.reservation.service;

import com.restaurant.reservation.entity.RestaurantTable;
import com.restaurant.reservation.strategyPattern.TableAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableAssignmentService {

    private final TableAssignmentStrategy strategy;

    public RestaurantTable assignTable(List<RestaurantTable> availableTables, int guestCount) {
        return strategy.assign(availableTables, guestCount);
    }
}