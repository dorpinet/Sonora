const crypto = require('crypto');

console.log('=== SONORA Environment Variables ===\n');

console.log('JWT_SECRET=' + crypto.randomBytes(64).toString('hex'));
console.log('JWT_REFRESH_SECRET=' + crypto.randomBytes(64).toString('hex'));
console.log('\n--- Copy these to your .env file ---');
