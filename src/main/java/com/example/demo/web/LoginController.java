package com.example.demo.web;

import com.example.demo.model.service.UserLoginServiceModel;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class LoginController {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @ModelAttribute("userModel")
    public UserLoginServiceModel userModel() {
        return new UserLoginServiceModel();
    }


    @GetMapping("/users/login")
    public String showLogin() {
        return "auth-login";
    }

    @PostMapping("/users/login-error")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String userName,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, userName);
        redirectAttributes.addFlashAttribute("bad_credentials", true);

        return "redirect:/users/login";
    }
    //todo не се използва този логин ами на Spring security имплементацията
    @PostMapping("/users/login")
    public String login(@Valid @ModelAttribute UserLoginServiceModel userModel,
                        BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        boolean hasErrors = bindingResult.hasErrors();
        LOGGER.info("User tried to login. User with username {} tried to login. Success {}?",
                userModel.getUsername(), !hasErrors);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userModel", userModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userModel", bindingResult);
            return "redirect:/users/login";
        }
        if (userService.authenticate(userModel.getUsername(), userModel.getPassword())) {
            userService.login(userModel);
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("userModel", userModel);
            redirectAttributes.addFlashAttribute("notFound", true);

            return "redirect:/users/login";
        }
    }

}