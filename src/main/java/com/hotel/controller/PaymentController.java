package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.repository.BookingRepository;
import com.hotel.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvoiceService invoiceService;

    // Mock payment endpoint
    @PostMapping("/process/{bookingId}")
    public ResponseEntity<String> processPayment(@PathVariable Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isPaymentStatus()) {
            return ResponseEntity.badRequest().body("Payment already processed");
        }

        // Simulate payment success
        booking.setPaymentStatus(true);
        bookingRepository.save(booking);

        return ResponseEntity.ok("Payment processed successfully for Booking ID: " + bookingId);
    }

    @GetMapping(value = "/invoice/{bookingId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.isPaymentStatus()) {
            return ResponseEntity.badRequest().body(null);
        }

        ByteArrayInputStream bis = invoiceService.generateInvoice(booking);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice_" + bookingId + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
