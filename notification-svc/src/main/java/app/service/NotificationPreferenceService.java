package app.service;

import app.model.NotificationPreference;
import app.repository.NotificationPreferenceRepository;
import app.web.dto.NotificationPreferenceRequest;
import app.web.dto.NotificationPreferenceResponse;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static app.web.mapper.NotificationPreferenceMapper.toNotificationPreferenceResponse;

@Service
public class NotificationPreferenceService
{
    private final NotificationPreferenceRepository preferenceRepository;
    private final ResourcePatternResolver resourcePatternResolver;

    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository, ResourcePatternResolver resourcePatternResolver)
    {
        this.preferenceRepository = preferenceRepository;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public NotificationPreferenceResponse upsert(NotificationPreferenceRequest request)
    {
        Optional<NotificationPreference> optionalNotificationPreference = preferenceRepository.findByUserId(UUID.fromString(request.getUserId()));

        // Update
        if(optionalNotificationPreference.isPresent())
        {
            NotificationPreference notificationPreference = optionalNotificationPreference.get();
            notificationPreference.setEnabled(request.isNotificationEnabled());
            notificationPreference.setContactInfo(request.getContactInfo());
            notificationPreference.setUpdatedOn(java.time.LocalDateTime.now());
            NotificationPreference updatedPreference = preferenceRepository.save(notificationPreference);

            return toNotificationPreferenceResponse(updatedPreference);
        }

        // Create
        NotificationPreference newPreference = NotificationPreference.builder()
                .userId(UUID.fromString(request.getUserId()))
                .enabled(request.isNotificationEnabled())
                .contactInfo(request.getContactInfo())
                .type(app.model.NotificationType.EMAIL)
                .createdOn(java.time.LocalDateTime.now())
                .updatedOn(java.time.LocalDateTime.now())
                .build();

        NotificationPreference savedPreference = preferenceRepository.save(newPreference);
        return toNotificationPreferenceResponse(savedPreference);
    }

    public NotificationPreference getById(UUID id)
    {
        return preferenceRepository.findByUserId(id).orElseThrow(() -> new IllegalArgumentException("Notification preference not found"));
    }
}
