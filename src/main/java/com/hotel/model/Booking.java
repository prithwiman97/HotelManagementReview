package com.hotel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @jakarta.validation.constraints.NotNull(message = "Check-in date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @jakarta.validation.constraints.NotNull(message = "Check-out date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    @jakarta.validation.constraints.Min(value = 1, message = "At least 1 adult is required")
    private int numberOfAdults;

    @jakarta.validation.constraints.Min(value = 0, message = "Number of children cannot be negative")
    private int numberOfChildren;

    private double totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // CONFIRMED, CANCELLED, COMPLETED

    private boolean paymentStatus; // true = paid, false = pending

    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
}
