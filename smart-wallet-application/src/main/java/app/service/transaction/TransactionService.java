package app.service.transaction;

import app.mapper.transaction.TransactionMapper;
import app.model.dto.transaction.TransactionDto;
import app.model.dto.wallet.WalletDto;
import app.model.entity.transaction.Transaction;
import app.model.entity.transaction.TransactionStatus;
import app.model.entity.transaction.TransactionType;
import app.model.entity.user.User;
import app.repository.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService
{
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository)
    {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(User owner,
                                            String sender,
                                            String receiver,
                                            BigDecimal amount,
                                            BigDecimal balanceLeft,
                                            Currency currency,
                                            TransactionType transactionType,
                                            TransactionStatus transactionStatus,
                                            String description,
                                            String failureReason

    )
    {
        Transaction transaction = Transaction.builder()
                .owner(owner)
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .balanceLeft(balanceLeft)
                .currency(currency)
                .type(transactionType)
                .status(transactionStatus)
                .description(description)
                .failureReason(failureReason)
                .createdOn(java.time.LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public TransactionDto getById(String id)
    {
        Transaction transaction = transactionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        return TransactionMapper.toDto(transaction);
    }

    public List<TransactionDto> getAllByOwnerId(UUID ownerId)
    {
        List<Transaction> transactions = transactionRepository.findAllByOwner_Id(ownerId);

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .sorted(Comparator.comparing(TransactionDto::getCreatedOn).reversed())
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getLastFourTransactionsByWallet(WalletDto wallet)
    {
        return transactionRepository.findAllBySenderOrReceiverOrderByCreatedOnDesc(
                        wallet.getId().toString(), wallet.getId().toString())
                .stream()
                .filter(t -> t.getOwner().getId() == wallet.getOwner().getId())
                .filter(t -> t.getStatus() == TransactionStatus.SUCCEEDED)
                .limit(4)
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
