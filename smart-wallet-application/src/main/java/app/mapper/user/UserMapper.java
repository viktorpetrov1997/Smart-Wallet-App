package app.mapper.user;

import app.mapper.subscription.SubscriptionMapper;
import app.mapper.wallet.WalletMapper;
import app.model.dto.subscription.SubscriptionDto;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.dto.wallet.WalletDto;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class UserMapper
{
    public static User toUserEntity(UserRegisterRequest userRegisterRequest)
    {
        if(userRegisterRequest == null) return null;

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .country(userRegisterRequest.getCountry())
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public static UserDto toUserDto(User user)
    {
        if(user == null) return null;

        List<SubscriptionDto> subscriptionDtoList = user.getSubscriptions().stream().map(SubscriptionMapper::toSubscriptionDto).toList();

        List<WalletDto> walletDtoList = user.getWallets().stream().map(WalletMapper::toWalletDto).toList();

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .email(user.getEmail())
                .role(user.getRole())
                .country(user.getCountry())
                .isActive(user.isActive())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .subscriptions(subscriptionDtoList)
                .wallets(walletDtoList)
                .build();
    }
}
