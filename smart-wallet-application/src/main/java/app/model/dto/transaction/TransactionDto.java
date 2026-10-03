package app.model.dto.transaction;

import app.model.dto.user.UserDto;
import app.model.entity.transaction.TransactionStatus;
import app.model.entity.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
@Builder
public class TransactionDto
{
    private UUID id;
    private UserDto owner;
    private String sender;
    private String receiver;
    private BigDecimal amount;
    private BigDecimal balanceLeft;
    private Currency currency;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private String failureReason;
    private LocalDateTime createdOn;
}
