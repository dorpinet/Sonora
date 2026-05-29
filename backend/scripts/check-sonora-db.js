require('dotenv').config();
const mongoose = require('mongoose');

(async () => {
  // Connect without specifying DB to access admin
  await mongoose.connect(process.env.MONGO_URI.replace('/sonora', ''));
  
  const sonoraDb = mongoose.connection.useDb('Sonora');
  const collections = await sonoraDb.db.listCollections().toArray();
  console.log('Collections in Sonora DB:', collections.map(c => c.name));
  
  if (collections.find(c => c.name === 'tracks')) {
    const tracks = await sonoraDb.db.collection('tracks').find().toArray();
    console.log('Tracks count:', tracks.length);
    tracks.slice(0, 10).forEach(t => console.log(' -', t.title, '| cover:', t.coverUrl ? 'YES' : 'NO'));
  }
  
  process.exit(0);
})();
