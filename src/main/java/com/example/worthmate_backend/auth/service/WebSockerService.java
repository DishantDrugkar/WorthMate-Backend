package com.example.worthmate_backend.auth.service;

import com.example.worthmate_backend.auth.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSockerService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToMentor(String mentorId, String bookingId) {

        NotificationDTO notification =

                new NotificationDTO(

                        "New booking received!",

                        bookingId

                );

        // 👇 mentor-specific channel

        messagingTemplate.convertAndSend(

                "/topic/mentor/" + mentorId,

                notification

        );

    }
}
