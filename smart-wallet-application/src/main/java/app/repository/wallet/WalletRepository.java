package app.repository.wallet;

import app.model.entity.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID>
{
    List<Wallet> findAllByOwner_Username(String ownerUsername);
}
