package com.example.worthmate_backend.auth.controller;

import com.example.worthmate_backend.auth.dto.BookingRequest;
import com.example.worthmate_backend.auth.entity.Booking;
import com.example.worthmate_backend.auth.repository.BookingRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ================= CREATE ORDER =================
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody BookingRequest req) {

        try {

            JSONObject options = new JSONObject();

            int amountInPaise = (int) Math.round(req.getAmount() * 100);

            options.put("amount", amountInPaise);
            options.put("currency", "INR");

            // FIXED receipt length issue
            String receipt = "rcpt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            options.put("receipt", receipt);

            Order order = razorpayClient.orders.create(options);

            Booking booking = new Booking();
            booking.setUserId(null);
            booking.setMentorId(req.getMentorId());
            booking.setAmount(req.getAmount());
            booking.setDate(req.getDate());
            booking.setTime(req.getTime());
            booking.setStatus("PENDING");
            booking.setPaid(false);
            booking.setConfirmed(false);
            booking.setAvailabilityId(req.getAvailabilityId());
            booking.setRazorpayOrderId(order.get("id"));

            Booking saved = bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("id"),
                    "amount", amountInPaise,
                    "bookingId", saved.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ================= VERIFY =================
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> body) {

        try {

            String orderId = body.get("razorpay_order_id");
            String paymentId = body.get("razorpay_payment_id");
            String signature = body.get("razorpay_signature");

            String payload = orderId + "|" + paymentId;

            String generatedSignature = hmacSHA256(payload, keySecret);

            if (!generatedSignature.equals(signature)) {
                return ResponseEntity.badRequest().body(Map.of("status", "failed"));
            }

            Booking booking = bookingRepository.findByRazorpayOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            booking.setPaid(true);
            booking.setConfirmed(true);
            booking.setStatus("CONFIRMED");

            booking.setMeetingLink(
                    "https://meet.google.com/" +
                            UUID.randomUUID().toString().substring(0, 8)
            );

            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "bookingId", booking.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    private String hmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));

        return sb.toString();
    }
}