package app.web.wallet;

import app.model.dto.transaction.TransactionDto;
import app.model.dto.user.UserDto;
import app.model.dto.wallet.WalletDto;
import app.service.user.AuthenticationUserDetails;
import app.service.user.UserService;
import app.service.wallet.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallets")
public class WalletController
{
    private final UserService userService;
    private final WalletService walletService;

    public WalletController(UserService userService, WalletService walletService)
    {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ModelAndView getWallets(@AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        UserDto user = userService.getById(principal.getId());
        List<WalletDto> wallets = user.getWallets();

        Map<UUID, List<TransactionDto>> lastFourTransactions = walletService.getLastFourTransactions(wallets);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallets");
        modelAndView.addObject("wallets", wallets);
        modelAndView.addObject("lastFourTransactions", lastFourTransactions);

        return modelAndView;
    }

    @PutMapping("/{walletId}/balance/top-up")
    public ModelAndView topUpWallet(@PathVariable String walletId)
    {
        TransactionDto transaction = walletService.topUp(UUID.fromString(walletId), BigDecimal.valueOf(20));

        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }

    @PutMapping("/{walletId}/status")
    public ModelAndView switchWalletStatus(@PathVariable String walletId,
                                           @AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        walletService.switchStatus(walletId, principal.getId());

        return new ModelAndView("redirect:/wallets");
    }
}
