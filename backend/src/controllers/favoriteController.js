const Favorite = require('../models/Favorite');
const Track = require('../models/Track');
const Note = require('../models/Note');

class FavoriteController {

  static async getAll(req, res, next) {
    try {
      const { type, page = 1, limit = 50 } = req.query;
      const query = { userId: req.user._id };
      if (type) query.itemType = type;

      const skip = (parseInt(page) - 1) * parseInt(limit);
      const favorites = await Favorite.find(query)
        .sort({ addedAt: -1 })
        .skip(skip)
        .limit(parseInt(limit));


      const populated = [];
      for (const fav of favorites) {
        let item = null;
        if (fav.itemType === 'track') {
          item = await Track.findById(fav.itemId);
        } else if (fav.itemType === 'note') {
          item = await Note.findById(fav.itemId);
        }
        if (item) {
          populated.push({ ...fav.toObject(), item });
        }
      }

      const total = await Favorite.countDocuments(query);

      res.status(200).json({ favorites: populated, total, page: parseInt(page), totalPages: Math.ceil(total / limit) });
    } catch (error) {
      next(error);
    }
  }


  static async add(req, res, next) {
    try {
      const { itemId, itemType } = req.body;

      if (!itemId || !itemType) {
        return res.status(400).json({ error: 'itemId and itemType are required.' });
      }

      if (!['track', 'note'].includes(itemType)) {
        return res.status(400).json({ error: 'itemType must be track or note.' });
      }


      let item = null;
      if (itemType === 'track') item = await Track.findById(itemId);
      else item = await Note.findById(itemId);

      if (!item) {
        return res.status(404).json({ error: `${itemType} not found.` });
      }

      const favorite = await Favorite.create({
        userId: req.user._id,
        itemId,
        itemType,
      });

      res.status(201).json({ message: 'Added to favorites.', favorite });
    } catch (error) {
      if (error.code === 11000) {
        return res.status(409).json({ error: 'Already in favorites.' });
      }
      next(error);
    }
  }


  static async remove(req, res, next) {
    try {
      const { itemType } = req.query;
      const query = { userId: req.user._id, itemId: req.params.itemId };
      if (itemType) query.itemType = itemType;

      const result = await Favorite.findOneAndDelete(query);
      if (!result) return res.status(404).json({ error: 'Favorite not found.' });

      res.status(200).json({ message: 'Removed from favorites.' });
    } catch (error) {
      next(error);
    }
  }


  static async check(req, res, next) {
    try {
      const { itemType } = req.query;
      const favorite = await Favorite.findOne({
        userId: req.user._id,
        itemId: req.params.itemId,
        itemType,
      });

      res.status(200).json({ isFavorite: !!favorite });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = FavoriteController;
