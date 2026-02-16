package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/search")
    public List<Room> searchRooms(
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxOccupancy) {
        return roomRepository.searchRooms(roomType, minPrice, maxPrice, maxOccupancy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createRoom(@jakarta.validation.Valid @RequestBody Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            return ResponseEntity.badRequest().body("Room number already exists");
        }
        return ResponseEntity.ok(roomRepository.save(room));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id,
            @jakarta.validation.Valid @RequestBody Room roomDetails) {
        return roomRepository.findById(id).map(room -> {
            room.setRoomType(roomDetails.getRoomType());
            room.setPricePerNight(roomDetails.getPricePerNight());
            room.setMaxOccupancy(roomDetails.getMaxOccupancy());
            room.setAmenities(roomDetails.getAmenities());
            room.setDescription(roomDetails.getDescription());
            room.setAvailable(roomDetails.isAvailable());
            return ResponseEntity.ok(roomRepository.save(room));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        return roomRepository.findById(id).map(room -> {
            roomRepository.delete(room);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bulk-upload")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> uploadRooms(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a CSV file!");
        }

        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { // Skip header
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                // flexible parsing assuming order: RoomNumber, Type, Price, Occupancy,
                // Amenities, Description
                if (data.length >= 4) {
                    Room room = new Room();
                    room.setRoomNumber(data[0].trim());
                    if (roomRepository.existsByRoomNumber(room.getRoomNumber()))
                        continue; // Skip duplicates
                    room.setRoomType(data[1].trim());
                    room.setPricePerNight(Double.parseDouble(data[2].trim()));
                    room.setMaxOccupancy(Integer.parseInt(data[3].trim()));
                    if (data.length > 4)
                        room.setAmenities(data[4].trim());
                    if (data.length > 5)
                        room.setDescription(data[5].trim());
                    rooms.add(room);
                }
            }
            roomRepository.saveAll(rooms);
            return ResponseEntity.ok("Uploaded " + rooms.size() + " rooms successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload the file: " + e.getMessage());
        }
    }
}
