package app.web;

import app.service.NotificationPreferenceService;
import app.web.dto.NotificationPreferenceRequest;
import app.web.dto.NotificationPreferenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController
{
    private final NotificationPreferenceService notificationPreferenceService;

    @PostMapping
    public ResponseEntity<NotificationPreferenceResponse> upsertPreference(@RequestBody NotificationPreferenceRequest request)
    {
        NotificationPreferenceResponse response = notificationPreferenceService.upsert(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
