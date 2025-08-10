# FastAPI Reddit OAuth Example

This project demonstrates how to implement Reddit OAuth login in a FastAPI backend.

## Setup

1.  **Install dependencies:**
    ```bash
    pip install -r requirements.txt
    ```

2.  **Create a Reddit OAuth2 application:**
    - Go to [Reddit's app preferences](https://www.reddit.com/prefs/apps).
    - Click "are you a developer? create an app...".
    - Fill out the form:
        - **name:** Give your app a name.
        - **type:** Select "web app".
        - **description:** (Optional)
        - **about url:** (Optional)
        - **redirect uri:** `http://127.0.0.1:8000/callback`
    - Click "create app". You will get a client ID and client secret.

3.  **Configure environment variables:**
    - Rename `.env.example` to `.env`.
    - Open the `.env` file and replace the placeholder values with your Reddit client ID and client secret.

## Running the application

```bash
uvicorn main:app --reload
```

## Endpoints

-   `/login`: Initiates the Reddit OAuth2 login flow.
-   `/callback`: Handles the callback from Reddit after authorization.
-   `/my-posts`: Fetches the authenticated user's submitted posts from Reddit.
