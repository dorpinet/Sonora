require('dotenv').config();
const mongoose = require('mongoose');

const Track = require('../src/models/Track');

const BASE = 'https://pub-b7eac54927804333ac001f55505f5006.r2.dev';

const COVERS = {
  // kissesfromeldar
  'ніч зоряна': BASE + '/kissesfromeldar%20-%20nich_zoryana.jpg',
  '#однаєдина': BASE + '/kissesfromeldar%20-%20odna_yedyna.jpg',
  '#devourme': BASE + '/kissesfromeldar%20-%20_devourme.jpg',
  'walkinlikeazomb': BASE + '/kissesfromeldar%20-%20walkinlikeazomb.jpg',
  'номенклатура': BASE + '/kissesfromeldar%20-%20nomenklatura.jpg',
  '#зміїнийострів': BASE + '/kissesfromeldar%20-%20zmiinyi_ostriv.jpg',
  '#сповідь': BASE + '/kissesfromeldar%20-%20spovid.jpg',

  // irlbabee
  'сука': BASE + '/irlbabee%20-%20suka.jpg',
  'коли на мені погляд': BASE + '/irlbabee%20-%20koly_na_meni_pohlyad.jpg',
  'вдячна': BASE + '/irlbabee%20-%20vdyachna.jpg',
  'ікона': BASE + '/irlbabee%20-%20ikona.jpg',
  'сумую знову': BASE + '/irlbabee%20-%20sumuyu_znovu.jpg',

  // 2hollis
  'poster boy': BASE + '/2hollis%20-%20poster%20boy.jpg',
  'all 2s': BASE + '/2hollis%20-%20all%202s.jpg',
  'GOD': BASE + '/2hollis%20-%20GOD.jpg',
  'actor': BASE + '/2hollis%20-%20actor.jpg',

  // Kanii
  'sins (let me in)': BASE + '/Kanii%20-%20sins%20_let%20me%20in_.jpg',
  'marry me': BASE + '/Kanii%20-%20marry%20me.jpg',
  'Heart Racing': BASE + '/Kanii%20-%20Heart%20Racing.jpg',
  'Go (Xtayalive 2)': BASE + '/Kanii%20-%20Go%20_Xtayalive%202_.jpg',

  // Lucy Bedroque
  '2010 Justin Bieber': BASE + '/Lucy%20Bedroque%20-%202010%20Justin%20Bieber.jpg',
  'Fenty Face': BASE + '/Lucy%20Bedroque%20-%20Fenty%20Face.jpg',

  // prettifun
  'Touch The Sun': BASE + '/prettifun%20-%20Touch%20The%20Sun.jpg',
  'Ice Cream': BASE + '/prettifun%20-%20Ice%20Cream.jpg',

  // nate sib
  'tonight': BASE + '/nate%20sib%20-%20tonight.jpg',

  // osquinn
  'let go': BASE + '/osquinn%20-%20let%20go.jpg',

  // usedcvnt
  'they called me Violet': BASE + '/usedcvnt%20-%20they%20called%20me%20Violet.jpg',
  'just leave me to bleed': BASE + '/usedcvnt%20-%20just%20leave%20me%20to%20bleed.jpg',

  // Rory in early 20s
  'De Kieru': BASE + '/Rory%20in%20early%2020s%20-%20De%20Kieru.jpg',
  'Kimaterishu': BASE + '/Rory%20in%20early%2020s%20-%20Kimaterishu.jpg',
  'digital winter v3': BASE + '/Rory%20in%20early%2020s%20-%20digital%20winter%20v3.jpg',
  'yaderi': BASE + '/Rory%20in%20early%2020s%20-%20yaderi.jpg',

  // hkmori
  'lost': BASE + '/hkmori%20-%20lost.jpg',
  'wake up, kill myself...': BASE + '/hkmori%20-%20wake%20up_%20kill%20myself___.jpg',

  // jorj1357
  'help_urself_real_breakcore_v5': BASE + '/jorj1357%20-%20help_urself_real_breakcore_v5.jpg',

  // proloxx
  'windows breakcore': BASE + '/proloxx%20-%20windows%20breakcore.jpg',

  // Nuvfr
  'LUNAROS': BASE + '/Nuvfr%20-%20LUNAROS.jpg',

  // my head hurts
  'ending to a worthless life': BASE + '/my%20head%20hurts%20-%20ending%20to%20a%20worthless%20life.jpg',

  // Xxtarlit_
  '636% #next': BASE + '/Xxtarlit_%20-%20636_%20_next.jpg',

  // Lamp (City Pop)
  '儚き春の一幕': BASE + '/Lamp%20-%20Hakanaki%20Haru%20no%20Hitomaku.jpg',
  '二十歳の恋': BASE + '/Lamp%20-%20Hatachi%20no%20Koi.jpg',
  '密やかに': BASE + '/Lamp%20-%20Hisoyaka%20ni.jpg',
  'For Lovers': BASE + '/Lamp%20-%20For%20Lovers.jpg',
  'Last Train At 25 O\'clock': BASE + '/Lamp%20-%20Last%20Train%20At%2025%20O_clock.jpg',
  'Rainy Tapestry': BASE + '/Lamp%20-%20Rainy%20Tapestry.jpg',
  '夕暮れ': BASE + '/Lamp%20-Yugure.jpg',
  'Behind The Moon Shadow': BASE + '/Lamp%20-%20Behind%20The%20Moon%20Shadow.jpg',
  'ゆめうつつ': BASE + '/Lamp%20-%20Yumeutsutsu.jpg',

  // Ichiko Aoba
  'Dawn in the Adan': BASE + '/Ichiko%20Aoba%20-%20Dawn%20in%20the%20Adan.jpg',

  // Panchiko
  'Laputa': BASE + '/Panchiko%20-%20Laputa.jpg',

  // Strawberry Guy
  'Without You': BASE + '/Strawberry%20Guy%20-%20Without%20You.jpg',

  // Asagaya Romantics
  '独り言': BASE + '/Asagaya%20Romantics%20-%20Hitorigoto.jpg',

  // mamerico
  'my room': BASE + '/mamerico%20-%20my%20room.jpg',

  // Bladee / Drain
  'Apple': BASE + '/Bladee%20-%20Apple.jpg',
  'BBY': BASE + '/Bladee%20-%20BBY.jpg',
  'Destroy Me': BASE + '/Bladee%20-%20Destroy%20Me.jpg',
  'Trash Star': BASE + '/Bladee%20-%20Trash%20Star.jpg',
  'egobaby': BASE + '/Bladee%20-%20egobaby.jpg',
  '1D': BASE + '/Bladee%20-%201D.jpg',
  'Sad MEAL': BASE + '/Bladee%20-%20Sad%20MEAL.jpg',
  'Frozen 2': BASE + '/Bladee%20-%20Frozen%202.jpg',

  // Ecco2k
  'GT-R': BASE + '/Ecco2k%20-%20GT-R.jpg',

  // Collaborations
  'Expression On Your Face': BASE + '/Mechatok_%20Ecco2k_%20Bladee%20-%20Expression%20On%20Your%20Face.jpg',
  'For You': BASE + '/Bladee_%20Ecco2k%20-%20For%20You.jpg',
  'Acid Rain': BASE + '/Bladee_%20Ecco2k%20-%20Acid%20Rain.jpg',
  'Faust': BASE + '/Bladee_%20Ecco2k%20-%20Faust.jpg',
  'Bentley': BASE + '/Bladee_%20Thaiboy%20Digital%20-%20Bentley.jpg',
  'I DONT LIKE PEOPLE': BASE + '/Bladee_%20Yung%20Lean%20-%20I%20DONT%20LIKE%20PEOPLE.jpg',

  // Mechatok
  'You Don\'t Exist': BASE + '/Mechatok%20-%20You%20Don_t%20Exist.jpg',

  // Crystal Castles
  'Alice Practice': BASE + '/Crystal%20Castles%20-%20Alice%20Practice.jpg',
  'Baptism': BASE + '/Crystal%20Castles%20-%20Baptism.jpg',
  'Celestica': BASE + '/Crystal%20Castles%20-%20Celestica.jpg',
  'Crimewave': BASE + '/Crystal%20Castles%20-%20Crimewave.jpg',
  'Kerosene': BASE + '/Crystal%20Castles%20-%20Kerosene.jpg',
  'Plague': BASE + '/Crystal%20Castles%20-%20Plague.jpg',
  'Transgender': BASE + '/Crystal%20Castles%20-%20Transgender.jpg',
  'Untrust Us': BASE + '/Crystal%20Castles%20-%20Untrust%20Us.jpg',
  'Violent Dreams': BASE + '/Crystal%20Castles%20-%20Violent%20Dreams.jpg',
  'Suffocation': BASE + '/Crystal%20Castles%20-%20Suffocation.jpg',

  // Sidewalks and Skeletons
  'Cicatrice': BASE + '/Sidewalks%20and%20Skeletons%20-%20Cicatrice.jpg',
  'Euthanasia Daze': BASE + '/Sidewalks%20and%20Skeletons%20-%20Euthanasia%20Daze.jpg',
  'Pure': BASE + '/Sidewalks%20and%20Skeletons%20-%20Pure.jpg',

  // Mareux
  'Underground': BASE + '/Mareux%20-%20Underground.jpg',

  // Pastel Ghost
  'Clouds': BASE + '/Pastel%20Ghost%20-%20Clouds.jpg',
  'Emotion': BASE + '/Pastel%20Ghost%20-%20Emotion.jpg',
  'Ethereality': BASE + '/Pastel%20Ghost%20-%20Ethereality.jpg',
  'Possession': BASE + '/Pastel%20Ghost%20-%20Possession.jpg',

  // Ken Carson
  'Rock N Roll': BASE + '/Ken%20Carson%20-%20Rock%20N%20Roll.jpg',
  'Shoot': BASE + '/Ken%20Carson%20-%20Shoot.jpg',

  // Playboi Carti
  'Ain\'t Doin That': BASE + '/Playboi%20Carti%20-%20Ain_t%20Doin%20That.jpg',

  // Destroy Lonely
  'FAKENGGAS': BASE + '/Destroy%20Lonely%20-%20FAKENGGAS.jpg',

  // Homixide Gang
  '55 Lifestyle': BASE + '/Homixide%20Gang%20-%2055%20Lifestyle.jpg',
  '5unna': BASE + '/Homixide%20Gang%20-%205unna.jpg',

  // OsamaSon
  'Pop': BASE + '/OsamaSon%20-%20Pop.jpg',

  // Nettspend
  'We not like you': BASE + '/Nettspend%20-%20We%20not%20like%20you.jpg',
  'nothing like uuu': BASE + '/Nettspend%20-%20nothing%20like%20uuu.jpg',

  // ian
  'Bentayga': BASE + '/ian%20-%20Bentayga.jpg',
  'Magic Johnson': BASE + '/ian%20-%20Magic%20Johnson.jpg',
  'Never Stop': BASE + '/ian%20-%20Never%20Stop.jpg',

  // Rich Amiri
  'CODEINE CRAZY': BASE + '/Rich%20Amiri%20-%20CODEINE%20CRAZY.jpg',
  'MADONNA & RIHANNA': BASE + '/Rich%20Amiri%20-%20MADONNA%20_%20RIHANNA.jpg',
};

const FALLBACK = 'https://pub-e7e50e9504174e26a5ee24e580152921.r2.dev/default-cover.jpg';

async function update() {
  await mongoose.connect(process.env.MONGO_URI);
  console.log('Connected:', mongoose.connection.db.databaseName);

  let updated = 0;
  let notFound = [];

  for (const [title, url] of Object.entries(COVERS)) {
    const res = await Track.updateOne(
      { title },
      { $set: { coverUrl: url } }
    );
    if (res.modifiedCount > 0) {
      console.log('✓', title);
      updated++;
    } else {
      notFound.push(title);
    }
  }

  // Fallback
  const fb = await Track.updateMany(
    { $or: [{ coverUrl: null }, { coverUrl: '' }] },
    { $set: { coverUrl: FALLBACK } }
  );
  if (fb.modifiedCount > 0) {
    console.log('✓ Fallback for', fb.modifiedCount, 'tracks');
    updated += fb.modifiedCount;
  }

  console.log('\nUpdated:', updated, '/ 101');
  if (notFound.length > 0) {
    console.log('\nNot found in DB:', notFound.length);
    notFound.forEach(t => console.log(' -', t));
  }

  process.exit(0);
}

update().catch(err => { console.error(err); process.exit(1); });
