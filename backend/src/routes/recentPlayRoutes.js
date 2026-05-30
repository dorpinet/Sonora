const express = require('express');
const router = express.Router();
const RecentPlayController = require('../controllers/recentPlayController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/', authMiddleware, RecentPlayController.add);
router.get('/', authMiddleware, RecentPlayController.getAll);

module.exports = router;
