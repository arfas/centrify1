package com.example.redditoauth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/my-posts")
    @ResponseBody
    public String myPosts(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "You are not logged in.";
        }
        return redditService.getUserSubmittedPosts(principal);
    }
}
