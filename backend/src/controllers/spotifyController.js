const SpotifyService = require('../services/spotifyService');

class SpotifyController {

  static async search(req, res, next) {
    try {
      const { q, limit = 20 } = req.query;

      if (!q) {
        return res.status(400).json({ error: 'Query parameter "q" is required.' });
      }

      const results = await SpotifyService.searchTracks(q, { limit: parseInt(limit) });


      const tracks = (results.tracks?.items || []).map(t => ({
        spotifyId: t.id,
        title: t.name,
        artist: t.artists?.map(a => a.name).join(', '),
        album: t.album?.name,
        coverUrl: t.album?.images?.[0]?.url || null,
        previewUrl: t.preview_url,
        durationMs: t.duration_ms,
        explicit: t.explicit,
      }));

      res.status(200).json({
        tracks,
        total: results.tracks?.total || 0,
      });
    } catch (error) {
      next(error);
    }
  }


  static async getPreview(req, res, next) {
    try {
      const { id } = req.params;
      const data = await SpotifyService.getTrackPreview(id);

      if (!data.previewUrl) {
        return res.status(404).json({ error: 'Preview not available for this track.' });
      }

      res.status(200).json(data);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = SpotifyController;
