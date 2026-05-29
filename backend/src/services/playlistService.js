const Track = require('../models/Track');
const Playlist = require('../models/Playlist');
const cron = require('node-cron');

class PlaylistService {

  static CATEGORY_MAP = {
    'sport': {
      genres: ['Rage', 'Drain'],
      description: 'High-energy tracks for workouts and sports',
    },
    'meditation': {
      genres: ['City Pop', 'Dreamwave', 'Darkwave'],
      description: 'Chill vibes for relaxation and meditation',
    },
    'party': {
      genres: ['Hyperpop', 'Electropop', 'Witch House'],
      description: 'Upbeat tracks for parties and fun',
    },
    'focus': {
      genres: ['Breakcore', 'Electronic', 'Witch House'],
      description: 'Intense concentration music for coding and deep work',
    },
  };


  static async generateDailyPlaylist(userId, interests = []) {

    const primaryInterest = interests.length > 0 ? interests[0].toLowerCase() : 'focus';
    const category = this.CATEGORY_MAP[primaryInterest] || this.CATEGORY_MAP['focus'];
    const targetGenres = category.genres;



    const tracks = await Track.aggregate([
      { $match: { genre: { $in: targetGenres } } },
      { $sample: { size: 7 } },
    ]);


    if (tracks.length < 5) {
      const additional = await Track.aggregate([
        { $match: { genre: { $nin: targetGenres } } },
        { $sample: { size: 5 - tracks.length } },
      ]);
      tracks.push(...additional);
    }


    const playlistTracks = tracks.map(t => ({
      trackId: t._id,
      title: t.title,
      artist: t.artist,
      coverUrl: t.coverUrl,
      addedAt: new Date(),
    }));


    const now = new Date();
    const hour = now.getHours();
    let timeLabel = '';
    if (hour >= 5 && hour < 12) timeLabel = 'Morning';
    else if (hour >= 12 && hour < 17) timeLabel = 'Afternoon';
    else if (hour >= 17 && hour < 22) timeLabel = 'Evening';
    else timeLabel = 'Night';

    const interestLabel = primaryInterest.charAt(0).toUpperCase() + primaryInterest.slice(1);
    const name = `${timeLabel} ${interestLabel}`;


    await Playlist.deleteOne({ userId, type: 'daily' });


    const playlist = await Playlist.create({
      userId,
      name,
      description: category.description,
      type: 'daily',
      genre: targetGenres.join(', '),
      tracks: playlistTracks,
      generatedDate: now,
    });

    return playlist;
  }


  static async generateDailyForAllUsers() {
    const User = require('../models/User');
    const users = await User.find({
      interests: { $exists: true, $not: { $size: 0 } },
    });

    console.log(`[CRON] Generating daily playlists for ${users.length} users...`);
    let count = 0;
    for (const user of users) {
      try {
        await this.generateDailyPlaylist(user._id, user.interests);
        count++;
      } catch (err) {
        console.error(`[CRON] Failed for user ${user._id}:`, err.message);
      }
    }
    console.log(`[CRON] Generated ${count} daily playlists.`);
    return count;
  }


  static scheduleDailyGeneration() {
    cron.schedule('0 0 * * *', async () => {
      console.log('[CRON] Starting daily playlist generation...');
      try {
        await this.generateDailyForAllUsers();
      } catch (err) {
        console.error('[CRON] Error:', err);
      }
    }, {
      timezone: 'Europe/Kiev',
    });
    console.log('[CRON] Daily playlists scheduled for 00:00 Europe/Kiev');
  }
}

module.exports = PlaylistService;
