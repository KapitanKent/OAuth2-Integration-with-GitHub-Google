package com.AbadianoKent.Oauth2.controller;

import com.AbadianoKent.Oauth2.model.User;
import com.AbadianoKent.Oauth2.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        if (oauth2User != null) {
            Object id = oauth2User.getAttribute("id");
            if (id != null) {
                Optional<User> u = userRepository.findById(Long.valueOf(id.toString()));
                if (u.isPresent()) {
                    model.addAttribute("user", u.get());
                    return "profile";
                }
            }
        }
        return "redirect:/";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal OAuth2User oauth2User,
                                @RequestParam String displayName,
                                @RequestParam(required = false) String bio) {
        if (oauth2User != null) {
            Object id = oauth2User.getAttribute("id");
            if (id != null) {
                Optional<User> u = userRepository.findById(Long.valueOf(id.toString()));
                if (u.isPresent()) {
                    User user = u.get();
                    user.setDisplayName(displayName);
                    user.setBio(bio);
                    userRepository.save(user);
                }
            }
        }
        return "redirect:/profile";
    }
}
