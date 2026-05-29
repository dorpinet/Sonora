require('dotenv').config();
const mongoose = require('mongoose');

(async () => {
  await mongoose.connect(process.env.MONGO_URI);
  
  console.log('Connected DB:', mongoose.connection.db.databaseName);
  
  const collections = await mongoose.connection.db.listCollections().toArray();
  console.log('Collections:', collections.map(c => c.name));
  
  if (collections.find(c => c.name === 'tracks')) {
    const tracks = await mongoose.connection.db.collection('tracks').find().toArray();
    console.log('Tracks in collection:', tracks.length);
    if (tracks.length > 0) {
      tracks.slice(0, 5).forEach(t => console.log(' -', t.title, '| cover:', t.coverUrl ? 'YES' : 'NO'));
    }
  }
  
  process.exit(0);
})();
