package notification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import notification.model.Notification;
import notification.model.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long>{

    List<Notification> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    List<Notification> findByEmployeeIdAndReadStatusFalse(Long employeeId);

    boolean existsByEmployeeIdAndLeaveIdAndType(Long employeeId, Long leaveRequestId, NotificationType type);

}
