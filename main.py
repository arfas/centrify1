import os
import requests
from fastapi import FastAPI, Request, Depends
from fastapi.responses import RedirectResponse
from itsdangerous import URLSafeSerializer
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

# Configuration
REDDIT_CLIENT_ID = os.getenv("REDDIT_CLIENT_ID")
REDDIT_CLIENT_SECRET = os.getenv("REDDIT_CLIENT_SECRET")
REDIRECT_URI = "http://127.0.0.1:8000/callback"
SECRET_KEY = "your_secret_key"  # Replace with a real secret key in a production environment

# Session management
serializer = URLSafeSerializer(SECRET_KEY)

def get_session(request: Request):
    return request.cookies.get("session")

def set_session(response, data):
    response.set_cookie("session", serializer.dumps(data))

@app.get("/login")
async def login():
    # Note: The 'duration=permanent' part of the scope is what makes Reddit
    # issue a refresh token.
    scope = "identity read history"
    # Generate a random state to prevent CSRF attacks
    state = serializer.dumps({"action": "login"})
    # Redirect the user to Reddit's authorization page
    return RedirectResponse(
        f"https://www.reddit.com/api/v1/authorize?client_id={REDDIT_CLIENT_ID}"
        f"&response_type=code&state={state}&redirect_uri={REDIRECT_URI}&duration=permanent&scope={scope}"
    )

@app.get("/callback")
async def callback(request: Request, code: str, state: str):
    # Verify the state to prevent CSRF attacks
    try:
        unsigned_state = serializer.loads(state)
        if unsigned_state.get("action") != "login":
            return {"error": "Invalid state"}
    except:
        return {"error": "Invalid state"}

    # Exchange the authorization code for an access token
    auth = requests.auth.HTTPBasicAuth(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET)
    post_data = {
        "grant_type": "authorization_code",
        "code": code,
        "redirect_uri": REDIRECT_URI,
    }
    headers = {"User-Agent": "MyFastAPIApp/0.0.1"}
    response = requests.post(
        "https://www.reddit.com/api/v1/access_token",
        auth=auth,
        data=post_data,
        headers=headers,
    )
    token_data = response.json()

    # Store the access token in the session
    response = RedirectResponse(url="/me")
    set_session(response, {"access_token": token_data["access_token"]})
    return response

@app.get("/me")
async def get_user_identity(session: str = Depends(get_session)):
    if not session:
        return RedirectResponse(url="/login")

    data = serializer.loads(session)
    access_token = data.get("access_token")

    if not access_token:
        return RedirectResponse(url="/login")

    headers = {"Authorization": f"bearer {access_token}", "User-Agent": "MyFastAPIApp/0.0.1"}
    response = requests.get("https://oauth.reddit.com/api/v1/me", headers=headers)

    return response.json()

@app.get("/my-posts")
async def my_posts(session: str = Depends(get_session)):
    if not session:
        return RedirectResponse(url="/login")

    data = serializer.loads(session)
    access_token = data.get("access_token")

    if not access_token:
        return RedirectResponse(url="/login")

    # Get the username from the /api/v1/me endpoint
    headers = {"Authorization": f"bearer {access_token}", "User-Agent": "MyFastAPIApp/0.0.1"}
    response = requests.get("https://oauth.reddit.com/api/v1/me", headers=headers)
    username = response.json()["name"]

    # Fetch the user's submitted posts
    headers = {"Authorization": f"bearer {access_token}", "User-Agent": "MyFastAPIApp/0.0.1"}
    response = requests.get(f"https://oauth.reddit.com/user/{username}/submitted", headers=headers)

    return response.json()
