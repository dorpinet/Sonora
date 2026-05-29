require('dotenv').config();
const mongoose = require('mongoose');

const Track = require('../src/models/Track');

(async () => {
  await mongoose.connect(process.env.MONGO_URI);
  console.log('Connected to:', mongoose.connection.db.databaseName);

  const tracks = await Track.find({ $or: [{ coverUrl: null }, { coverUrl: '' }] });
  console.log(`\n${tracks.length} tracks need covers:\n`);
  
  tracks.forEach((t, i) => {
    console.log(`${i + 1}. "${t.title}" by ${t.artist} (${t.genre})`);
  });

  console.log('\n--- COPY ABOVE TO GET URLs FROM R2 ---');
  process.exit(0);
})();
