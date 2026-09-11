package notification.dto;

import lombok.Data;
import notification.model.NotificationType;

@Data
public class NotificationRequest {
    private Long employeeId;

    private Long leaveId;

    private NotificationType type;

}
