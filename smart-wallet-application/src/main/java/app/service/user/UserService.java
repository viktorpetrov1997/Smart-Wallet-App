package app.service.user;

import app.mapper.user.UserMapper;
import app.model.dto.user.EditUserRequest;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.subscription.Subscription;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.model.entity.wallet.Wallet;
import app.repository.user.UserRepository;
import app.service.subscription.SubscriptionService;
import app.service.wallet.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService
{
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private SubscriptionService subscriptionService;
    private WalletService walletService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       SubscriptionService subscriptionService, WalletService walletService)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
    }

    //    public UserDto login(UserLoginRequest userLoginRequest) {
    //        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequest.getUsername());
    //
    //        if (optionalUser.isEmpty() ||
    //                !passwordEncoder.matches(userLoginRequest.getPassword(), optionalUser.get().getPassword())
    //        ) {
    //
    //            throw new RuntimeException("Username or password mismatch!");
    //        }
    //
    //        return UserMapper.toUserDto(optionalUser.get());
    //    }

    public UserDto register(UserRegisterRequest userRegisterRequest)
    {
        //   1.	Account Creation: Validate the username to ensure its unique and store the user’s details securely.
        //   You must consider persisting user’s sensitive data in a secure way!

        userRepository.findByUsername(userRegisterRequest.getUsername()).ifPresent(user ->
        {
            //TODO: Create custom exception e.g. UserAlreadyExistsException
            throw new RuntimeException("User with this username already exists!");
        });

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        userRegisterRequest.setPassword(encodedPassword);

        User userEntity = UserMapper.toUserEntity(userRegisterRequest);

        //  3.Default Subscription Setup: Assign a free subscription to the user upon registration
        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(userEntity);
        userEntity.setSubscriptions(List.of(defaultSubscription));

        //  2.Default Wallet Creation: Automatically create a wallet for the user
        Wallet defaultWallet = walletService.createDefaultWallet(userEntity);
        userEntity.setWallets(List.of(defaultWallet));

        userRepository.save(userEntity);

        return UserMapper.toUserDto(userEntity);
    }

    public List<UserDto> findAll()
    {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getById(UUID id)
    {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));
        return UserMapper.toUserDto(user);
    }

    public UserDto update(String id, EditUserRequest editUserRequest)
    {
        User entity = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));

        // Update the user entity with the new information
        entity.setFirstName(editUserRequest.getFirstName());
        entity.setLastName(editUserRequest.getLastName());
        entity.setProfilePicture(editUserRequest.getProfilePicture());
        entity.setEmail(editUserRequest.getEmail());

        User updatedUser = userRepository.save(entity);

        return UserMapper.toUserDto(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers()
    {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void switchStatus(UUID id)
    {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void switchRole(UUID id)
    {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(id)));

        if(user.getRole() == UserRole.USER)
        {
            user.setRole(UserRole.ADMIN);
        }
        else
        {
            user.setRole(UserRole.USER);
        }
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return AuthenticationUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }
}
