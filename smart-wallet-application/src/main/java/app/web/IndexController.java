package app.web;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        modelAndView.addObject("userLoginData", userLoginRequest);

        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@ModelAttribute UserLoginRequest userLoginRequest)
    {
        UserDto user = userService.login(userLoginRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage()
    {
        return new ModelAndView("home");
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
    public ModelAndView registerUser(@ModelAttribute UserRegisterRequest userRegisterRequest)
    {
        userService.register(userRegisterRequest);

        return new ModelAndView("redirect:/login");
    }
}
