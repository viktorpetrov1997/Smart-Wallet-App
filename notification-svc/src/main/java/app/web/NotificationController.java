package app.web;

import app.service.NotificationService;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController
{
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request)
    {
        NotificationResponse response = notificationService.send(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getHistory(@RequestParam String userId)
    {
        List<NotificationResponse> response = notificationService.getHistory(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> retryFailedNotification(@RequestParam String userId)
    {
        notificationService.retryFailed(userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestParam String userId)
    {
        notificationService.deleteAll(userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
