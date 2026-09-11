package notification.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notification.dto.NotificationRequest;
import notification.kafka.event.LeaveEvent;
import notification.model.NotificationType;
import notification.repository.NotificationRepository;
import notification.service.NotificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @KafkaListener(
            topics = "leave-events",
            groupId = "notification-group"
    )
    public void consumeLeaveEvent(LeaveEvent event) {

        System.out.println("======================================");
        System.out.println("Kafka event received");
        System.out.println("Employee ID : " + event.getEmployeeId());
        System.out.println("Leave ID    : " + event.getLeaveRequestId());
        System.out.println("Leave Type  : " + event.getLeaveType());
        System.out.println("Status      : " + event.getStatus());
        System.out.println("Event Type  : " + event.getEventType());
        System.out.println("======================================");

        // Convert Kafka event type to NotificationType
        NotificationType type =
                NotificationType.valueOf(event.getEventType().name());

        // Check for duplicate notification
        boolean exists =
                notificationRepository.existsByEmployeeIdAndLeaveIdAndType(
                        event.getEmployeeId(),
                        event.getLeaveRequestId(),
                        type
                );

        if (exists) {

            log.info(
                    "Notification already exists. employeeId={}, leaveId={}, type={}. Skipping.",
                    event.getEmployeeId(),
                    event.getLeaveRequestId(),
                    type
            );

            return;
        }

        // Create notification request
        NotificationRequest request = new NotificationRequest();

        request.setEmployeeId(event.getEmployeeId());
        request.setLeaveId(event.getLeaveRequestId());
        request.setType(type);

        notificationService.createNotification(request);

        log.info(
                "Notification created. employeeId={}, leaveId={}, type={}",
                event.getEmployeeId(),
                event.getLeaveRequestId(),
                type
        );
    }
}