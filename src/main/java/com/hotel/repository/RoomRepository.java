package com.hotel.repository;

import com.hotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByIsAvailableTrue();

    @Query("SELECT r FROM Room r WHERE " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "(:minPrice IS NULL OR r.pricePerNight >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.pricePerNight <= :maxPrice) AND " +
           "(:maxOccupancy IS NULL OR r.maxOccupancy >= :maxOccupancy)")
    List<Room> searchRooms(@Param("roomType") String roomType,
                           @Param("minPrice") Double minPrice,
                           @Param("maxPrice") Double maxPrice,
                           @Param("maxOccupancy") Integer maxOccupancy);
    
    boolean existsByRoomNumber(String roomNumber);
}
