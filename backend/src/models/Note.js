const mongoose = require('mongoose');

const noteSchema = new mongoose.Schema({
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
  imageUrl: {
    type: String,
    required: true,
  },
  coverUrl: {
    type: String,
    default: null,
  },
  duration: {
    type: Number,
    default: 30,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
}, {
  collection: 'notes',
  timestamps: false,
  versionKey: false,
});

module.exports = mongoose.models.Note || mongoose.model('Note', noteSchema);
