package app.service.wallet;

import app.model.entity.user.User;
import app.model.entity.wallet.Wallet;
import app.model.entity.wallet.WalletStatus;
import app.repository.wallet.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Service
public class WalletService
{
    private WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository)
    {
        this.walletRepository = walletRepository;
    }

    public Wallet createDefaultWallet(User user)
    {
        Wallet wallet = Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.valueOf(20.00))
                .currency(Currency.getInstance("EUR"))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        return wallet;
    }
}
