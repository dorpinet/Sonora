const express = require('express');
const router = express.Router();
const FavoriteController = require('../controllers/favoriteController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/', authMiddleware, FavoriteController.getAll);
router.post('/', authMiddleware, FavoriteController.add);
router.get('/check/:itemId', authMiddleware, FavoriteController.check);
router.delete('/:itemId', authMiddleware, FavoriteController.remove);

module.exports = router;
