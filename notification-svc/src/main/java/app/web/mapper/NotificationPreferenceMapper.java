package app.web.mapper;

import app.model.NotificationPreference;
import app.web.dto.NotificationPreferenceResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotificationPreferenceMapper
{
    public static NotificationPreferenceResponse toNotificationPreferenceResponse(NotificationPreference notificationPreference)
    {
        return NotificationPreferenceResponse.builder()
                .contactInfo(notificationPreference.getContactInfo())
                .enabled(notificationPreference.isEnabled())
                .notificationType(notificationPreference.getType())
                .build();
    }
}
