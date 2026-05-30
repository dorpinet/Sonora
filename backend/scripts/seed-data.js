const mongoose = require('mongoose');
require('dotenv').config();

const Track = require('../src/models/Track');
const Note = require('../src/models/Note');
const User = require('../src/models/User');
const Playlist = require('../src/models/Playlist');
const bcrypt = require('bcryptjs');

const sampleTracks = [
  { title: 'Cyber Drain', artist: 'Neon Ghost', genre: 'Drain', durationSeconds: 180, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Void Walker', artist: 'Static Noise', genre: 'Drain', durationSeconds: 195, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Data Loss', artist: 'Glitch Corp', genre: 'Drain', durationSeconds: 210, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Rage Quit', artist: 'Hypercore', genre: 'Rage', durationSeconds: 185, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Blood Pump', artist: 'Adrenaline', genre: 'Rage', durationSeconds: 200, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Break Stuff', artist: 'Anger Mgmt', genre: 'Rage', durationSeconds: 175, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'City Lights', artist: 'Dreamwave', genre: 'City Pop', durationSeconds: 220, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Midnight Drive', artist: 'Synthlove', genre: 'City Pop', durationSeconds: 240, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Neon Rain', artist: 'Vapor Soul', genre: 'City Pop', durationSeconds: 190, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Hyper Pop', artist: 'Bubblegum', genre: 'Hyperpop', durationSeconds: 165, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Glitch Princess', artist: '100gecs', genre: 'Hyperpop', durationSeconds: 170, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Crystal Castles', artist: 'Vanished', genre: 'Hyperpop', durationSeconds: 180, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Witch House', artist: 'Salem', genre: 'Witch House', durationSeconds: 210, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Dark Ritual', artist: 'Ritualz', genre: 'Witch House', durationSeconds: 195, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Breakcore Heart', artist: 'Venetian', genre: 'Breakcore', durationSeconds: 185, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Amen Break', artist: 'Squarepusher', genre: 'Breakcore', durationSeconds: 200, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Meditation One', artist: 'Zen Master', genre: 'Meditation', durationSeconds: 300, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Deep Focus', artist: 'Brainwave', genre: 'Meditation', durationSeconds: 320, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Binaural', artist: 'Frequency', genre: 'Meditation', durationSeconds: 280, coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
];

const sampleNotes = [
  { title: 'Moonlight Sonata', artist: 'Beethoven', imageUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', duration: 180, spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Fur Elise', artist: 'Beethoven', imageUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', duration: 200, spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Clair de Lune', artist: 'Debussy', imageUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', duration: 240, spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Nocturne Op.9', artist: 'Chopin', imageUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', duration: 220, spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
  { title: 'Gymnopedie', artist: 'Satie', imageUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', coverUrl: 'https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b', duration: 190, spotifyId: '4uLU6hMCjMIupJG0VmvPZY' },
];

async function seed() {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log('Connected to MongoDB');

    let tracks = await Track.find();
    if (tracks.length === 0) {
      console.log('Inserting sample tracks...');
      await Track.insertMany(sampleTracks);
      tracks = await Track.find();
      console.log(`Inserted ${tracks.length} tracks`);
    } else {
      console.log(`Found ${tracks.length} existing tracks`);
    }

    let notes = await Note.find();
    if (notes.length === 0) {
      console.log('Inserting sample notes...');
      await Note.insertMany(sampleNotes);
      notes = await Note.find();
      console.log(`Inserted ${notes.length} notes`);
    } else {
      console.log(`Found ${notes.length} existing notes`);
    }

    let user = await User.findOne({ email: 'demo@sonora.app' });
    if (!user) {
      console.log('Creating demo user...');
      const hashedPassword = await bcrypt.hash('DemoPass123!', 10);
      user = await User.create({
        email: 'demo@sonora.app',
        passwordHash: hashedPassword,
        username: 'demo_user',
        interests: ['sport', 'meditation'],
        isVerified: true,
      });
      console.log('Created demo user: demo@sonora.app / DemoPass123!');
    } else {
      console.log('Found existing demo user');
    }

    const userPlaylists = await Playlist.find({ userId: user._id });
    if (userPlaylists.length === 0) {
      console.log('Creating user playlists...');

      const likedTracks = tracks.slice(0, 5).map(t => ({
        trackId: String(t._id),
        title: t.title,
        artist: t.artist,
        coverUrl: t.coverUrl,
        addedAt: new Date(),
      }));

      const recentTracks = tracks.slice(3, 8).map(t => ({
        trackId: String(t._id),
        title: t.title,
        artist: t.artist,
        coverUrl: t.coverUrl,
        addedAt: new Date(Date.now() - Math.random() * 86400000),
      }));

      const dailyTracks = tracks.slice(5, 12).map(t => ({
        trackId: String(t._id),
        title: t.title,
        artist: t.artist,
        coverUrl: t.coverUrl,
        addedAt: new Date(),
      }));

      await Playlist.create([
        {
          userId: user._id,
          name: 'Liked',
          description: 'Your favorite tracks',
          type: 'favorites',
          tracks: likedTracks,
          coverUrl: likedTracks[0]?.coverUrl,
        },
        {
          userId: user._id,
          name: 'Recently Played',
          description: 'Tracks you listened to',
          type: 'user_created',
          tracks: recentTracks,
          coverUrl: recentTracks[0]?.coverUrl,
        },
        {
          userId: user._id,
          name: 'For Your Favs',
          description: 'Explore new tracks!',
          type: 'user_created',
          tracks: dailyTracks,
          coverUrl: dailyTracks[0]?.coverUrl,
        },
        {
          userId: user._id,
          name: 'Daily Mix',
          description: 'Explore new tracks!',
          type: 'daily',
          tracks: dailyTracks,
          coverUrl: dailyTracks[0]?.coverUrl,
          generatedDate: new Date(),
        },
      ]);
      console.log('Created 4 user playlists');
    } else {
      console.log(`Found ${userPlaylists.length} existing user playlists`);
    }

    console.log('Seed complete!');
    process.exit(0);
  } catch (err) {
    console.error('Seed failed:', err);
    process.exit(1);
  }
}

seed();
