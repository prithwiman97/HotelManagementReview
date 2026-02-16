package com.hotel.repository;

import com.hotel.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
        List<Booking> findByUserId(Long userId);

        @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND " +
                        "((b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)) AND " +
                        "b.status = :status")
        List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                        @Param("checkIn") LocalDate checkIn,
                        @Param("checkOut") LocalDate checkOut,
                        @Param("status") Booking.BookingStatus status);
}
