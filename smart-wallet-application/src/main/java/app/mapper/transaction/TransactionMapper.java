package app.mapper.transaction;

import app.mapper.user.UserMapper;
import app.model.dto.transaction.TransactionDto;
import app.model.entity.transaction.Transaction;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransactionMapper
{
    public static TransactionDto toDto(Transaction transaction)
    {
        return TransactionDto.builder()
                .id(transaction.getId())
                .owner(UserMapper.toUserDto(transaction.getOwner()))
                .sender(transaction.getSender())
                .receiver(transaction.getReceiver())
                .amount(transaction.getAmount())
                .balanceLeft(transaction.getBalanceLeft())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .failureReason(transaction.getFailureReason())
                .createdOn(transaction.getCreatedOn())
                .build();
    }
}
