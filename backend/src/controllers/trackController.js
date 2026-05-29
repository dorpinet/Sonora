const Track = require('../models/Track');
const Favorite = require('../models/Favorite');
const SpotifyService = require('../services/spotifyService');

class TrackController {

  static async getAll(req, res, next) {
    try {
      const { genre, artist, search, page = 1, limit = 50 } = req.query;
      const query = {};

      if (genre) query.genre = new RegExp(genre, 'i');
      if (artist) query.artist = new RegExp(artist, 'i');
      if (search) {
        query.$or = [
          { title: new RegExp(search, 'i') },
          { artist: new RegExp(search, 'i') },
        ];
      }

      const skip = (parseInt(page) - 1) * parseInt(limit);
      const tracks = await Track.find(query)
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(parseInt(limit));

      const total = await Track.countDocuments(query);

      res.status(200).json({ tracks, total, page: parseInt(page), totalPages: Math.ceil(total / limit) });
    } catch (error) {
      next(error);
    }
  }


  static async getById(req, res, next) {
    try {
      const track = await Track.findById(req.params.id);
      if (!track) return res.status(404).json({ error: 'Track not found.' });


      let isFavorite = false;
      if (req.user) {
        const fav = await Favorite.findOne({ userId: req.user._id, itemId: track._id, itemType: 'track' });
        isFavorite = !!fav;
      }

      res.status(200).json({ track, isFavorite });
    } catch (error) {
      next(error);
    }
  }


  static async getGenres(req, res, next) {
    try {
      const genres = await Track.distinct('genre');
      res.status(200).json({ genres });
    } catch (error) {
      next(error);
    }
  }


  static async searchSpotify(req, res, next) {
    try {
      const { q, limit = 20 } = req.query;
      if (!q) return res.status(400).json({ error: 'Query parameter "q" is required.' });


      await SpotifyService.ensureClientCredentials();
      const results = await SpotifyService.searchTracks(q, { limit: parseInt(limit) });

      res.status(200).json({ tracks: results.tracks?.items || [] });
    } catch (error) {
      next(error);
    }
  }


  static async getSpotifyPreview(req, res, next) {
    try {
      const { id } = req.params;
      await SpotifyService.ensureClientCredentials();
      const previewUrl = await SpotifyService.getTrackPreview(id);

      if (!previewUrl) {
        return res.status(404).json({ error: 'Preview not available for this track.' });
      }

      res.status(200).json({ previewUrl });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = TrackController;
