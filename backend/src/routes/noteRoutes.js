const express = require('express');
const router = express.Router();
const NoteController = require('../controllers/noteController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', NoteController.getAll);
router.get('/artists', NoteController.getArtists);
router.get('/:id', authMiddleware, NoteController.getById);

module.exports = router;
