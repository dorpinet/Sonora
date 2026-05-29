const mongoose = require('mongoose');

const VALID_INTERESTS = ['sport', 'meditation', 'party', 'focus'];

const userSchema = new mongoose.Schema({
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    trim: true,
  },
  username: {
    type: String,
    trim: true,
    default: null,
  },
  passwordHash: {
    type: String,
    required: [true, 'Password is required'],
    select: false,
  },
  sessionToken: {
    type: String,
    default: null,
    index: true,
  },
  refreshToken: {
    type: String,
    default: null,
    select: false,
  },
  interests: {
    type: [String],
    default: [],
    validate: {
      validator: function(v) {
        return v.every(i => VALID_INTERESTS.includes(i.toLowerCase()));
      },
      message: props => `${props.value} contains invalid interest. Valid: ${VALID_INTERESTS.join(', ')}`,
    },
  },
  theme: {
    type: String,
    enum: ['dark', 'light'],
    default: 'dark',
  },
  avatarUrl: {
    type: String,
    default: null,
  },
  isVerified: {
    type: Boolean,
    default: false,
  },
  lastLogin: {
    type: Date,
    default: null,
  },
}, {
  timestamps: true,
});

module.exports = mongoose.model('User', userSchema);
