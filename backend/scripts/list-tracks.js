require('dotenv').config();
const mongoose = require('mongoose');

(async () => {
  await mongoose.connect(process.env.MONGO_URI);
  const Track = require('../src/models/Track');
  const tracks = await Track.find();
  console.log('Total tracks:', tracks.length);
  tracks.forEach(t => console.log(t.title, '- cover:', t.coverUrl ? 'YES' : 'NO'));
  process.exit(0);
})();
