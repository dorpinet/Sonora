const Track = require('../models/Track');
const Playlist = require('../models/Playlist');
const cron = require('node-cron');

class PlaylistService {

  static CATEGORY_MAP = {
    'sport': {
      genres: ['Rage', 'Drain'],
    },
    'meditation': {
      genres: ['City Pop', 'Dreamwave', 'Darkwave'],
    },
    'party': {
      genres: ['Hyperpop', 'Electropop', 'Witch House'],
    },
    'focus': {
      genres: ['Breakcore', 'Electronic', 'Witch House'],
    },
  };


  static async generateDailyPlaylist(userId, interests = []) {
    const primaryInterest = interests.length > 0 ? interests[0].toLowerCase() : 'focus';
    const category = this.CATEGORY_MAP[primaryInterest] || this.CATEGORY_MAP['focus'];
    const targetGenres = category.genres;

    let tracks = await Track.aggregate([
      { $match: { genre: { $in: targetGenres } } },
      { $sample: { size: 7 } },
    ]);

    if (tracks.length < 5) {
      const additional = await Track.aggregate([
        { $match: { genre: { $nin: targetGenres } } },
        { $sample: { size: 7 - tracks.length } },
      ]);
      tracks.push(...additional);
    }

    if (tracks.length < 5) {
      const fallback = await Track.aggregate([
        { $sample: { size: 7 - tracks.length } },
      ]);
      tracks.push(...fallback);
    }

    const playlistTracks = tracks.map(t => ({
      trackId: t._id,
      title: t.title,
      artist: t.artist,
      coverUrl: t.coverUrl,
      addedAt: new Date(),
    }));

    await Playlist.deleteOne({ userId, type: 'daily' });

    const playlist = await Playlist.create({
      userId,
      name: 'Daily Mix',
      description: 'Explore new tracks!',
      type: 'daily',
      genre: targetGenres.join(', '),
      tracks: playlistTracks,
      generatedDate: new Date(),
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
