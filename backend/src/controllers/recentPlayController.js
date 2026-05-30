const RecentPlay = require('../models/RecentPlay');

class RecentPlayController {
  static async add(req, res, next) {
    try {
      const { trackId, title, artist, coverUrl } = req.body;
      if (!trackId) {
        return res.status(400).json({ error: 'trackId is required' });
      }

      await RecentPlay.create({
        userId: req.user._id,
        trackId,
        title: title || '',
        artist: artist || '',
        coverUrl: coverUrl || '',
        playedAt: new Date(),
      });

      // Keep only last 50
      const count = await RecentPlay.countDocuments({ userId: req.user._id });
      if (count > 50) {
        const toDelete = await RecentPlay.find({ userId: req.user._id })
          .sort({ playedAt: 1 })
          .limit(count - 50);
        const ids = toDelete.map(r => r._id);
        await RecentPlay.deleteMany({ _id: { $in: ids } });
      }

      res.status(201).json({ message: 'Recent play recorded' });
    } catch (error) {
      next(error);
    }
  }

  static async getAll(req, res, next) {
    try {
      const plays = await RecentPlay.find({ userId: req.user._id })
        .sort({ playedAt: -1 })
        .limit(20);

      res.status(200).json({ recentPlays: plays });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = RecentPlayController;
