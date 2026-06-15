package app.service.user;

import app.mapper.user.UserMapper;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.subscription.Subscription;
import app.model.entity.user.User;
import app.model.entity.wallet.Wallet;
import app.repository.user.UserRepository;
import app.service.subscription.SubscriptionService;
import app.service.wallet.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService
{
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private SubscriptionService subscriptionService;
    private WalletService walletService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, WalletService walletService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
    }

    public UserDto login(UserLoginRequest userLoginRequest)
    {
        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());

        if(optionalUser.isEmpty() || !passwordEncoder.matches(userLoginRequest.getPassword(),
                optionalUser.get().getPassword()))
        {
            throw new RuntimeException("Username or password mismatch!");
        }

        return UserMapper.toUserDto(optionalUser.get());
    }

    public UserDto register(UserRegisterRequest userRegisterRequest)
    {
        userRepository.findByUsername(userRegisterRequest.getUsername())
                .ifPresent(user ->
                {
                    // TODO: Create custom exception e.g. UserAlreadyExistsException
                    throw new RuntimeException("User with this username already exists!");
                });

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(encodedPassword);

        User userEntity = UserMapper.toUserEntity(userRegisterRequest);

        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(userEntity);
        userEntity.setSubscriptions(List.of(defaultSubscription));

        Wallet defaultWallet = walletService.createDefaultWallet(userEntity);
        userEntity.setWallets(List.of(defaultWallet));

        userRepository.save(userEntity);

        return UserMapper.toUserDto(userEntity);
    }

    public List<UserDto> findAll()
    {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }
}
