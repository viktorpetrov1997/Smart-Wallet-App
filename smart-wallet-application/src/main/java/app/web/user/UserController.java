package app.web.user;

import app.model.dto.user.EditUserRequest;
import app.model.dto.user.UserDto;
import app.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController
{
    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView profile(@PathVariable String id)
    {
        UserDto user = userService.getById(UUID.fromString(id));

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView profile(@PathVariable  String id, @ModelAttribute EditUserRequest editUserRequest)
    {
        UserDto updatedUser = userService.update(id, editUserRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", updatedUser);

        return modelAndView;
    }
}
