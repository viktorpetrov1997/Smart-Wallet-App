package app.web;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.service.user.AuthenticationUserDetails;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController
{
    private final UserService userService;

    public IndexController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index()
    {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage()
    {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("userLoginRequest", userLoginRequest);

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage()
    {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("userRegisterRequest", userRegisterRequest);

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid UserRegisterRequest userRegisterRequest, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
        {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register");
            return modelAndView;
        }

        userService.register(userRegisterRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationUserDetails principal)
    {
        //        AuthenticationUserDetails principal = (AuthenticationUserDetails) SecurityContextHolder
        //                .getContext()
        //                .getAuthentication()
        //                .getPrincipal();

        UserDto user = userService.getById(principal.getId());

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
