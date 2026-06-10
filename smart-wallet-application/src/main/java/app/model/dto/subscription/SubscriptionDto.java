package app.model.dto.subscription;

import app.model.entity.subscription.SubscriptionPeriod;
import app.model.entity.subscription.SubscriptionStatus;
import app.model.entity.subscription.SubscriptionType;
import app.model.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SubscriptionDto
{
    private UUID id;

    private User owner;

    private SubscriptionStatus status;

    private SubscriptionPeriod period;

    private SubscriptionType type;

    private BigDecimal price;

    private boolean renewalAllowed;

    private LocalDateTime createdOn;

    private LocalDateTime completedOn;
}
