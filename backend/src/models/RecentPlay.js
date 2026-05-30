const mongoose = require('mongoose');

const recentPlaySchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true,
  },
  trackId: {
    type: String,
    required: true,
  },
  title: String,
  artist: String,
  coverUrl: String,
  playedAt: {
    type: Date,
    default: Date.now,
    index: true,
  },
}, {
  timestamps: false,
});

module.exports = mongoose.model('RecentPlay', recentPlaySchema);
