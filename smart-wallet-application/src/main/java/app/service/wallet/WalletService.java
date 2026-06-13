package app.service.wallet;

import app.model.entity.transaction.Transaction;
import app.model.entity.transaction.TransactionStatus;
import app.model.entity.transaction.TransactionType;
import app.model.entity.user.User;
import app.model.entity.wallet.Wallet;
import app.model.entity.wallet.WalletStatus;
import app.repository.wallet.WalletRepository;
import app.service.transaction.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService
{
    private WalletRepository walletRepository;
    TransactionService transactionService;

    public WalletService(WalletRepository walletRepository, TransactionService transactionService)
    {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Transaction topUp(UUID walletId, BigDecimal amount)
    {
        Optional<Wallet> optionalWallet = walletRepository.findById(walletId);

        if(optionalWallet.isEmpty())
        {
            throw new RuntimeException("Wallet with id [%s] not found.".formatted(walletId));
        }

        Wallet wallet = optionalWallet.get();
        String description = "Top Up Wallet with %.2f".formatted(amount);

        if(wallet.getStatus().equals(WalletStatus.INACTIVE))
        {
            return transactionService.createNewTransaction(
                    wallet.getOwner(),
                    "Some Sender",
                    wallet.getId().toString(),
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.DEPOSIT,
                    TransactionStatus.FAILED,
                    description,
                    "Wallet is inactive. Please contact support for more details."
            );
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletRepository.save(wallet);

        return transactionService.createNewTransaction(
                wallet.getOwner(),
                "Some Sender",
                wallet.getId().toString(),
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                description,
                null
        );
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

//    public void createNewWallet(User user)
//    {
//        Wallet wallet = Wallet.builder()
//                .owner(user)
//                .currency(Currency.getInstance("EUR"))
//                .balance(BigDecimal.valueOf(0.00))
//                .status(WalletStatus.ACTIVE)
//                .createdOn(LocalDateTime.now())
//                .updatedOn(LocalDateTime.now())
//                .build();
//
//        walletRepository.save(wallet);
//    }
}
