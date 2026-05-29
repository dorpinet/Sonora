# SONORA Deployment Guide

## 1. Generate Secrets (JWT Keys)

Before anything, generate your JWT secrets:

```bash
cd backend
node scripts/generate-secrets.js
```

Copy the output into your `.env` file.

## 2. Fill .env

Create `backend/.env` from `.env.example`:

```bash
cp backend/.env.example backend/.env
```

Fill in these values:

| Variable | Where to get | Example |
|----------|-------------|---------|
| `MONGO_URI` | MongoDB Atlas → Database → Connect → Drivers → Node.js | `mongodb+srv://user:pass@cluster.mongodb.net/sonora?retryWrites=true&w=majority` |
| `JWT_SECRET` | Run `node scripts/generate-secrets.js` | `a3f8b2...` (64+ chars) |
| `JWT_REFRESH_SECRET` | Run same script | `c7e1d9...` (64+ chars) |
| `SENDGRID_API_KEY` | SendGrid Dashboard → Settings → API Keys → Create API Key | `SG.xxx...` |
| `SENDGRID_FROM_EMAIL` | Must be verified in SendGrid | `sonora@app.com` |
| `SPOTIFY_CLIENT_ID` | [developer.spotify.com](https://developer.spotify.com/dashboard) → Create App | `abc123...` |
| `SPOTIFY_CLIENT_SECRET` | Same Spotify app page | `xyz789...` |
| `FRONTEND_URL` | Your deployed frontend URL (for now use `*`) | `*` |
| `PORT` | Render will override this | `3000` |

## 3. Test Locally

```bash
cd backend
npm install
npm start
```

Check:
- `http://localhost:3000/health` → should return `{"status":"ok"}`
- Use Postman/Insomnia to test `/api/auth/register-init`

## 4. Deploy Backend (Render)

### Step 1: Push to GitHub
Make sure your code is in a GitHub repo.

### Step 2: Create Web Service on Render
1. Go to [render.com](https://render.com) → New → Web Service
2. Connect your GitHub repo
3. Configure:
   - **Name**: `sonora-api`
   - **Root Directory**: `backend`
   - **Environment**: `Node`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
4. Add Environment Variables (copy everything from `.env`)
5. Select **Free** plan
6. Click **Create Web Service**

### Step 3: Wait for Deploy
Render will:
- Install dependencies (`npm install`)
- Start server (`npm start`)
- Give you a URL: `https://sonora-api.onrender.com`

### Step 4: Test Deployed API
Open in browser:
```
https://sonora-api.onrender.com/health
```
Should return `{"status":"ok"}`.

### Step 5: Update Frontend Config
In your JavaFX code, set the API URL:

```java
// For local development
private static final String API_BASE = "http://localhost:3000/api";

// For production (after Render deploy)
private static final String API_BASE = "https://sonora-api.onrender.com/api";
```

## 5. Important Notes

### Free Tier Limitations (Render)
- **Sleeping**: After 15 min of inactivity, the server sleeps. First request after sleep takes ~30 seconds.
- **Solution**: The JavaFX frontend sends a ping on startup to wake the server.

### MongoDB Atlas
- **IP Whitelist**: Add `0.0.0.0/0` to Network Access (so Render can connect).
- **Database**: Make sure `sonora` database exists with `tracks` and `notes` collections.

### SendGrid
- Must verify sender email before sending.
- Free tier: 100 emails/day.

### Spotify
- Client Credentials work without user login.
- 30-second previews are free and public.

## 6. Frontend Distribution (JavaFX)

For your coursework submission:
1. Build JAR: `mvn clean package`
2. The JAR will be in `target/sonora-1.0.jar`
3. Include JAR in GitHub release or submission folder
4. Add `README.md` to root explaining how to run:
   ```bash
   java -jar sonora-1.0.jar
   ```

The app connects to deployed backend automatically.

## 7. Troubleshooting

| Problem | Solution |
|---------|----------|
| MongoDB connection error | Check `MONGO_URI` and Atlas IP whitelist |
| SendGrid emails not sending | Verify sender email in SendGrid |
| CORS errors | Update `FRONTEND_URL` in Render env vars |
| Server sleeps on Render | Send a ping request on app startup |
| JWT errors | Regenerate secrets with `generate-secrets.js` |
