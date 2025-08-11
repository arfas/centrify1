package com.example.redditoauth;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RedditService {

    private final WebClient webClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public RedditService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.webClient = WebClient.builder()
                .baseUrl("https://oauth.reddit.com")
                .build();
    }

    public String getUserSubmittedPosts(OAuth2User principal) {
        OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient(
                "reddit",
                principal.getName()
        );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String username = principal.getAttribute("name");
        System.out.println("accessToken = " + accessToken.getTokenValue());

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user/{username}/submitted").build(username))
                .headers(headers -> headers.setBearerAuth(accessToken.getTokenValue()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
