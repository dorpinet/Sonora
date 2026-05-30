const Playlist = require('../models/Playlist');
const Track = require('../models/Track');
const QRCode = require('qrcode');
const PlaylistService = require('../services/playlistService');

class PlaylistController {
  static async getAll(req, res, next) {
    try {
      const { type, page = 1, limit = 20 } = req.query;
      const query = { userId: req.user._id };
      if (type) query.type = type;

      const skip = (parseInt(page) - 1) * parseInt(limit);
      const playlists = await Playlist.find(query)
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(parseInt(limit));

      const total = await Playlist.countDocuments(query);

      res.status(200).json({ playlists, total, page: parseInt(page), totalPages: Math.ceil(total / limit) });
    } catch (error) {
      next(error);
    }
  }

  static async getDaily(req, res, next) {
    try {
      let playlist = await Playlist.findOne({ userId: req.user._id, type: 'daily' });

      if (!playlist) {
        playlist = await PlaylistService.generateDailyPlaylist(
          req.user._id,
          req.user.interests || []
        );
      }

      res.status(200).json({ playlist });
    } catch (error) {
      next(error);
    }
  }

  static async create(req, res, next) {
    try {
      const { name, description, tracks = [], isPublic = false } = req.body;

      if (!name) {
        return res.status(400).json({ error: 'Playlist name is required' });
      }

      const trackIds = tracks.map(t => t.trackId || t);
      const validTracks = await Track.find({ _id: { $in: trackIds } });

      const playlistTracks = validTracks.map(t => ({
        trackId: String(t._id),
        title: t.title,
        artist: t.artist,
        coverUrl: t.coverUrl,
        addedAt: new Date(),
      }));

      const playlist = await Playlist.create({
        userId: req.user._id,
        name,
        description: description || '',
        type: 'user_created',
        tracks: playlistTracks,
        isPublic,
      });

      res.status(201).json({ message: 'Playlist created', playlist });
    } catch (error) {
      next(error);
    }
  }

  static async getById(req, res, next) {
    try {
      const playlist = await Playlist.findOne({
        _id: req.params.id,
        userId: req.user._id,
      });

      if (!playlist) return res.status(404).json({ error: 'Playlist not found' });

      res.status(200).json({ playlist });
    } catch (error) {
      next(error);
    }
  }

  static async update(req, res, next) {
    try {
      const { name, description, tracks, isPublic } = req.body;
      const updates = {};

      if (name !== undefined) updates.name = name;
      if (description !== undefined) updates.description = description;
      if (isPublic !== undefined) updates.isPublic = isPublic;

      if (tracks !== undefined) {
        const trackIds = tracks.map(t => t.trackId || t);
        const validTracks = await Track.find({ _id: { $in: trackIds } });
        updates.tracks = validTracks.map(t => ({
          trackId: String(t._id),
          title: t.title,
          artist: t.artist,
          coverUrl: t.coverUrl,
          addedAt: new Date(),
        }));
      }

      const playlist = await Playlist.findOneAndUpdate(
        { _id: req.params.id, userId: req.user._id },
        updates,
        { new: true, runValidators: true }
      );

      if (!playlist) return res.status(404).json({ error: 'Playlist not found' });

      res.status(200).json({ message: 'Playlist updated', playlist });
    } catch (error) {
      next(error);
    }
  }

  static async delete(req, res, next) {
    try {
      const playlist = await Playlist.findOneAndDelete({
        _id: req.params.id,
        userId: req.user._id,
      });

      if (!playlist) return res.status(404).json({ error: 'Playlist not found' });

      res.status(200).json({ message: 'Playlist deleted' });
    } catch (error) {
      next(error);
    }
  }

  static async generateQr(req, res, next) {
    try {
      const playlist = await Playlist.findOne({
        _id: req.params.id,
        userId: req.user._id,
      });

      if (!playlist) return res.status(404).json({ error: 'Playlist not found' });

      const shareUrl = `${process.env.FRONTEND_URL || 'http://localhost'}/playlist/${playlist._id}`;

      const qrDataUrl = await QRCode.toDataURL(shareUrl, {
        width: 400,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#FFFFFF',
        },
      });

      res.status(200).json({
        message: 'QR code generated',
        qrCode: qrDataUrl,
        shareUrl,
      });
    } catch (error) {
      next(error);
    }
  }

  static async regenerateDaily(req, res, next) {
    try {
      const playlist = await PlaylistService.generateDailyPlaylist(
        req.user._id,
        req.user.interests || []
      );

      res.status(200).json({ message: 'Daily playlist regenerated', playlist });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = PlaylistController;
