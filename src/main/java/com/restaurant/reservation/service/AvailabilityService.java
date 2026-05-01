package com.restaurant.reservation.service;

import com.restaurant.reservation.entity.*;
import com.restaurant.reservation.repository.*;
import com.restaurant.reservation.strategyPattern.TableAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final TimeSlotRepository timeSlotRepository;
    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    //  Inject Strategy
    private final TableAssignmentStrategy strategy;

    // =========== GET AVAILABLE SLOTS =============
    public List<String> getAvailableSlots(Long restaurantId, LocalDate date, int guests) {

        List<TimeSlot> slots =
                timeSlotRepository.findByRestaurantIdAndIsActiveTrue(restaurantId);

        return slots.stream()
                .filter(slot -> hasAvailableTable(restaurantId, date, slot.getId(), guests))
                .map(slot -> slot.getSlotTime().toString())
                .collect(Collectors.toList());
    }

    // ===CHECK AVAILABILITY USING STRATEGY =====
    public boolean hasAvailableTable(Long restaurantId,
                                     LocalDate date,
                                     Long slotId,
                                     int guests) {

        //  Get tables that can fit guests
        List<RestaurantTable> tables =
                tableRepository.findByRestaurantIdAndCapacityGreaterThanEqual(
                        restaurantId, guests);

        // Filter only FREE tables (not reserved)
        List<RestaurantTable> availableTables = tables.stream()
                .filter(table -> !reservationRepository
                        .existsByTableIdAndReservationDateAndTimeSlotIdAndStatusNot(
                                table.getId(),
                                date,
                                slotId,
                                ReservationStatus.CANCELLED))
                .collect(Collectors.toList());

        //  Use Strategy to pick best table
        try {
            strategy.assign(availableTables, guests);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}