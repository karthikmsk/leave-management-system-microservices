package notification.dto;

import java.time.LocalDateTime;
import lombok.Data;
import notification.model.NotificationType;

@Data
public class NotificationResponse {
    private Long id;
    
    private Long employeeId;

    private Long leaveId;

    private String title;

    private String message;

    private NotificationType type;

    private boolean readStatus;

    private LocalDateTime createdAt;

}
