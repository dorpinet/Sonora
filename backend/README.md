# SONORA Backend

Node.js + Express + MongoDB backend for SONORA music app.

## Features

- **Auth**: Registration/Login with OTP via SendGrid (6-digit code, 10 min expiry)
- **JWT**: Access token (15 min) + Refresh token (7 days)
- **Spotify**: Client Credentials — public search + 30-sec previews, no user login needed
- **Daily Playlists**: Auto-generated at 00:00 based on 4 interest categories
- **Notes (Sheet Music)**: Browse piano notes with covers
- **Favorites**: Save tracks and notes to favorites
- **QR Codes**: Generate shareable QR codes for user playlists
- **Rate Limiting**: 200 req/15min general, 10 req/15min for auth
- **Security**: Helmet, CORS, input validation

## Tech Stack

- Express.js
- Mongoose (MongoDB Atlas)
- JWT (jsonwebtoken)
- bcryptjs
- SendGrid (@sendgrid/mail)
- spotify-web-api-node
- node-cron
- qrcode
- express-rate-limit + helmet + cors

## Interest Categories

When users select interests, they choose from 4 categories. Daily playlists are generated from matching genres:

| Interest | Genres | Vibe |
|----------|--------|------|
| **Sport** | Rage, Drain | High-energy for workouts |
| **Meditation** | City Pop, Dreamwave, Darkwave | Chill, relaxing |
| **Party** | Hyperpop, Electropop, Witch House | Fun, upbeat |
| **Focus** | Breakcore, Electronic, Witch House | Intense concentration |

Each day at 00:00, a random selection of 5-7 tracks from the matching genres is compiled into a daily playlist.

## Quick Start

### 1. Install

```bash
cd backend
npm install
```

### 2. Environment

Copy `.env.example` to `.env` and fill in:

```bash
cp .env.example .env
```

Required variables:
- `MONGO_URI` — your MongoDB Atlas connection string
- `JWT_SECRET` — random string for JWT signing
- `JWT_REFRESH_SECRET` — random string for refresh tokens
- `SENDGRID_API_KEY` — from SendGrid dashboard
- `SENDGRID_FROM_EMAIL` — verified sender email
- `SPOTIFY_CLIENT_ID` — from Spotify Developer Dashboard
- `SPOTIFY_CLIENT_SECRET` — from Spotify Developer Dashboard
- `SPOTIFY_REDIRECT_URI` — `http://localhost:3000/api/spotify/callback` for dev
- `FRONTEND_URL` — for CORS (e.g. `http://localhost` for JavaFX dev)

### 3. Run

```bash
# Development
npm run dev

# Production
npm start
```

Server starts on `http://localhost:3000`

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register-init` | Send OTP to email |
| POST | `/api/auth/register-verify` | Verify OTP + create account |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout (clear tokens) |
| POST | `/api/auth/resend-otp` | Resend OTP |
| GET | `/api/auth/me` | Get current user |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get profile |
| PUT | `/api/users/me` | Update profile |
| POST | `/api/users/avatar` | Update avatar URL |
| PUT | `/api/users/password` | Change password |
| POST | `/api/users/interests` | Save interests (sport, gym, study, etc.) |

### Tracks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tracks` | List tracks (filter by genre, artist, search) |
| GET | `/api/tracks/:id` | Get track details |
| GET | `/api/tracks/genres` | List all genres |
| GET | `/api/tracks/spotify/search?q=...` | Search Spotify |
| GET | `/api/tracks/spotify/:id/preview` | Get 30-sec preview URL |

### Notes (Sheet Music)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notes` | List notes |
| GET | `/api/notes/:id` | Get note details |
| GET | `/api/notes/artists` | List note artists |

### Playlists
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/playlists` | User playlists |
| GET | `/api/playlists/daily` | Get/generate daily playlist |
| POST | `/api/playlists` | Create playlist |
| GET | `/api/playlists/:id` | Get playlist |
| PUT | `/api/playlists/:id` | Update playlist |
| DELETE | `/api/playlists/:id` | Delete playlist |
| POST | `/api/playlists/:id/generate-qr` | Generate QR code |
| POST | `/api/playlists/regenerate-daily` | Force regenerate daily |

### Spotify (No login required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/spotify/search?q=...` | Search tracks publicly |
| GET | `/api/spotify/preview/:id` | Get 30-sec preview URL |

### Favorites
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/favorites` | List favorites |
| POST | `/api/favorites` | Add to favorites |
| DELETE | `/api/favorites/:itemId` | Remove from favorites |
| GET | `/api/favorites/check/:itemId` | Check if favorited |

## Deploy

Recommended free hosting: [Render](https://render.com) or [Railway](https://railway.app)

On Render:
1. Connect GitHub repo
2. Set Root Directory: `backend`
3. Build Command: `npm install`
4. Start Command: `npm start`
5. Add Environment Variables from `.env`

## Project Structure

```
backend/
├── src/
│   ├── app.js              # Entry point
│   ├── config/
│   │   └── db.js           # MongoDB connection
│   ├── controllers/        # Business logic
│   ├── middleware/         # Auth, error handling
│   ├── models/             # Mongoose schemas
│   ├── routes/             # API routes
│   ├── services/           # External integrations
│   └── utils/              # Helpers
├── package.json
├── .env.example
└── .gitignore
```
