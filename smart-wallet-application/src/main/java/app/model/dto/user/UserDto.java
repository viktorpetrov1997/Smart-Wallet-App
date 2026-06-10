package app.model.dto.user;

import app.model.dto.subscription.SubscriptionDto;
import app.model.dto.wallet.WalletDto;
import app.model.entity.subscription.Subscription;
import app.model.entity.user.Country;
import app.model.entity.user.UserRole;
import app.model.entity.wallet.Wallet;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserDto
{
    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String profilePicture;

    private String email;

    private UserRole role;

    private Country country;

    private boolean isActive;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private List<SubscriptionDto> subscriptions;

    private List<WalletDto> wallets;
}
