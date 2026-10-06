package app.web.mapper;

import app.model.Notification;
import app.web.dto.NotificationResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotificationMapper
{
    public static NotificationResponse toNotificationResponse(Notification notification)
    {
        return NotificationResponse.builder()
                .type(notification.getType())
                .subject(notification.getSubject())
                .status(notification.getStatus())
                .createdOn(notification.getCreatedOn())
                .build();
    }
}
