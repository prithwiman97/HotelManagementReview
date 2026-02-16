package com.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(unique = true)
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String roomType; // e.g., Standard, Deluxe, Suite

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    private double pricePerNight;

    private String amenities; // Comma-separated or JSON string

    @Min(value = 1, message = "Max occupancy must be at least 1")
    private int maxOccupancy;

    private boolean isAvailable = true;

    @Column(length = 1000)
    private String description;

    // Could add List<String> images; but for simplicity keeping it out or as a comma separated string
}
