const express = require('express');
const router = express.Router();
const TrackController = require('../controllers/trackController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', TrackController.getAll);
router.get('/genres', TrackController.getGenres);
router.get('/spotify/search', TrackController.searchSpotify);
router.get('/spotify/:id/preview', TrackController.getSpotifyPreview);

router.get('/:id', authMiddleware, TrackController.getById);

module.exports = router;
