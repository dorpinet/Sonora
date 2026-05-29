const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Otp = require('../models/Otp');
const EmailService = require('../services/emailService');
const { validateEmail, validatePassword, validateUsername, validatePasswordsMatch } = require('../utils/validators');

const generateTokens = (userId) => {
  const accessToken = jwt.sign({ id: userId }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN || '15m',
  });
  const refreshToken = jwt.sign({ id: userId }, process.env.JWT_REFRESH_SECRET, {
    expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || '7d',
  });
  return { accessToken, refreshToken };
};

const clearTokens = async (userId) => {
  await User.findByIdAndUpdate(userId, { sessionToken: null, refreshToken: null });
};

class AuthController {
  static async registerInit(req, res, next) {
    try {
      const email = validateEmail(req.body.email);

      const existing = await User.findOne({ email });
      if (existing && existing.isVerified) {
        return res.status(409).json({ error: 'Email already registered' });
      }

      await Otp.deleteMany({ email, purpose: 'register' });

      const code = EmailService.generateOtp();
      const expiresAt = new Date(Date.now() + 10 * 60 * 1000);

      await Otp.create({ email, code, purpose: 'register', expiresAt });
      await EmailService.sendOtp(email, code, 'register');

      res.status(200).json({ message: 'Verification code sent', email });
    } catch (error) {
      next(error);
    }
  }

  static async registerVerify(req, res, next) {
    try {
      const email = validateEmail(req.body.email);
      const { code } = req.body;
      const password = validatePassword(req.body.password);
      validatePasswordsMatch(password, req.body.confirmPassword);
      const username = validateUsername(req.body.username);
      const interests = req.body.interests || [];

      if (!code || typeof code !== 'string') {
        return res.status(400).json({ error: 'Verification code is required' });
      }

      const otpDoc = await Otp.findOne({ email, code, purpose: 'register', isUsed: false });
      if (!otpDoc || otpDoc.expiresAt < new Date()) {
        return res.status(400).json({ error: 'Invalid or expired verification code' });
      }

      otpDoc.isUsed = true;
      await otpDoc.save();

      const passwordHash = await bcrypt.hash(password, 12);

      const user = await User.create({
        email,
        username,
        passwordHash,
        interests,
        isVerified: true,
      });

      const tokens = generateTokens(user._id);
      user.sessionToken = tokens.accessToken;
      user.refreshToken = tokens.refreshToken;
      await user.save();

      try {
        await EmailService.sendWelcomeEmail(email, username);
      } catch (e) {
        console.error('Welcome email failed:', e.message);
      }

      res.status(201).json({
        message: 'Registration successful',
        user: {
          id: user._id,
          email: user.email,
          username: user.username,
          theme: user.theme,
          interests: user.interests,
        },
        tokens,
      });
    } catch (error) {
      next(error);
    }
  }

  static async login(req, res, next) {
    try {
      const email = validateEmail(req.body.email);
      const { password } = req.body;

      if (!password) {
        return res.status(400).json({ error: 'Password is required' });
      }

      const user = await User.findOne({ email }).select('+passwordHash');
      if (!user) {
        return res.status(401).json({ error: 'Invalid credentials' });
      }

      if (!user.isVerified) {
        return res.status(403).json({ error: 'Email not verified' });
      }

      const isMatch = await bcrypt.compare(password, user.passwordHash);
      if (!isMatch) {
        return res.status(401).json({ error: 'Invalid credentials' });
      }

      user.lastLogin = new Date();
      const tokens = generateTokens(user._id);
      user.sessionToken = tokens.accessToken;
      user.refreshToken = tokens.refreshToken;
      await user.save();

      res.status(200).json({
        message: 'Login successful',
        user: {
          id: user._id,
          email: user.email,
          username: user.username,
          theme: user.theme,
          interests: user.interests,
          avatarUrl: user.avatarUrl,
        },
        tokens,
      });
    } catch (error) {
      next(error);
    }
  }

  static async refresh(req, res, next) {
    try {
      const { refreshToken } = req.body;

      if (!refreshToken) {
        return res.status(401).json({ error: 'Refresh token required' });
      }

      const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET);
      const user = await User.findById(decoded.id).select('+refreshToken');

      if (!user || user.refreshToken !== refreshToken) {
        return res.status(401).json({ error: 'Invalid refresh token' });
      }

      const tokens = generateTokens(user._id);
      user.sessionToken = tokens.accessToken;
      user.refreshToken = tokens.refreshToken;
      await user.save();

      res.status(200).json({ tokens });
    } catch (error) {
      if (error.name === 'TokenExpiredError' || error.name === 'JsonWebTokenError') {
        return res.status(401).json({ error: 'Invalid or expired refresh token' });
      }
      next(error);
    }
  }

  static async logout(req, res, next) {
    try {
      const userId = req.user?._id;
      if (userId) {
        await clearTokens(userId);
      }
      res.status(200).json({ message: 'Logged out successfully' });
    } catch (error) {
      next(error);
    }
  }

  static async resendOtp(req, res, next) {
    try {
      const email = validateEmail(req.body.email);
      const purpose = req.body.purpose || 'register';

      await Otp.deleteMany({ email, purpose });

      const code = EmailService.generateOtp();
      const expiresAt = new Date(Date.now() + 10 * 60 * 1000);

      await Otp.create({ email, code, purpose, expiresAt });
      await EmailService.sendOtp(email, code, purpose);

      res.status(200).json({ message: 'New verification code sent', email });
    } catch (error) {
      next(error);
    }
  }

  static async forgotPasswordInit(req, res, next) {
    try {
      const email = validateEmail(req.body.email);

      const user = await User.findOne({ email });
      if (!user || !user.isVerified) {
        return res.status(404).json({ error: 'User not found' });
      }

      await Otp.deleteMany({ email, purpose: 'reset_password' });

      const code = EmailService.generateOtp();
      const expiresAt = new Date(Date.now() + 10 * 60 * 1000);

      await Otp.create({ email, code, purpose: 'reset_password', expiresAt });
      await EmailService.sendOtp(email, code, 'reset_password');

      res.status(200).json({ message: 'Reset code sent to email', email });
    } catch (error) {
      next(error);
    }
  }

  static async forgotPasswordVerify(req, res, next) {
    try {
      const email = validateEmail(req.body.email);
      const { code } = req.body;
      const password = validatePassword(req.body.password);
      validatePasswordsMatch(password, req.body.confirmPassword);

      if (!code) {
        return res.status(400).json({ error: 'Verification code is required' });
      }

      const otpDoc = await Otp.findOne({ email, code, purpose: 'reset_password', isUsed: false });
      if (!otpDoc || otpDoc.expiresAt < new Date()) {
        return res.status(400).json({ error: 'Invalid or expired verification code' });
      }

      otpDoc.isUsed = true;
      await otpDoc.save();

      const passwordHash = await bcrypt.hash(password, 12);
      await User.findOneAndUpdate({ email }, { passwordHash });

      res.status(200).json({ message: 'Password reset successful' });
    } catch (error) {
      next(error);
    }
  }

  static async changeEmailInit(req, res, next) {
    try {
      const userId = req.user._id;
      const newEmail = validateEmail(req.body.newEmail);
      const { password } = req.body;

      if (!password) {
        return res.status(400).json({ error: 'Current password is required' });
      }

      const user = await User.findById(userId).select('+passwordHash');
      if (!user) {
        return res.status(404).json({ error: 'User not found' });
      }

      const isMatch = await bcrypt.compare(password, user.passwordHash);
      if (!isMatch) {
        return res.status(401).json({ error: 'Invalid password' });
      }

      const existing = await User.findOne({ email: newEmail });
      if (existing && existing._id.toString() !== userId.toString()) {
        return res.status(409).json({ error: 'Email already in use' });
      }

      await Otp.deleteMany({ email: newEmail, purpose: 'change_email' });

      const code = EmailService.generateOtp();
      const expiresAt = new Date(Date.now() + 10 * 60 * 1000);

      await Otp.create({ email: newEmail, code, purpose: 'change_email', expiresAt });
      await EmailService.sendOtp(newEmail, code, 'change_email');

      res.status(200).json({ message: 'Verification code sent to new email', email: newEmail });
    } catch (error) {
      next(error);
    }
  }

  static async changeEmailVerify(req, res, next) {
    try {
      const userId = req.user._id;
      const newEmail = validateEmail(req.body.newEmail);
      const { code } = req.body;

      if (!code) {
        return res.status(400).json({ error: 'Verification code is required' });
      }

      const otpDoc = await Otp.findOne({ email: newEmail, code, purpose: 'change_email', isUsed: false });
      if (!otpDoc || otpDoc.expiresAt < new Date()) {
        return res.status(400).json({ error: 'Invalid or expired verification code' });
      }

      otpDoc.isUsed = true;
      await otpDoc.save();

      await User.findByIdAndUpdate(userId, { email: newEmail });

      res.status(200).json({ message: 'Email updated successfully', email: newEmail });
    } catch (error) {
      next(error);
    }
  }

  static async me(req, res, next) {
    try {
      const user = await User.findById(req.user._id).select('-passwordHash -refreshToken');
      res.status(200).json({ user });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = AuthController;
