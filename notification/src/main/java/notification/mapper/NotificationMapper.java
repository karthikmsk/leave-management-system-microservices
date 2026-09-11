package notification.mapper;

import org.mapstruct.Mapper;

import notification.dto.NotificationRequest;
import notification.dto.NotificationResponse;
import notification.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
    Notification toEntity(NotificationRequest request);

} 
