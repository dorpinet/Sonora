const User = require('../models/User');
const Favorite = require('../models/Favorite');
const bcrypt = require('bcryptjs');
const { validateUsername, validatePassword, validatePasswordsMatch } = require('../utils/validators');

class UserController {
  static async getProfile(req, res, next) {
    try {
      const user = await User.findById(req.user._id).select('-passwordHash -refreshToken');
      if (!user) return res.status(404).json({ error: 'User not found' });

      const favoritesCount = await Favorite.countDocuments({ userId: user._id });

      res.status(200).json({
        user: {
          id: user._id,
          email: user.email,
          username: user.username,
          theme: user.theme,
          interests: user.interests,
          avatarUrl: user.avatarUrl,
          lastLogin: user.lastLogin,
          createdAt: user.createdAt,
        },
        stats: { favoritesCount },
      });
    } catch (error) {
      next(error);
    }
  }

  static async updateProfile(req, res, next) {
    try {
      const { theme } = req.body;
      const updates = {};

      if (theme !== undefined) {
        if (!['dark', 'light'].includes(theme)) {
          return res.status(400).json({ error: 'Theme must be dark or light' });
        }
        updates.theme = theme;
      }

      const user = await User.findByIdAndUpdate(
        req.user._id,
        updates,
        { new: true, runValidators: true }
      ).select('-passwordHash -refreshToken');

      res.status(200).json({ message: 'Profile updated', user });
    } catch (error) {
      next(error);
    }
  }

  static async updateUsername(req, res, next) {
    try {
      const { password } = req.body;
      const username = validateUsername(req.body.username);

      if (!password) {
        return res.status(400).json({ error: 'Password is required' });
      }

      const user = await User.findById(req.user._id).select('+passwordHash');
      const isMatch = await bcrypt.compare(password, user.passwordHash);
      if (!isMatch) {
        return res.status(401).json({ error: 'Invalid password' });
      }

      user.username = username;
      await user.save();

      res.status(200).json({ message: 'Username updated', username });
    } catch (error) {
      next(error);
    }
  }

  static async updateAvatar(req, res, next) {
    try {
      const { avatarUrl } = req.body;
      if (!avatarUrl) {
        return res.status(400).json({ error: 'avatarUrl is required' });
      }

      const user = await User.findByIdAndUpdate(
        req.user._id,
        { avatarUrl },
        { new: true }
      ).select('-passwordHash -refreshToken');

      res.status(200).json({ message: 'Avatar updated', avatarUrl: user.avatarUrl });
    } catch (error) {
      next(error);
    }
  }

  static async changePassword(req, res, next) {
    try {
      const { currentPassword, newPassword, confirmPassword } = req.body;

      if (!currentPassword) {
        return res.status(400).json({ error: 'Current password is required' });
      }

      validatePassword(newPassword);
      validatePasswordsMatch(newPassword, confirmPassword);

      const user = await User.findById(req.user._id).select('+passwordHash');
      const isMatch = await bcrypt.compare(currentPassword, user.passwordHash);
      if (!isMatch) {
        return res.status(401).json({ error: 'Current password is incorrect' });
      }

      user.passwordHash = await bcrypt.hash(newPassword, 12);
      await user.save();

      res.status(200).json({ message: 'Password changed successfully' });
    } catch (error) {
      next(error);
    }
  }

  static async saveInterests(req, res, next) {
    try {
      const { interests } = req.body;

      if (!Array.isArray(interests) || interests.length === 0) {
        return res.status(400).json({ error: 'Interests array required' });
      }

      const validInterests = ['sport', 'meditation', 'party', 'focus'];
      const filtered = interests.filter(i => validInterests.includes(i.toLowerCase()));

      if (filtered.length === 0) {
        return res.status(400).json({
          error: `Invalid interests. Valid options: ${validInterests.join(', ')}`,
        });
      }

      const user = await User.findByIdAndUpdate(
        req.user._id,
        { interests: filtered },
        { new: true }
      ).select('-passwordHash -refreshToken');

      res.status(200).json({ message: 'Interests saved', interests: user.interests });
    } catch (error) {
      next(error);
    }
  }
}

module.exports = UserController;
