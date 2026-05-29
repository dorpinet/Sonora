const mongoose = require('mongoose');

const trackSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true,
  },
  artist: {
    type: String,
    required: true,
    trim: true,
  },
  genre: {
    type: String,
    required: true,
  },
  durationSeconds: {
    type: Number,
    default: 30,
  },
  coverUrl: {
    type: String,
    default: null,
  },
  spotifyId: {
    type: String,
    default: null,
  },
  audioUrl: {
    type: String,
    default: null,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
}, {
  collection: 'tracks',
  timestamps: false,
  versionKey: false,
});

module.exports = mongoose.models.Track || mongoose.model('Track', trackSchema);
