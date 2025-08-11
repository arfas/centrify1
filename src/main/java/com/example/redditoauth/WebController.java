package com.example.redditoauth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class WebController {

    private final RedditService redditService;

    public WebController(RedditService redditService) {
        this.redditService = redditService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/success")
    public String success(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("user", principal.getAttributes());
        model.addAttribute("access_token", principal.getAttribute("access_token"));
        return "success";
    }

    @GetMapping("/my-posts")
    public String myPosts(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String posts = redditService.getUserSubmittedPosts(principal);
        model.addAttribute("posts", posts);
        return "my-posts";
    }
}
