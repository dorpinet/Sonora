const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/authController');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/register-init', AuthController.registerInit);
router.post('/register-verify', AuthController.registerVerify);
router.post('/login', AuthController.login);
router.post('/refresh', AuthController.refresh);
router.post('/resend-otp', AuthController.resendOtp);
router.post('/forgot-password', AuthController.forgotPasswordInit);
router.post('/reset-password', AuthController.forgotPasswordVerify);
router.post('/change-email-init', authMiddleware, AuthController.changeEmailInit);
router.post('/change-email-verify', authMiddleware, AuthController.changeEmailVerify);
router.get('/me', authMiddleware, AuthController.me);
router.post('/logout', authMiddleware, AuthController.logout);

module.exports = router;
