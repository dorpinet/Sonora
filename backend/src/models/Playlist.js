const mongoose = require('mongoose');

const playlistSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true,
  },
  name: {
    type: String,
    required: true,
    trim: true,
  },
  description: {
    type: String,
    default: '',
    trim: true,
  },
  type: {
    type: String,
    enum: ['daily', 'user_created', 'favorites'],
    default: 'user_created',
  },
  genre: {
    type: String,
    default: null,
  },
  tracks: [{
    trackId: {
      type: String,
      default: null,
    },
    title: String,
    artist: String,
    coverUrl: String,
    addedAt: {
      type: Date,
      default: Date.now,
    },
  }],
  coverUrl: {
    type: String,
    default: null,
  },
  qrCodeUrl: {
    type: String,
    default: null,
  },
  isPublic: {
    type: Boolean,
    default: false,
  },
  generatedDate: {
    type: Date,
    default: null,
  },
}, {
  timestamps: true,
});

module.exports = mongoose.model('Playlist', playlistSchema);
