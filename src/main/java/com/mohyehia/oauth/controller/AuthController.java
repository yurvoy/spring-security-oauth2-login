package com.mohyehia.oauth.controller;

import com.mohyehia.oauth.entity.User;
import com.mohyehia.oauth.entity.form.SignUpRequest;
import com.mohyehia.oauth.service.framework.UserService;
import com.mohyehia.oauth.utils.AppConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String viewLoginPage(){
        return "login";
    }

    @GetMapping("/signup")
    public String viewSignupPage(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveNewUser(@ModelAttribute("signUpRequest") SignUpRequest signUpRequest,
                              Model model,
                              RedirectAttributes attributes) {
        log.info("Submitted data =>" + signUpRequest);
        if(userService.findByEmail(signUpRequest.getEmail()) != null){
            model.addAttribute("signUpRequest", signUpRequest);
            model.addAttribute("error", "Email Address already exists!");
            return "signup";
        }
        // save user to database & redirect to login for now
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setAuthProvider(AppConstant.LOCAL_PROVIDER);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        User savedUser = userService.save(user);
        if(savedUser == null){
            model.addAttribute("signUpRequest", signUpRequest);
            model.addAttribute("error", "An error occurred while performing your request, please try again!");
            return "signup";
        }
        log.info("new user saved successfully!");
        attributes.addFlashAttribute("success", "Your account created successfully, you can now login with your credentials!");
        return "redirect:/login";
    }
}
