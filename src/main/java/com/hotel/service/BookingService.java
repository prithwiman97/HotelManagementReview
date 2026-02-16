package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public Booking createBooking(Booking booking) {
        // Validate dates
        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Check availability
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(),
                Booking.BookingStatus.CONFIRMED);

        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Room is not available for selected dates");
        }

        // Calculate total amount
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Room ID"));

        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (nights < 1)
            nights = 1;

        booking.setTotalAmount(room.getPricePerNight() * nights);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(false); // Pending until payment (mock)

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Refund logic (simplified): e.g., create refund record
        // For now just update status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
