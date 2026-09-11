package notification.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import notification.dto.NotificationRequest;
import notification.dto.NotificationResponse;
import notification.exception.NotificationNotFoundException;
import notification.mapper.NotificationMapper;
import notification.model.Notification;
import notification.repository.NotificationRepository;
import notification.security.CustomUserDetails;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private CustomUserDetails getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public List<NotificationResponse> getMyNotifications() {
        CustomUserDetails loggedInUser = getLoggedInUser();
        return notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(loggedInUser.getEmployeeId()).stream()
                .map(notificationMapper::toResponse).toList();
    }

    public List<NotificationResponse> getMyUnreadNotifications() {
        CustomUserDetails loggedInUser = getLoggedInUser();
        return notificationRepository.findByEmployeeIdAndReadStatusFalse(loggedInUser.getEmployeeId()).stream()
                .map(notificationMapper::toResponse).toList();

    }

    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setTitle(notification.getType().getTitle());
        notification.setMessage(notification.getType().getMessage());
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponse(savedNotification);
    }

    public NotificationResponse markAsRead(Long id) {
        CustomUserDetails loggedInUser = getLoggedInUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        if(loggedInUser.getEmployeeId().equals(notification.getEmployeeId())){
            notification.setReadStatus(true);
        }
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponse(savedNotification);
    }

    public List<NotificationResponse> readAllNotifications() {
        CustomUserDetails loggedInUser = getLoggedInUser();
        List<Notification> notifications = notificationRepository.findByEmployeeIdAndReadStatusFalse(loggedInUser.getEmployeeId());
        notifications.forEach(notification -> notification.setReadStatus(true));
        return notificationRepository.saveAll(notifications).stream().map(notificationMapper::toResponse).toList();
    }

}
