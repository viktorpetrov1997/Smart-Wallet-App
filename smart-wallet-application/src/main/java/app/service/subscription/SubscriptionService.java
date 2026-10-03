package app.service.subscription;

import app.model.entity.subscription.Subscription;
import app.model.entity.subscription.SubscriptionPeriod;
import app.model.entity.subscription.SubscriptionStatus;
import app.model.entity.subscription.SubscriptionType;
import app.model.entity.user.User;
import app.repository.subscription.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class SubscriptionService
{
    SubscriptionRepository subscriptionRepository;

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
                .period(SubscriptionPeriod.MONTHLY)
                .status(SubscriptionStatus.ACTIVE)
                .type(SubscriptionType.DEFAULT)
                .price(BigDecimal.valueOf(0.00))
                .completedOn(now.plusMonths(1))
                .renewalAllowed(true)
                .createdOn(now)
                .build();

        //TODO: Log some proper info message
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
