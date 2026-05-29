const express = require('express');
const router = express.Router();
const UserController = require('../controllers/userController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/me', authMiddleware, UserController.getProfile);
router.put('/me', authMiddleware, UserController.updateProfile);
router.put('/username', authMiddleware, UserController.updateUsername);
router.post('/avatar', authMiddleware, UserController.updateAvatar);
router.put('/password', authMiddleware, UserController.changePassword);
router.post('/interests', authMiddleware, UserController.saveInterests);

module.exports = router;
