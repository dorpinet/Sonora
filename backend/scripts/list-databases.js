require('dotenv').config();
const mongoose = require('mongoose');

(async () => {
  await mongoose.connect(process.env.MONGO_URI);
  
  const admin = mongoose.connection.db.admin();
  const dbs = await admin.listDatabases();
  console.log('All databases in cluster:');
  dbs.databases.forEach(db => {
    console.log(' -', db.name, '(size:', db.sizeOnDisk, ')');
  });
  
  process.exit(0);
})();
