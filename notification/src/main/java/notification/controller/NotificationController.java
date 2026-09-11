package notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notification.dto.NotificationRequest;
import notification.dto.NotificationResponse;
import notification.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(){
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @GetMapping("/my/unread")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications(){
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications());
    }

    @PostMapping()
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request){
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id){
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/my/readAll")
    public ResponseEntity<List<NotificationResponse>> readAllNotifications(){
        return ResponseEntity.ok(notificationService.readAllNotifications());
    }

}
