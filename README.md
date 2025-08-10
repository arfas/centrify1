# Spring Boot Reddit OAuth Example

This project demonstrates how to implement Reddit OAuth login in a Spring Boot backend.

## Setup

1.  **Install dependencies:**
    This project uses Maven, so you don't need to install dependencies manually. They will be downloaded when you build the project.

2.  **Create a Reddit OAuth2 application:**
    - Go to [Reddit's app preferences](https://www.reddit.com/prefs/apps).
    - Click "are you a developer? create an app...".
    - Fill out the form:
        - **name:** Give your app a name.
        - **type:** Select "web app".
        - **description:** (Optional)
        - **about url:** (Optional)
        - **redirect uri:** `http://127.0.0.1:8080/login/oauth2/code/reddit`
    - Click "create app". You will get a client ID and client secret.

3.  **Configure environment variables:**
    - Open the `.env` file and replace the placeholder values with your Reddit client ID and client secret.

## Running the application

```bash
mvn spring-boot:run
```

The application will be available at `http://127.0.0.1:8080`.

## Endpoints

-   `/`: The home page with a link to log in.
-   `/login`: Initiates the Reddit OAuth2 login flow (handled by Spring Security).
-   `/my-posts`: Fetches the authenticated user's submitted posts from Reddit.
