package app.service.wallet;

import app.mapper.transaction.TransactionMapper;
import app.mapper.user.UserMapper;
import app.model.dto.transaction.TransactionDto;
import app.model.dto.transfer.TransferRequest;
import app.model.dto.user.UserDto;
import app.model.dto.wallet.WalletDto;
import app.model.entity.transaction.Transaction;
import app.model.entity.transaction.TransactionStatus;
import app.model.entity.transaction.TransactionType;
import app.model.entity.user.User;
import app.model.entity.wallet.Wallet;
import app.model.entity.wallet.WalletStatus;
import app.repository.wallet.WalletRepository;
import app.service.transaction.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class WalletService
{
    WalletRepository walletRepository;
    TransactionService transactionService;

    public WalletService(WalletRepository walletRepository, TransactionService transactionService)
    {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public TransactionDto topUp(UUID walletId, BigDecimal amount)
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

            Transaction transaction = transactionService.createNewTransaction(wallet.getOwner(),
                    wallet.getOwner().getUsername(),
                    wallet.getId().toString(),
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.DEPOSIT,
                    TransactionStatus.FAILED,
                    description,
                    "Wallet is inactive. Please contact support for more details.");

            return TransactionMapper.toDto(transaction);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletRepository.save(wallet);

        Transaction transaction = transactionService.createNewTransaction(wallet.getOwner(),
                wallet.getOwner().getUsername(),
                wallet.getId().toString(),
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                description,
                null);

        return TransactionMapper.toDto(transaction);
    }

    public Wallet createDefaultWallet(User user)
    {
        Wallet wallet = Wallet.builder()
                .owner(user)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.valueOf(20.00))
                .status(WalletStatus.ACTIVE)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        return wallet;
    }

    public Transaction charge(User user, UUID walledId, BigDecimal amount, String chargeDescription)
    {
        Optional<Wallet> optionalWallet = walletRepository.findById(walledId);

        if(optionalWallet.isEmpty())
        {
            throw new RuntimeException("Wallet with id [%s] not found.".formatted(walledId));
        }

        Wallet wallet = optionalWallet.get();

        if(wallet.getStatus().equals(WalletStatus.INACTIVE))
        {
            return transactionService.createNewTransaction(wallet.getOwner(),
                    wallet.getId().toString(),
                    user.getUsername(),
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    chargeDescription,
                    "Wallet is inactive. Please contact support for more details.");
        }

        if(wallet.getBalance().compareTo(amount) < 0)
        {
            return transactionService.createNewTransaction(wallet.getOwner(), wallet.getId().toString(), user.getUsername(), amount, wallet.getBalance(), wallet.getCurrency(), TransactionType.WITHDRAWAL, TransactionStatus.FAILED, chargeDescription, "Insufficient funds. Please top up your wallet and try again.");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletRepository.save(wallet);

        return transactionService.createNewTransaction(wallet.getOwner(), wallet.getId().toString(), user.getUsername(), amount, wallet.getBalance(), wallet.getCurrency(), TransactionType.WITHDRAWAL, TransactionStatus.SUCCEEDED, chargeDescription, null);
    }

    public TransactionDto transferFunds(UserDto senderDto, TransferRequest transferRequest)
    {
        User sender = UserMapper.toEntity(senderDto);
        Wallet senderWallet = walletRepository.findById(transferRequest.getFromWalletId()).orElseThrow(() -> new RuntimeException("Wallet with id [%s] not found.".formatted(transferRequest.getFromWalletId())));

        Optional<Wallet> receiver = walletRepository.findAllByOwner_Username(transferRequest.getToUsername()).stream().filter(wallet -> wallet.getStatus().equals(WalletStatus.ACTIVE)).findFirst();

        if(receiver.isEmpty())
        {
            Transaction transaction = transactionService.createNewTransaction(sender,
                    senderWallet.getId().toString(),
                    transferRequest.getToUsername(),
                    transferRequest.getAmount(),
                    senderWallet.getBalance(),
                    senderWallet.getCurrency(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    "Transfer to %s failed.".formatted(transferRequest.getToUsername()),
                    "Receiver does not have an active wallet. Please ask the receiver to create an active wallet and try again.");

            return TransactionMapper.toDto(transaction);
        }

        Transaction withdrawal = charge(sender, senderWallet.getId(), transferRequest.getAmount(), "Transfer to %s".formatted(transferRequest.getToUsername()));

        if(withdrawal.getStatus().equals(TransactionStatus.FAILED))
        {
            return TransactionMapper.toDto(withdrawal);
        }

        Wallet receiverWallet = receiver.get();
        receiverWallet.setBalance(receiverWallet.getBalance().add(transferRequest.getAmount()));
        walletRepository.save(receiverWallet);

        Transaction transaction = transactionService.createNewTransaction(receiverWallet.getOwner(),
                senderWallet.getId().toString(),
                transferRequest.getToUsername(),
                transferRequest.getAmount(),
                receiverWallet.getBalance(),
                receiverWallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                "Transfer to %s.".formatted(transferRequest.getToUsername()), null);

        return TransactionMapper.toDto(transaction);
    }

    public void switchStatus(String walletId, UUID id)
    {
        Optional<Wallet> optionalWallet = walletRepository.findById(UUID.fromString(walletId));

        if(optionalWallet.isEmpty())
        {
            throw new RuntimeException("Wallet with id [%s] not found.".formatted(walletId));
        }

        Wallet wallet = optionalWallet.get();

        if(!wallet.getOwner().getId().equals(id))
        {
            throw new RuntimeException("You are not authorized to switch the status of this wallet.");
        }

        if(wallet.getStatus().equals(WalletStatus.ACTIVE))
        {
            wallet.setStatus(WalletStatus.INACTIVE);
        }
        else
        {
            wallet.setStatus(WalletStatus.ACTIVE);
        }

        wallet.setUpdatedOn(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    public Map<UUID, List<TransactionDto>> getLastFourTransactions(List<WalletDto> wallets)
    {
        Map<UUID, List<TransactionDto>> transactionsByWalletId = new HashMap<>();

        for(WalletDto wallet : wallets)
        {
            List<TransactionDto> transactions = transactionService.getLastFourTransactionsByWallet(wallet);
            transactionsByWalletId.put(wallet.getId(), transactions);
        }

        return transactionsByWalletId;
    }
}

//    public void createNewWallet(User user) {
//
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

