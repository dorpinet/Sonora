const Note = require('../models/Note');
const Favorite = require('../models/Favorite');

class NoteController {

  static async getAll(req, res, next) {
    try {
      const { artist, search, page = 1, limit = 50 } = req.query;
      const query = {};

      if (artist) query.artist = new RegExp(artist, 'i');
      if (search) {
        query.$or = [
          { title: new RegExp(search, 'i') },
          { artist: new RegExp(search, 'i') },
        ];
      }

      const skip = (parseInt(page) - 1) * parseInt(limit);
      const notes = await Note.find(query)
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(parseInt(limit));

      const total = await Note.countDocuments(query);

      res.status(200).json({ notes, total, page: parseInt(page), totalPages: Math.ceil(total / limit) });
    } catch (error) {
      next(error);
    }
  }


  static async getById(req, res, next) {
    try {
      const note = await Note.findById(req.params.id);
      if (!note) return res.status(404).json({ error: 'Note not found.' });

      let isFavorite = false;
      if (req.user) {
        const fav = await Favorite.findOne({ userId: req.user._id, itemId: note._id, itemType: 'note' });
        isFavorite = !!fav;
      }

      res.status(200).json({ note, isFavorite });
    } catch (error) {
      next(error);
    }
  }


  static async getArtists(req, res, next) {
    try {
      const artists = await Note.distinct('artist');
      res.status(200).json({ artists });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = NoteController;
