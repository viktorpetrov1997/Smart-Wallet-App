package app.web.transfer;

import app.model.dto.transaction.TransactionDto;
import app.model.dto.transfer.TransferRequest;
import app.model.dto.user.UserDto;
import app.service.user.AuthenticationUserDetails;
import app.service.user.UserService;
import app.service.wallet.WalletService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transfers")
public class TransferController
{
    private final UserService userService;
    private final WalletService walletService;

    public TransferController(UserService userService, WalletService walletService)
    {
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public ModelAndView getTransfersPage(@AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        UserDto user = userService.getById(principal.getId());
        ModelAndView modelAndView = new ModelAndView("transfer");
        modelAndView.addObject("user", user);
        modelAndView.addObject("transferRequest", TransferRequest.builder().build());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView initiateTransfer(@Valid TransferRequest transferRequest,
                                         BindingResult bindingResult,
                                         @AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        UserDto user = userService.getById(principal.getId());

        if(bindingResult.hasErrors())
        {
            ModelAndView modelAndView = new ModelAndView("transfer");
            modelAndView.addObject("transferRequest", transferRequest);
            modelAndView.addObject("user", user);

            return modelAndView;
        }

        TransactionDto transaction = walletService.transferFunds(user, transferRequest);
        return new ModelAndView("redirect:/transactions/" + transaction.getId());
    }
}
