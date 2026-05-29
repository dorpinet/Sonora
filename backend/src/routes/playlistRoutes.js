const express = require('express');
const router = express.Router();
const PlaylistController = require('../controllers/playlistController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', authMiddleware, PlaylistController.getAll);
router.get('/daily', authMiddleware, PlaylistController.getDaily);
router.post('/regenerate-daily', authMiddleware, PlaylistController.regenerateDaily);
router.post('/', authMiddleware, PlaylistController.create);
router.get('/:id', authMiddleware, PlaylistController.getById);
router.put('/:id', authMiddleware, PlaylistController.update);
router.delete('/:id', authMiddleware, PlaylistController.delete);
router.post('/:id/generate-qr', authMiddleware, PlaylistController.generateQr);

module.exports = router;
