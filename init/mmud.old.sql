# MySQL dump 8.13
#
# Host: localhost    Database: mmud
#--------------------------------------------------------
# Server version	3.23.37

#
# Table structure for table 'action'
#

CREATE TABLE action (
  id int(5) NOT NULL default '0',
  contents blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'action'
#


#
# Table structure for table 'answers'
#

CREATE TABLE answers (
  name varchar(20) NOT NULL default '',
  question varchar(80) NOT NULL default '',
  answer blob,
  PRIMARY KEY  (name,question)
) TYPE=MyISAM;

#
# Dumping data for table 'answers'
#


#
# Table structure for table 'bantable'
#

CREATE TABLE bantable (
  address varchar(40) NOT NULL default '',
  days int(3) default NULL,
  IP varchar(40) NOT NULL default '',
  name varchar(20) NOT NULL default '',
  deputy varchar(20) NOT NULL default '',
  date datetime default NULL,
  reason varchar(255) NOT NULL default '',
  PRIMARY KEY  (address)
) TYPE=MyISAM;

#
# Dumping data for table 'bantable'
#


#
# Table structure for table 'bogus_itemtable'
#

CREATE TABLE bogus_itemtable (
  id int(7) NOT NULL default '0',
  search varchar(50) NOT NULL default '',
  belongsto varchar(20) NOT NULL default '',
  amount int(7) default NULL,
  room int(7) NOT NULL default '0',
  wearing varchar(20) NOT NULL default '',
  wielding varchar(20) NOT NULL default '',
  PRIMARY KEY  (id,search,belongsto,room,wearing,wielding)
) TYPE=MyISAM;

#
# Dumping data for table 'bogus_itemtable'
#


#
# Table structure for table 'bottable'
#

CREATE TABLE bottable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default NULL,
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race varchar(20) default NULL,
  sex varchar(20) default NULL,
  age varchar(20) default NULL,
  length varchar(20) default NULL,
  width varchar(40) default NULL,
  complexion varchar(40) default NULL,
  eyes varchar(40) default NULL,
  face varchar(40) default NULL,
  hair varchar(40) default NULL,
  beard varchar(40) default NULL,
  arm varchar(40) default NULL,
  leg varchar(40) default NULL,
  gold int(9) default NULL,
  silver int(9) default NULL,
  copper int(9) default NULL,
  room int(7) default NULL,
  lok varchar(40) default NULL,
  whimpy int(3) default NULL,
  experience int(7) default NULL,
  fightingwho varchar(20) default NULL,
  sleep int(4) default NULL,
  punishment int(7) default NULL,
  fightable int(2) default NULL,
  vitals int(7) default NULL,
  fysically int(7) default NULL,
  mentally int(7) default NULL,
  drinkstats int(4) default NULL,
  eatstats int(4) default NULL,
  active int(1) default NULL,
  lastlogin datetime default NULL,
  birth datetime default NULL,
  god int(1) default NULL,
  guild varchar(20) default NULL,
  strength int(7) default NULL,
  intelligence int(7) default NULL,
  dexterity int(7) default NULL,
  constitution int(7) default NULL,
  wisdom int(7) default NULL,
  practises int(2) default NULL,
  training int(2) default NULL,
  bandage int(2) default NULL,
  alignment int(3) default NULL,
  manastats int(7) default NULL,
  movementstats int(7) default NULL,
  maxmana int(7) default NULL,
  maxmove int(7) default NULL,
  maxvital int(7) default NULL,
  cgiServerSoftware varchar(40) default NULL,
  cgiServerName varchar(40) default NULL,
  cgiGatewayInterface varchar(40) default NULL,
  cgiServerProtocol varchar(40) default NULL,
  cgiServerPort varchar(40) default NULL,
  cgiRequestMethod varchar(40) default NULL,
  cgiPathInfo varchar(40) default NULL,
  cgiPathTranslated varchar(40) default NULL,
  cgiScriptName varchar(40) default NULL,
  cgiRemoteHost varchar(40) default NULL,
  cgiRemoteAddr varchar(40) default NULL,
  cgiAuthType varchar(40) default NULL,
  cgiRemoteUser varchar(40) default NULL,
  cgiRemoteIdent varchar(40) default NULL,
  cgiContentType varchar(40) default NULL,
  cgiAccept varchar(40) default NULL,
  cgiUserAgent varchar(40) default NULL,
  jumpmana int(4) default '1',
  jumpmove int(4) default '1',
  jumpvital int(4) default '1',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'bottable'
#


#
# Table structure for table 'bugreportlist'
#

CREATE TABLE bugreportlist (
  id int(11) NOT NULL default '0',
  description blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'bugreportlist'
#


#
# Table structure for table 'claimed'
#

CREATE TABLE claimed (
  id int(5) NOT NULL default '0',
  block varchar(15) default NULL,
  deity varchar(50) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'claimed'
#


#
# Table structure for table 'depalias'
#

CREATE TABLE depalias (
  id varchar(20) NOT NULL default '',
  strformat blob,
  sqlformat blob,
  names varchar(20) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'depalias'
#


#
# Table structure for table 'help'
#

CREATE TABLE help (
  command varchar(20) NOT NULL default '',
  contents blob,
  PRIMARY KEY  (command)
) TYPE=MyISAM;

#
# Dumping data for table 'help'
#


#
# Table structure for table 'items'
#

CREATE TABLE items (
  id int(7) NOT NULL default '0',
  name varchar(100) default NULL,
  adject1 varchar(30) default NULL,
  adject2 varchar(30) default NULL,
  adject3 varchar(30) default NULL,
  manaincrease int(4) default NULL,
  hitincrease int(4) default NULL,
  vitalincrease int(4) default NULL,
  movementincrease int(4) default NULL,
  eatable blob,
  drinkable blob,
  room int(7) default NULL,
  lightable int(7) default NULL,
  getable int(1) default NULL,
  dropable int(1) default NULL,
  visible int(1) default NULL,
  wieldable int(1) default NULL,
  description blob,
  readdescr blob,
  wearable int(2) default NULL,
  gold int(7) default NULL,
  silver int(7) default NULL,
  copper int(7) default NULL,
  weight int(3) NOT NULL default '1',
  pasdefense int(11) default '0',
  damageresistance int(11) default '0',
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'items'
#


#
# Table structure for table 'itemtable'
#

CREATE TABLE itemtable (
  id int(7) NOT NULL default '0',
  search varchar(50) NOT NULL default '',
  belongsto varchar(20) NOT NULL default '',
  amount int(7) default NULL,
  room int(7) NOT NULL default '0',
  wearing varchar(20) NOT NULL default '',
  wielding varchar(20) NOT NULL default '',
  PRIMARY KEY  (id,search,belongsto,room,wearing,wielding)
) TYPE=MyISAM;

#
# Dumping data for table 'itemtable'
#


#
# Table structure for table 'logonmessage'
#

CREATE TABLE logonmessage (
  id int(2) NOT NULL default '0',
  message blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'logonmessage'
#

INSERT INTO logonmessage VALUES (0,'<B>Logonmessage</B>\n');

#
# Table structure for table 'mailtable'
#

CREATE TABLE mailtable (
  name varchar(20) NOT NULL default '',
  toname varchar(20) default NULL,
  header varchar(100) default NULL,
  whensent datetime NOT NULL default '0000-00-00 00:00:00',
  haveread tinyint(4) default NULL,
  newmail tinyint(4) default NULL,
  message blob,
  PRIMARY KEY  (name,whensent)
) TYPE=MyISAM;

#
# Dumping data for table 'mailtable'
#


#
# Table structure for table 'monster'
#

CREATE TABLE monster (
  name char(60) NOT NULL default '',
  description char(60) default NULL,
  intelligence tinyint(2) unsigned default NULL,
  treasure char(2) default NULL,
  treasure_bonus smallint(5) unsigned default NULL,
  alignment enum('CG','CN','CE','NG','N','NE','LG','LN','LE') default 'CG',
  no_appearing tinyint(3) unsigned default NULL,
  armor_class tinyint(2) default NULL,
  movement_ground tinyint(2) unsigned default NULL,
  movement_air tinyint(2) unsigned default NULL,
  hit_dice tinyint(2) unsigned default NULL,
  hitdice_bonus tinyint(2) default NULL,
  thac0 tinyint(2) unsigned default NULL,
  no_attacks tinyint(1) unsigned default NULL,
  damage_attack tinyint(3) unsigned default NULL,
  damage_bonus tinyint(2) default NULL,
  magic_resist tinyint(3) unsigned default NULL,
  size enum('T','S','M','L','H','G') default 'T',
  morale tinyint(2) unsigned default NULL,
  xp_value smallint(5) unsigned default NULL,
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'monster'
#


#
# Table structure for table 'negativeitem'
#

CREATE TABLE negativeitem (
  id int(5) NOT NULL default '0',
  block varchar(25) default NULL,
  name varchar(50) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'negativeitem'
#


#
# Table structure for table 'objects'
#

CREATE TABLE objects (
  number int(30) NOT NULL default '0',
  name varchar(30) NOT NULL default '',
  id int(5) NOT NULL default '0',
  contents blob
) TYPE=MyISAM;

#
# Dumping data for table 'objects'
#


#
# Table structure for table 'positiveitem'
#

CREATE TABLE positiveitem (
  id int(5) NOT NULL default '0',
  block varchar(25) default NULL,
  name varchar(50) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'positiveitem'
#


#
# Table structure for table 'private_info'
#

CREATE TABLE private_info (
  id int(11) NOT NULL auto_increment,
  entrytime datetime default NULL,
  realname varchar(40) default NULL,
  email varchar(40) default NULL,
  age varchar(40) default NULL,
  sex varchar(40) default NULL,
  location varchar(20) default NULL,
  employment varchar(50) default NULL,
  internalnewsletter varchar(40) default NULL,
  external_advertising varchar(40) default NULL,
  comments blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'private_info'
#


#
# Table structure for table 'respawningitemtable'
#

CREATE TABLE respawningitemtable (
  id int(7) NOT NULL default '0',
  search varchar(50) NOT NULL default '',
  belongsto varchar(20) NOT NULL default '',
  amount int(7) default NULL,
  room int(7) NOT NULL default '0',
  wearing varchar(20) NOT NULL default '',
  wielding varchar(20) NOT NULL default '',
  PRIMARY KEY  (id,search,belongsto,room,wearing,wielding)
) TYPE=MyISAM;

#
# Dumping data for table 'respawningitemtable'
#


#
# Table structure for table 'rooms'
#

CREATE TABLE rooms (
  id int(5) NOT NULL default '0',
  west int(5) default NULL,
  east int(5) default NULL,
  north int(5) default NULL,
  south int(5) default NULL,
  up int(5) default NULL,
  down int(5) default NULL,
  contents blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'rooms'
#

INSERT INTO rooms VALUES (1,2,0,0,0,0,0,'<H1><IMG\nSRC=\"http://localhost.localdomain/~karchan/pictures/gif/cave.gif\"\nALIGN=MIDDLE>The Cave</H1><IMG\nSRC=\"http://localhost.localdomain/~karchan/pictures/gif/letters/y.gif\"\nALIGN=left>ou are in the middle of a cave.  Around you, stone walls make it\nimpenetrable.  To the west, you can see a beautiful blue sky.  The cave is\nwet; water is running down the walls.  It is dark and dreary, a complete\ncontrast with the sky in the west.<br><br>Nearby, a small lake can be seen\nwith crystal clear water.  A strange glow in the water makes it appear like\nthere is a light in there.<p>In the cave, a red leather book on an old rusty\nchain can be found.  There is also a red button.  The red button looks\nstrangely modern and very out of place next to the book and the chain.  Who\nknows what might happen if you were to push the button...<P><EMBED\nsrc=\"http://localhost.localdomain/~karchan/wav/ambcave2.wav\"\nautostart=\"true\" hidden=\"true\" loop=\"true\"></EMBED>');

#
# Table structure for table 'sillynamestable'
#

CREATE TABLE sillynamestable (
  name char(40) NOT NULL default '',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'sillynamestable'
#


#
# Table structure for table 'skills'
#

CREATE TABLE skills (
  number int(4) NOT NULL default '0',
  name varchar(20) default NULL,
  inlevel int(5) default NULL,
  outlevel int(5) default NULL,
  manacost int(5) default NULL,
  begineffect blob,
  endeffect blob,
  modifiername int(2) default NULL,
  difficulty int(2) default NULL,
  type int(2) default NULL,
  PRIMARY KEY  (number)
) TYPE=MyISAM;

#
# Dumping data for table 'skills'
#


#
# Table structure for table 'skilltable'
#

CREATE TABLE skilltable (
  number int(4) NOT NULL default '0',
  forwhom varchar(20) NOT NULL default '',
  skilllevel int(5) default NULL,
  PRIMARY KEY  (number,forwhom)
) TYPE=MyISAM;

#
# Dumping data for table 'skilltable'
#


#
# Table structure for table 'tmp_itemtable'
#

CREATE TABLE tmp_itemtable (
  id int(7) NOT NULL default '0',
  search varchar(50) NOT NULL default '',
  belongsto varchar(20) NOT NULL default '',
  amount int(7) default NULL,
  room int(7) NOT NULL default '0',
  wearing varchar(20) NOT NULL default '',
  wielding varchar(20) NOT NULL default '',
  PRIMARY KEY  (id,search,belongsto,room,wearing,wielding)
) TYPE=MyISAM;

#
# Dumping data for table 'tmp_itemtable'
#


#
# Table structure for table 'tmp_mailtable'
#

CREATE TABLE tmp_mailtable (
  name varchar(20) NOT NULL default '',
  toname varchar(20) default NULL,
  header varchar(100) default NULL,
  whensent datetime NOT NULL default '0000-00-00 00:00:00',
  haveread tinyint(4) default NULL,
  newmail tinyint(4) default NULL,
  message blob,
  PRIMARY KEY  (name,whensent)
) TYPE=MyISAM;

#
# Dumping data for table 'tmp_mailtable'
#


#
# Table structure for table 'tmp_usertable'
#

CREATE TABLE tmp_usertable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default NULL,
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race varchar(20) default NULL,
  sex varchar(20) default NULL,
  age varchar(20) default NULL,
  length varchar(20) default NULL,
  width varchar(40) default NULL,
  complexion varchar(40) default NULL,
  eyes varchar(40) default NULL,
  face varchar(40) default NULL,
  hair varchar(40) default NULL,
  beard varchar(40) default NULL,
  arm varchar(40) default NULL,
  leg varchar(40) default NULL,
  gold int(9) default NULL,
  silver int(9) default NULL,
  copper int(9) default NULL,
  room int(7) default NULL,
  lok varchar(40) default NULL,
  whimpy int(3) default NULL,
  experience int(7) default NULL,
  fightingwho varchar(20) default NULL,
  sleep int(4) default NULL,
  punishment int(7) default NULL,
  fightable int(2) default NULL,
  vitals int(7) default NULL,
  fysically int(7) default NULL,
  mentally int(7) default NULL,
  drinkstats int(4) default NULL,
  eatstats int(4) default NULL,
  active int(1) default NULL,
  lastlogin datetime default NULL,
  birth datetime default NULL,
  god int(1) default NULL,
  guild varchar(20) default NULL,
  strength int(7) default NULL,
  intelligence int(7) default NULL,
  dexterity int(7) default NULL,
  constitution int(7) default NULL,
  wisdom int(7) default NULL,
  practises int(2) default NULL,
  training int(2) default NULL,
  bandage int(2) default NULL,
  alignment int(3) default NULL,
  manastats int(7) default NULL,
  movementstats int(7) default NULL,
  maxmana int(7) default NULL,
  maxmove int(7) default NULL,
  maxvital int(7) default NULL,
  cgiServerSoftware varchar(40) default NULL,
  cgiServerName varchar(40) default NULL,
  cgiGatewayInterface varchar(40) default NULL,
  cgiServerProtocol varchar(40) default NULL,
  cgiServerPort varchar(40) default NULL,
  cgiRequestMethod varchar(40) default NULL,
  cgiPathInfo varchar(40) default NULL,
  cgiPathTranslated varchar(40) default NULL,
  cgiScriptName varchar(40) default NULL,
  cgiRemoteHost varchar(40) default NULL,
  cgiRemoteAddr varchar(40) default NULL,
  cgiAuthType varchar(40) default NULL,
  cgiRemoteUser varchar(40) default NULL,
  cgiRemoteIdent varchar(40) default NULL,
  cgiContentType varchar(40) default NULL,
  cgiAccept varchar(40) default NULL,
  cgiUserAgent varchar(40) default NULL,
  jumpmana int(4) default '1',
  jumpmove int(4) default '1',
  jumpvital int(4) default '1',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'tmp_usertable'
#


#
# Table structure for table 'todo'
#

CREATE TABLE todo (
  id int(3) NOT NULL default '0',
  descr varchar(80) default NULL,
  done varchar(10) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Dumping data for table 'todo'
#


#
# Table structure for table 'unbantable'
#

CREATE TABLE unbantable (
  name varchar(20) NOT NULL default '',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'unbantable'
#


#
# Table structure for table 'usertable'
#

CREATE TABLE usertable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default NULL,
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race varchar(20) default NULL,
  sex varchar(20) default NULL,
  age varchar(20) default NULL,
  length varchar(20) default NULL,
  width varchar(40) default NULL,
  complexion varchar(40) default NULL,
  eyes varchar(40) default NULL,
  face varchar(40) default NULL,
  hair varchar(40) default NULL,
  beard varchar(40) default NULL,
  arm varchar(40) default NULL,
  leg varchar(40) default NULL,
  gold int(9) default NULL,
  silver int(9) default NULL,
  copper int(9) default NULL,
  room int(7) default NULL,
  lok varchar(40) default NULL,
  whimpy int(3) default NULL,
  experience int(7) default NULL,
  fightingwho varchar(20) default NULL,
  sleep int(4) default NULL,
  punishment int(7) default NULL,
  fightable int(2) default NULL,
  vitals int(7) default NULL,
  fysically int(7) default NULL,
  mentally int(7) default NULL,
  drinkstats int(4) default NULL,
  eatstats int(4) default NULL,
  active int(1) default NULL,
  lastlogin datetime default NULL,
  birth datetime default NULL,
  god int(1) default NULL,
  guild varchar(20) default NULL,
  strength int(7) default NULL,
  intelligence int(7) default NULL,
  dexterity int(7) default NULL,
  constitution int(7) default NULL,
  wisdom int(7) default NULL,
  practises int(2) default NULL,
  training int(2) default NULL,
  bandage int(2) default NULL,
  alignment int(3) default NULL,
  manastats int(7) default NULL,
  movementstats int(7) default NULL,
  maxmana int(7) default NULL,
  maxmove int(7) default NULL,
  maxvital int(7) default NULL,
  cgiServerSoftware varchar(40) default NULL,
  cgiServerName varchar(40) default NULL,
  cgiGatewayInterface varchar(40) default NULL,
  cgiServerProtocol varchar(40) default NULL,
  cgiServerPort varchar(40) default NULL,
  cgiRequestMethod varchar(40) default NULL,
  cgiPathInfo varchar(40) default NULL,
  cgiPathTranslated varchar(40) default NULL,
  cgiScriptName varchar(40) default NULL,
  cgiRemoteHost varchar(40) default NULL,
  cgiRemoteAddr varchar(40) default NULL,
  cgiAuthType varchar(40) default NULL,
  cgiRemoteUser varchar(40) default NULL,
  cgiRemoteIdent varchar(40) default NULL,
  cgiContentType varchar(40) default NULL,
  cgiAccept varchar(40) default NULL,
  cgiUserAgent varchar(40) default NULL,
  jumpmana int(4) default '1',
  jumpmove int(4) default '1',
  jumpvital int(4) default '1',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Dumping data for table 'usertable'
#


