package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.entity.Payment;
import com.example.worthmate_backend.auth.repository.BookingRepository;
import com.example.worthmate_backend.auth.repository.PaymentRepository;
import com.example.worthmate_backend.auth.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    // =========================================
    // 🔥 RAZORPAY WEBHOOK (PRODUCTION FLOW)
    // =========================================
    @PostMapping("/webhook")
    public Map<String, Object> razorpayWebhook(@RequestBody Map<String, Object> payload) {

        try {

            // ================================
            // 1. EXTRACT PAYMENT DATA
            // ================================
            Map<String, Object> paymentEntity =
                    (Map<String, Object>) payload.get("payload");

            Map<String, Object> payment =
                    (Map<String, Object>) paymentEntity.get("payment");

            Map<String, Object> entity =
                    (Map<String, Object>) payment.get("entity");

            String razorpayPaymentId = (String) entity.get("id");
            String razorpayOrderId = (String) entity.get("order_id");

            // ================================
            // 2. FIND BOOKING
            // ================================
            Booking booking = bookingRepository
                    .findByRazorpayOrderId(razorpayOrderId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // ================================
            // 3. SAVE PAYMENT
            // ================================
            Payment pay = new Payment();
            pay.setBookingId(booking.getId());
            pay.setAmount(booking.getAmount());
            pay.setStatus("SUCCESS");
            pay.setProvider("RAZORPAY");
            pay.setPaymentId(razorpayPaymentId);
            pay.setOrderId(razorpayOrderId);

            paymentRepository.save(pay);

            // ================================
            // 4. CONFIRM BOOKING
            // ================================
            booking.setStatus("CONFIRMED");

            String meetingLink =
                    "https://meet.google.com/" +
                            UUID.randomUUID().toString().substring(0, 8);

            booking.setMeetingLink(meetingLink);

            bookingRepository.save(booking);

            // ================================
            // 5. MARK SLOT BOOKED
            // ================================
            availabilityRepository.deleteById(booking.getAvailabilityId());

            return Map.of(
                    "status", "success"
            );

        } catch (Exception e) {

            return Map.of(
                    "status", "failed",
                    "error", e.getMessage()
            );
        }
    }
}