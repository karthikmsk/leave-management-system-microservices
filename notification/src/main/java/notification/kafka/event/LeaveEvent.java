package notification.kafka.event;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveEvent {

    private Long employeeId;

    private Long leaveRequestId;

    private String employeeName;

    private String leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private LeaveEventType eventType;

}
