package app.service.transaction;

import app.model.entity.transaction.Transaction;
import app.model.entity.transaction.TransactionStatus;
import app.model.entity.transaction.TransactionType;
import app.model.entity.user.User;
import app.repository.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;

@Service
public class TransactionService
{
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository)
    {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(User owner, String sender, String receiver, BigDecimal amount,
                    BigDecimal balanceLeft, Currency currency, TransactionType transactionType,
                    TransactionStatus transactionStatus, String description, String failureReason)
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
}
