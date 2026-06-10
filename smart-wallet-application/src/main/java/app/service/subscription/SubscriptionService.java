package app.service.subscription;

import app.model.entity.subscription.Subscription;
import app.model.entity.subscription.SubscriptionPeriod;
import app.model.entity.subscription.SubscriptionStatus;
import app.model.entity.subscription.SubscriptionType;
import app.model.entity.user.User;
import app.repository.subscription.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SubscriptionService
{
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository)
    {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createDefaultSubscription(User user)
    {
        LocalDateTime now = LocalDateTime.now();

        Subscription subscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(SubscriptionPeriod.MONTHLY)
                .type(SubscriptionType.DEFAULT)
                .price(BigDecimal.valueOf(0.00))
                .renewalAllowed(true)
                .createdOn(now)
                .completedOn(now.plusMonths(1))
                .build();


        // TODO: Log some proper info message
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
