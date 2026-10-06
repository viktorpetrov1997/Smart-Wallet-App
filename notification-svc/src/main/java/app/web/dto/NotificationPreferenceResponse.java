package app.web.dto;

import app.model.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceResponse
{
    private NotificationType notificationType;

    private boolean enabled;

    private String contactInfo;
}
