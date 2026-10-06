package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.model.NotificationType;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import app.web.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.web.mapper.NotificationMapper.toNotificationResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService
{
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService notificationPreferenceService;
    private final MailSender mailSender;

    public NotificationResponse send(NotificationRequest request)
    {
        NotificationPreference notificationPreference = notificationPreferenceService.getById(UUID.fromString(request.getUserId()));

        if(!notificationPreference.isEnabled())
        {
            throw new IllegalArgumentException("User with id [%s] has turned off their notifications.".formatted(request.getUserId()));
        }

        Notification notification = Notification.builder()
                .subject(request.getSubject())
                .body(request.getBody())
                .createdOn(LocalDateTime.now())
                .type(NotificationType.EMAIL)
                .userId(UUID.fromString(request.getUserId()))
                .deleted(false).build();

        // Spring mail
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notificationPreference.getContactInfo());
        mailMessage.setSubject(request.getSubject());
        mailMessage.setText(request.getBody());

        sendMail(mailMessage, notification);

        notificationRepository.save(notification);

        return toNotificationResponse(notification);
    }

    private void sendMail(SimpleMailMessage mailMessage, Notification notification)
    {
        try
        {
            mailSender.send(mailMessage);
            notification.setStatus(NotificationStatus.SUCCEEDED);
        }
        catch(Exception e)
        {
            log.error("Mail sending failed due to: {}", e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
        }
    }

    public List<NotificationResponse> getHistory(String userId)
    {
        return getNotDeletedNotifications(userId).stream().map(NotificationMapper::toNotificationResponse).toList();
    }

    public void retryFailed(String userId)
    {
        NotificationPreference notificationPreference = notificationPreferenceService.getById(UUID.fromString(userId));

        if(!notificationPreference.isEnabled())
        {
            throw new IllegalArgumentException("User with id [%s] has turned off their notifications.".formatted(userId));
        }

        List<Notification> failedNotifications = getNotDeletedNotifications(userId).stream().filter(n -> n.getStatus() == NotificationStatus.FAILED).toList();

        failedNotifications.forEach(notification -> {

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(notificationPreference.getContactInfo());
            mailMessage.setSubject(notification.getSubject());
            mailMessage.setText(notification.getBody());

            sendMail(mailMessage, notification);

            notificationRepository.save(notification);
        });

    }

    public void deleteAll(String userId)
    {
        getNotDeletedNotifications(userId).forEach(notification -> {
            notification.setDeleted(true);
            notificationRepository.save(notification);
        });
    }

    private List<Notification> getNotDeletedNotifications(String userId)
    {
        return notificationRepository.findByUserId(UUID.fromString(userId)).stream().filter(n -> !n.isDeleted()).toList();
    }
}
