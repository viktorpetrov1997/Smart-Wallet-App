package app.mapper.subscription;

import app.model.dto.subscription.SubscriptionDto;
import app.model.entity.subscription.Subscription;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubscriptionMapper
{
    public static SubscriptionDto toSubscriptionDto(Subscription subscription)
    {
        if(subscription == null) return null;

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .owner(subscription.getOwner())
                .status(subscription.getStatus())
                .period(subscription.getPeriod())
                .type(subscription.getType())
                .price(subscription.getPrice())
                .renewalAllowed(subscription.isRenewalAllowed())
                .createdOn(subscription.getCreatedOn())
                .completedOn(subscription.getCompletedOn())
                .build();
    }
}
