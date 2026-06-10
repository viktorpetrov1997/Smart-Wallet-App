package app.model.dto.wallet;

import app.model.entity.user.User;
import app.model.entity.wallet.WalletStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
@Builder
public class WalletDto
{
    private UUID id;

    private User owner;

    private WalletStatus status;

    private BigDecimal balance;

    private Currency currency;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
