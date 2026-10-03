package app.web.transaction;

import app.model.dto.transaction.TransactionDto;
import app.service.transaction.TransactionService;
import app.service.user.AuthenticationUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transactions")
public class TransactionController
{
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ModelAndView getAllTransactions(@AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        ModelAndView modelAndView = new ModelAndView("transactions");

        modelAndView.addObject("transactions", transactionService.getAllByOwnerId(principal.getId()));

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView showTransaction(@PathVariable String id)
    {
        TransactionDto transaction = transactionService.getById(id);

        ModelAndView modelAndView = new ModelAndView("transaction-result");
        modelAndView.addObject("transaction", transaction);
        return modelAndView;
    }
}
