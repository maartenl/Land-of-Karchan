#!/bin/sh

#
# Please adapt the password value and the name of the database in which you
# plan to install the tables.

/usr/bin/mysql --user=mmud --password=itsasecret <<END_OF_DATA
# MySQL dump 8.13# Host: localhost    Database: mud
#--------------------------------------------------------
# Server version	3.23.37

select "" as "Creating database...";
create database mmud;
\u mmud

#
# Table structure for table 'action'
select "" as "Creating 'action' table...";
CREATE TABLE action (
  id int(5) NOT NULL default '0',
  contents blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Table structure for table 'answers'
select "" as "Creating 'answers' table...";
CREATE TABLE answers (
  name varchar(20) NOT NULL default '',
  question varchar(80) NOT NULL default '',
  answer blob,
  PRIMARY KEY  (name,question)
) TYPE=MyISAM;

#
# Table structure for table 'attributes'
select "" as "Creating 'attributes' table...";
CREATE TABLE attributes (
  name varchar(32) NOT NULL default '',
  value text,
  value_type varchar(20) NOT NULL default 'string',
  objectid varchar(200) NOT NULL default '0',
  objecttype int(3) NOT NULL default '0',
  PRIMARY KEY  (name,objectid,objecttype)
) TYPE=MyISAM;

#
# Table structure for table 'bantable'
select "" as "Creating 'bantable' table...";
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
# Table structure for table 'bogus_itemtable'
select "" as "Creating 'bogus_itemtable' table...";
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
# Table structure for table 'bottable'
select "" as "Creating 'bottable' table...";
CREATE TABLE bottable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default NULL,
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race enum('fox','zombie','wyvern','wolf','turtle','troll','spider','slug','ropegnaw','rabbit','orc','ooze','human','elf','dwarf','duck','deity','chipmunk','buggie') default NULL,
  sex enum('male','female') default NULL,
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
# Table structure for table 'characterinfo'
select "" as "Creating 'characterinfo' table...";
CREATE TABLE characterinfo (
  name varchar(20) NOT NULL default '',
  imageurl varchar(255) default NULL,
  homepageurl varchar(255) default NULL,
  dateofbirth varchar(255) default NULL,
  cityofbirth varchar(255) default NULL,
  storyline blob,
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Table structure for table 'commands'
select "" as "Creating 'commands' table...";
CREATE TABLE commands (
  id int(11) NOT NULL default '0',
  name varchar(32) NOT NULL default '',
  callable int(11) default NULL,
  command text,
  method_name varchar(32) default NULL,
  args text,
  room int(11) NOT NULL default '0',
  PRIMARY KEY  (id,name)
) TYPE=MyISAM;

#
# Table structure for table 'depalias'
select "" as "Creating 'depalias' table...";
CREATE TABLE depalias (
  id varchar(20) NOT NULL default '',
  strformat blob,
  sqlformat blob,
  names varchar(20) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Table structure for table 'events'
select "" as "Creating 'events' table...";
CREATE TABLE events (
  eventid int(11) NOT NULL default '0',
  name varchar(32) NOT NULL default '',
  month int(11) NOT NULL default '-1',
  dayofmonth int(11) NOT NULL default '-1',
  hour int(11) NOT NULL default '-1',
  minute int(11) NOT NULL default '-1',
  dayofweek int(11) NOT NULL default '-1',
  callable int(2) NOT NULL default '1',
  method_name varchar(32) default NULL,
  args text,
  room int(11) NOT NULL default '0',
  PRIMARY KEY  (eventid)
) TYPE=MyISAM;

#
# Table structure for table 'family'
select "" as "Creating 'family' table...";
CREATE TABLE family (
  name varchar(20) NOT NULL default '',
  toname varchar(20) NOT NULL default '',
  description int(11) default NULL,
  PRIMARY KEY  (name,toname)
) TYPE=MyISAM;

#
# Table structure for table 'familyvalues'
select "" as "Creating 'familyvalues' table...";
CREATE TABLE familyvalues (
  id int(11) NOT NULL default '0',
  description varchar(40) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Table structure for table 'guilds'
select "" as "Creating 'guilds' table...";
CREATE TABLE guilds (
  name varchar(255) NOT NULL default '',
  title varchar(255) default NULL,
  description varchar(255) default NULL,
  location varchar(255) default NULL,
  homepage varchar(255) default NULL,
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Table structure for table 'help'
select "" as "Creating 'help' table...";
CREATE TABLE help (
  command varchar(20) NOT NULL default '',
  contents blob,
  PRIMARY KEY  (command)
) TYPE=MyISAM;

#
# Table structure for table 'items'
select "" as "Creating 'items' table...";
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
  pasdefense int(2) default NULL,
  damageresistance int(2) default NULL,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Table structure for table 'itemtable'
select "" as "Creating 'itemtable' table...";
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
# Table structure for table 'logonmessage'
select "" as "Creating 'logonmessage' table...";
CREATE TABLE logonmessage (
  id int(2) NOT NULL default '0',
  message blob,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

#
# Table structure for table 'mailtable'
select "" as "Creating 'mailtable' table...";
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
# Table structure for table 'methods'
select "" as "Creating 'methods' table...";
CREATE TABLE methods (
  id int(11) NOT NULL default '1',
  name varchar(32) NOT NULL default '',
  src text NOT NULL,
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Table structure for table 'olditem'
select "" as "Creating 'olditem' table...";
CREATE TABLE olditem (
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
# Table structure for table 'oldmail'
select "" as "Creating 'oldmail' table...";
CREATE TABLE oldmail (
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
# Table structure for table 'olduser'
select "" as "Creating 'olduser' table...";
CREATE TABLE olduser (
  name varchar(20) NOT NULL default '',
  address varchar(40) default NULL,
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race enum('fox','zombie','wyvern','wolf','turtle','troll','spider','slug','ropegnaw','rabbit','orc','ooze','human','elf','dwarf','duck','deity','chipmunk','buggie') default NULL,
  sex enum('male','female') default NULL,
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
# Table structure for table 'private_info'
select "" as "Creating 'private_info' table...";
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
# Table structure for table 'respawningitemtable'
select "" as "Creating 'respawningitemtable' table...";
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
# Table structure for table 'rooms'
select "" as "Creating 'rooms' table...";
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

INSERT INTO rooms VALUES (1,2,0,0,0,0,0,'<H1><IMG
SRC=\"http://localhost.localdomain/~karchan/pictures/gif/cave.gif\"
ALIGN=MIDDLE>The Cave</H1><IMG
SRC=\"http://localhost.localdomain/~karchan/pictures/gif/letters/y.gif\"   
ALIGN=left>ou are in the middle of a cave.  Around you, stone walls make it
impenetrable.  To the west, you can see a beautiful blue sky.  The cave is 
wet; water is running down the walls.  It is dark and dreary, a complete   
contrast with the sky in the west.<br><br>Nearby, a small lake can be seen  
with crystal clear water.  A strange glow in the water makes it appear like 
there is a light in there.<p>In the cave, a red leather book on an old rusty
chain can be found.  There is also a red button.  The red button looks
strangely modern and very out of place next to the book and the chain.  Who
knows what might happen if you were to push the button...<P><EMBED
src=\"http://localhost.localdomain/~karchan/wav/ambcave2.wav\"
autostart=\"true\" hidden=\"true\" loop=\"true\"></EMBED>');
 
#
# Table structure for table 'sillynamestable'
select "" as "Creating 'sillynamestable' table...";
CREATE TABLE sillynamestable (
  name char(40) NOT NULL default '',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Table structure for table 'skills'
select "" as "Creating 'skills' table...";
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
# Table structure for table 'skilltable'
select "" as "Creating 'skilltable' table...";
CREATE TABLE skilltable (
  number int(4) NOT NULL default '0',
  forwhom varchar(20) NOT NULL default '',
  skilllevel int(5) default NULL,
  PRIMARY KEY  (number,forwhom)
) TYPE=MyISAM;

#
# Table structure for table 'tmp_itemtable'
select "" as "Creating 'tmp_itemtable' table...";
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
# Table structure for table 'tmp_mailtable'
select "" as "Creating 'tmp_mailtable' table...";
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
# Table structure for table 'tmp_usertable'
select "" as "Creating 'tmp_usertable' table...";
CREATE TABLE tmp_usertable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default '',
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race enum('fox','zombie','wyvern','wolf','turtle','troll','spider','slug','ropegnaw','rabbit','orc','ooze','human','elf','dwarf','duck','deity','chipmunk','buggie') default NULL,
  sex enum('male','female') default NULL,
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
  gold int(9) default '0',
  silver int(9) default '0',
  copper int(9) default '2',
  room int(7) default '1',
  lok varchar(40) default NULL,
  whimpy int(3) default '0',
  experience int(7) default '0',
  fightingwho varchar(20) default '',
  sleep int(4) default '0',
  punishment int(7) default '0',
  fightable int(2) default '0',
  vitals int(7) default '0',
  fysically int(7) default '0',
  mentally int(7) default '0',
  drinkstats int(4) default '0',
  eatstats int(4) default '0',
  active int(1) default '0',
  lastlogin datetime default NULL,
  birth datetime default NULL,
  god int(1) default '0',
  guild varchar(20) default '',
  strength int(7) default '2',
  intelligence int(7) default '2',
  dexterity int(7) default '2',
  constitution int(7) default '2',
  wisdom int(7) default '2',
  practises int(2) default '0',
  training int(2) default '0',
  bandage int(2) default '0',
  alignment int(3) default '0',
  manastats int(7) default '0',
  movementstats int(7) default '0',
  maxmana int(7) default '118',
  maxmove int(7) default '500',
  maxvital int(7) default '118',
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
# Table structure for table 'unbantable'
select "" as "Creating 'unbantable' table...";
CREATE TABLE unbantable (
  name varchar(20) NOT NULL default '',
  PRIMARY KEY  (name)
) TYPE=MyISAM;

#
# Table structure for table 'usertable'
select "" as "Creating 'usertable' table...";
CREATE TABLE usertable (
  name varchar(20) NOT NULL default '',
  address varchar(40) default '',
  password varchar(40) default NULL,
  title varchar(254) default NULL,
  realname varchar(80) default NULL,
  email varchar(40) default NULL,
  race enum('fox','zombie','wyvern','wolf','turtle','troll','spider','slug','ropegnaw','rabbit','orc','ooze','human','elf','dwarf','duck','deity','chipmunk','buggie') default NULL,
  sex enum('male','female') default NULL,
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
  gold int(9) default '0',
  silver int(9) default '0',
  copper int(9) default '2',
  room int(7) default '1',
  lok varchar(40) default NULL,
  whimpy int(3) default '0',
  experience int(7) default '0',
  fightingwho varchar(20) default '',
  sleep int(4) default '0',
  punishment int(7) default '0',
  fightable int(2) default '0',
  vitals int(7) default '0',
  fysically int(7) default '0',
  mentally int(7) default '0',
  drinkstats int(4) default '0',
  eatstats int(4) default '0',
  active int(1) default '0',
  lastlogin datetime default NULL,
  birth datetime default NULL,
  god int(1) default '0',
  guild varchar(20) default '',
  strength int(7) default '2',
  intelligence int(7) default '2',
  dexterity int(7) default '2',
  constitution int(7) default '2',
  wisdom int(7) default '2',
  practises int(2) default '0',
  training int(2) default '0',
  bandage int(2) default '0',
  alignment int(3) default '0',
  manastats int(7) default '0',
  movementstats int(7) default '0',
  maxmana int(7) default '118',
  maxmove int(7) default '500',
  maxvital int(7) default '118',
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
# Table structure for table 'voice'
select "" as "Creating 'voice' table...";
CREATE TABLE voice (
  id int(11) NOT NULL default '0',
  voice varchar(255) default NULL,
  sleep int(11) default NULL,
  room int(11) NOT NULL default '0',
  PRIMARY KEY  (id,room)
) TYPE=MyISAM;

select "" as "All done... Bye bye...";
END_OF_DATA
