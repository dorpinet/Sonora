require('dotenv').config();
const mongoose = require('mongoose');

const Track = require('../src/models/Track');

// ======== ЗАПОВНИ ЦЕ ========
// Формат: "Назва трека": "URL_з_R2"
const COVERS = {
  // Приклад:
  // "ніч зоряна": "https://pub-XXX.r2.dev/nich.jpg",
  // "poster boy": "https://pub-XXX.r2.dev/poster.jpg",

  // Додавай сюди всі 101 трек:
};

const FALLBACK = "https://pub-e7e50e9504174e26a5ee24e580152921.r2.dev/default-cover.jpg";

async function update() {
  await mongoose.connect(process.env.MONGO_URI);
  console.log('Connected:', mongoose.connection.db.databaseName);

  let updated = 0;

  for (const [title, url] of Object.entries(COVERS)) {
    const res = await Track.updateOne(
      { title },
      { $set: { coverUrl: url } }
    );
    if (res.modifiedCount > 0) {
      console.log('✓', title, '->', url);
      updated++;
    } else {
      console.log('✗ Track not found:', title);
    }
  }

  // Фолбек для тих що залишились
  const fallbackRes = await Track.updateMany(
    { $or: [{ coverUrl: null }, { coverUrl: '' }] },
    { $set: { coverUrl: FALLBACK } }
  );
  if (fallbackRes.modifiedCount > 0) {
    console.log('✓ Fallback for', fallbackRes.modifiedCount, 'tracks');
    updated += fallbackRes.modifiedCount;
  }

  console.log('\nTotal updated:', updated, '/ 101');
  process.exit(0);
}

update().catch(err => { console.error(err); process.exit(1); });
