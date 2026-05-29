const express = require('express');
const router = express.Router();
const SpotifyController = require('../controllers/spotifyController');

router.get('/search', SpotifyController.search);
router.get('/preview/:id', SpotifyController.getPreview);

module.exports = router;
