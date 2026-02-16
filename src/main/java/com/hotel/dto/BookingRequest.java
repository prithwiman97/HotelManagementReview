package com.hotel.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @jakarta.validation.constraints.NotNull(message = "Room ID is required")
    private Long roomId;

    @jakarta.validation.constraints.NotNull(message = "Check-in date is required")
    @jakarta.validation.constraints.FutureOrPresent(message = "Check-in date must be today or in the future")
    private java.time.LocalDate checkInDate;

    @jakarta.validation.constraints.NotNull(message = "Check-out date is required")
    @jakarta.validation.constraints.Future(message = "Check-out date must be in the future")
    private java.time.LocalDate checkOutDate;

    @jakarta.validation.constraints.Min(value = 1, message = "At least 1 adult is required")
    private int numberOfAdults;

    @jakarta.validation.constraints.Min(value = 0, message = "Number of children cannot be negative")
    private int numberOfChildren;
}
