#!/bin/sh

. ./mysql_constants

echo "Setting up database..."

# ${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB}  <<END_OF_DATA

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s mmudtest <<END_OF_DATA
# it is not necessary to disable foreign key checks,
# as this is only the skeleton structure of the mud
# none of the tables have actual contents yet.
# SET FOREIGN_KEY_CHECKS=0;


-- MySQL dump 10.9
--
-- Host: localhost    Database: mud
-- ------------------------------------------------------
-- Server version	4.1.14

# the following items are commented out,
# uncomment them if they are needed, but as this database
# setup does not contain any actual data, no 
# foreignkeys/indexes/etc should trigger.
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bugs`
--

DROP TABLE IF EXISTS `bugs`;
CREATE TABLE `bugs` (
  `title` varchar(255) NOT NULL default '',
  `description` text,
  `answer` text,
  `closed` int(1) default '0',
  `name` varchar(20) NOT NULL default '',
  `creation` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`creation`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `characterinfo`
--

DROP TABLE IF EXISTS `characterinfo`;
CREATE TABLE `characterinfo` (
  `name` varchar(20) NOT NULL default '',
  `imageurl` varchar(255) default NULL,
  `homepageurl` varchar(255) default NULL,
  `dateofbirth` varchar(255) default NULL,
  `cityofbirth` varchar(255) default NULL,
  `storyline` text,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `family`
--

DROP TABLE IF EXISTS `family`;
CREATE TABLE `family` (
  `name` varchar(20) NOT NULL default '',
  `toname` varchar(20) NOT NULL default '',
  `description` int(11) default NULL,
  PRIMARY KEY  (`name`,`toname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `familyvalues`
--

DROP TABLE IF EXISTS `familyvalues`;
CREATE TABLE `familyvalues` (
  `id` int(11) NOT NULL default '0',
  `description` varchar(40) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `links`
--

DROP TABLE IF EXISTS `links`;
CREATE TABLE `links` (
  `linkname` varchar(255) NOT NULL default '',
  `url` varchar(255) NOT NULL default '',
  `type` int(1) NOT NULL default '0',
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `name` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`linkname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_admin`
--

DROP TABLE IF EXISTS `mm_admin`;
CREATE TABLE `mm_admin` (
  `name` varchar(39) NOT NULL default '',
  `passwd` varchar(38) NOT NULL default '',
  `ip` varchar(38) NOT NULL default '',
  `created` datetime NOT NULL default '0000-00-00 00:00:00',
  `validuntil` date NOT NULL default '0000-00-00',
  `email` varchar(40) NOT NULL default '',
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_answers`
--

DROP TABLE IF EXISTS `mm_answers`;
CREATE TABLE `mm_answers` (
  `name` varchar(20) NOT NULL default '',
  `question` varchar(80) NOT NULL default '',
  `answer` text NOT NULL,
  PRIMARY KEY  (`name`,`question`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_area`
--

DROP TABLE IF EXISTS `mm_area`;
CREATE TABLE `mm_area` (
  `area` varchar(49) NOT NULL default '',
  `description` text NOT NULL,
  `shortdesc` varchar(255) NOT NULL default '',
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`area`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_area_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_bannednamestable`
--

DROP TABLE IF EXISTS `mm_bannednamestable`;
CREATE TABLE `mm_bannednamestable` (
  `name` varchar(20) NOT NULL default '',
  `deputy` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `days` int(3) default NULL,
  `reason` varchar(255) default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_bantable`
--

DROP TABLE IF EXISTS `mm_bantable`;
CREATE TABLE `mm_bantable` (
  `address` varchar(40) NOT NULL default '',
  `days` int(3) default NULL,
  `IP` varchar(40) NOT NULL default '',
  `name` varchar(20) NOT NULL default '',
  `deputy` varchar(20) NOT NULL default '',
  `date` datetime default NULL,
  `reason` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_boardmessages`
--

DROP TABLE IF EXISTS `mm_boardmessages`;
CREATE TABLE `mm_boardmessages` (
  `boardid` varchar(20) NOT NULL default '',
  `name` varchar(20) NOT NULL default '',
  `posttime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `message` text NOT NULL,
  PRIMARY KEY  (`boardid`,`name`,`posttime`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_boards`
--

DROP TABLE IF EXISTS `mm_boards`;
CREATE TABLE `mm_boards` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(80) NOT NULL default '',
  `description` text NOT NULL,
  `owner` varchar(20) NOT NULL default '',
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_boards_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_charattributes`
--

DROP TABLE IF EXISTS `mm_charattributes`;
CREATE TABLE `mm_charattributes` (
  `name` varchar(32) NOT NULL default '',
  `value` text,
  `value_type` varchar(20) NOT NULL default '',
  `charname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`name`,`charname`),
  KEY `charname` (`charname`),
  CONSTRAINT `mm_charattributes_ibfk_1` FOREIGN KEY (`charname`) REFERENCES `mm_usertable` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_charitemtable`
--

DROP TABLE IF EXISTS `mm_charitemtable`;
CREATE TABLE `mm_charitemtable` (
  `id` int(11) NOT NULL default '0',
  `belongsto` varchar(20) NOT NULL default '',
  `wearing` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `belongsto` (`belongsto`),
  CONSTRAINT `mm_charitemtable_ibfk_1` FOREIGN KEY (`id`) REFERENCES `mm_itemtable` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_charitemtable_ibfk_2` FOREIGN KEY (`belongsto`) REFERENCES `mm_usertable` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_commandlog`
--

DROP TABLE IF EXISTS `mm_commandlog`;
CREATE TABLE `mm_commandlog` (
  `stamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `name` varchar(20) NOT NULL default '',
  `command` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`stamp`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_commands`
--

DROP TABLE IF EXISTS `mm_commands`;
CREATE TABLE `mm_commands` (
  `id` int(11) NOT NULL default '0',
  `callable` int(1) default '0',
  `command` varchar(255) NOT NULL default '',
  `method_name` varchar(52) NOT NULL default '',
  `room` int(11) default NULL,
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`,`command`),
  KEY `method_name` (`method_name`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_commands_ibfk_1` FOREIGN KEY (`method_name`) REFERENCES `mm_methods` (`name`) ON UPDATE CASCADE,
  CONSTRAINT `mm_commands_ibfk_2` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_errormessages`
--

DROP TABLE IF EXISTS `mm_errormessages`;
CREATE TABLE `mm_errormessages` (
  `msg` varchar(80) NOT NULL default '',
  `description` text NOT NULL,
  PRIMARY KEY  (`msg`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_events`
--

DROP TABLE IF EXISTS `mm_events`;
CREATE TABLE `mm_events` (
  `eventid` int(11) NOT NULL default '0',
  `name` varchar(32) default NULL,
  `month` int(11) NOT NULL default '-1',
  `dayofmonth` int(11) NOT NULL default '-1',
  `hour` int(11) NOT NULL default '-1',
  `minute` int(11) NOT NULL default '-1',
  `dayofweek` int(11) NOT NULL default '-1',
  `callable` int(2) NOT NULL default '0',
  `method_name` varchar(52) NOT NULL default '',
  `room` int(11) default NULL,
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`eventid`),
  KEY `method_name` (`method_name`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_events_ibfk_1` FOREIGN KEY (`method_name`) REFERENCES `mm_methods` (`name`) ON UPDATE CASCADE,
  CONSTRAINT `mm_events_ibfk_2` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_help`
--

DROP TABLE IF EXISTS `mm_help`;
CREATE TABLE `mm_help` (
  `command` varchar(20) NOT NULL default '',
  `contents` text NOT NULL,
  PRIMARY KEY  (`command`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_itemattributes`
--

DROP TABLE IF EXISTS `mm_itemattributes`;
CREATE TABLE `mm_itemattributes` (
  `name` varchar(32) NOT NULL default '',
  `value` text,
  `value_type` varchar(20) NOT NULL default '',
  `id` int(11) NOT NULL default '0',
  PRIMARY KEY  (`name`,`id`),
  KEY `id` (`id`),
  CONSTRAINT `mm_itemattributes_ibfk_1` FOREIGN KEY (`id`) REFERENCES `mm_itemtable` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_itemitemtable`
--

DROP TABLE IF EXISTS `mm_itemitemtable`;
CREATE TABLE `mm_itemitemtable` (
  `id` int(11) NOT NULL default '0',
  `containerid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `containerid` (`containerid`),
  CONSTRAINT `mm_itemitemtable_ibfk_1` FOREIGN KEY (`id`) REFERENCES `mm_itemtable` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_itemitemtable_ibfk_2` FOREIGN KEY (`containerid`) REFERENCES `mm_itemtable` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_items`
--

DROP TABLE IF EXISTS `mm_items`;
CREATE TABLE `mm_items` (
  `id` int(7) NOT NULL default '0',
  `name` varchar(100) default NULL,
  `adject1` varchar(30) default NULL,
  `adject2` varchar(30) default NULL,
  `adject3` varchar(30) default NULL,
  `manaincrease` int(4) default NULL,
  `hitincrease` int(4) default NULL,
  `vitalincrease` int(4) default NULL,
  `movementincrease` int(4) default NULL,
  `eatable` text,
  `drinkable` text,
  `room` int(7) default NULL,
  `lightable` int(7) default NULL,
  `getable` int(1) default NULL,
  `dropable` int(1) default NULL,
  `visible` int(1) default NULL,
  `wieldable` int(1) default NULL,
  `description` text NOT NULL,
  `readdescr` text,
  `wearable` int(2) default NULL,
  `gold` int(7) default NULL,
  `silver` int(7) default NULL,
  `copper` int(7) default NULL,
  `weight` int(3) NOT NULL default '1',
  `pasdefense` int(2) default NULL,
  `damageresistance` int(2) default NULL,
  `container` int(11) NOT NULL default '0',
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `capacity` int(11) default NULL,
  `isopenable` int(1) NOT NULL default '0',
  `keyid` int(11) default NULL,
  `containtype` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_items_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_itemtable`
--

DROP TABLE IF EXISTS `mm_itemtable`;
CREATE TABLE `mm_itemtable` (
  `id` int(11) NOT NULL auto_increment,
  `itemid` int(11) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `owner` varchar(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `itemid` (`itemid`),
  CONSTRAINT `mm_itemtable_ibfk_1` FOREIGN KEY (`itemid`) REFERENCES `mm_items` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_log`
--

DROP TABLE IF EXISTS `mm_log`;
CREATE TABLE `mm_log` (
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `name` varchar(20) NOT NULL default '',
  `message` varchar(255) NOT NULL default '',
  `addendum` text,
  PRIMARY KEY  (`creation`,`name`,`message`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_logonmessage`
--

DROP TABLE IF EXISTS `mm_logonmessage`;
CREATE TABLE `mm_logonmessage` (
  `id` int(2) NOT NULL default '0',
  `message` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_mailtable`
--

DROP TABLE IF EXISTS `mm_mailtable`;
CREATE TABLE `mm_mailtable` (
  `name` varchar(20) NOT NULL default '',
  `toname` varchar(20) NOT NULL default '',
  `header` varchar(100) default NULL,
  `whensent` datetime NOT NULL default '0000-00-00 00:00:00',
  `haveread` tinyint(4) default NULL,
  `newmail` tinyint(4) default NULL,
  `message` text NOT NULL,
  PRIMARY KEY  (`name`,`whensent`),
  KEY `toname` (`toname`),
  CONSTRAINT `mm_mailtable_ibfk_1` FOREIGN KEY (`name`) REFERENCES `mm_usertable` (`name`) ON UPDATE CASCADE,
  CONSTRAINT `mm_mailtable_ibfk_2` FOREIGN KEY (`toname`) REFERENCES `mm_usertable` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_methods`
--

DROP TABLE IF EXISTS `mm_methods`;
CREATE TABLE `mm_methods` (
  `name` varchar(52) NOT NULL default '',
  `src` text NOT NULL,
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`name`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_methods_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_roomattributes`
--

DROP TABLE IF EXISTS `mm_roomattributes`;
CREATE TABLE `mm_roomattributes` (
  `name` varchar(32) NOT NULL default '',
  `value` text,
  `value_type` varchar(20) NOT NULL default '',
  `id` int(11) NOT NULL default '0',
  PRIMARY KEY  (`name`,`id`),
  KEY `id` (`id`),
  CONSTRAINT `mm_roomattributes_ibfk_1` FOREIGN KEY (`id`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_roomitemtable`
--

DROP TABLE IF EXISTS `mm_roomitemtable`;
CREATE TABLE `mm_roomitemtable` (
  `id` int(11) NOT NULL default '0',
  `room` int(11) NOT NULL default '0',
  `search` varchar(50) default NULL,
  PRIMARY KEY  (`id`),
  KEY `room` (`room`),
  CONSTRAINT `mm_roomitemtable_ibfk_1` FOREIGN KEY (`id`) REFERENCES `mm_itemtable` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_roomitemtable_ibfk_2` FOREIGN KEY (`room`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_rooms`
--

DROP TABLE IF EXISTS `mm_rooms`;
CREATE TABLE `mm_rooms` (
  `id` int(5) NOT NULL default '0',
  `west` int(5) default NULL,
  `east` int(5) default NULL,
  `north` int(5) default NULL,
  `south` int(5) default NULL,
  `up` int(5) default NULL,
  `down` int(5) default NULL,
  `contents` text NOT NULL,
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `area` varchar(49) NOT NULL default 'Main',
  PRIMARY KEY  (`id`),
  KEY `area` (`area`),
  KEY `north` (`north`),
  KEY `south` (`south`),
  KEY `east` (`east`),
  KEY `west` (`west`),
  KEY `up` (`up`),
  KEY `down` (`down`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_rooms_ibfk_1` FOREIGN KEY (`area`) REFERENCES `mm_area` (`area`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_2` FOREIGN KEY (`north`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_3` FOREIGN KEY (`south`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_4` FOREIGN KEY (`east`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_5` FOREIGN KEY (`west`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_6` FOREIGN KEY (`up`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_7` FOREIGN KEY (`down`) REFERENCES `mm_rooms` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `mm_rooms_ibfk_8` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_shopkeeperitems`
--

DROP TABLE IF EXISTS `mm_shopkeeperitems`;
CREATE TABLE `mm_shopkeeperitems` (
  `id` int(11) NOT NULL default '0',
  `max` int(11) default NULL,
  `charname` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`id`,`charname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_sillynamestable`
--

DROP TABLE IF EXISTS `mm_sillynamestable`;
CREATE TABLE `mm_sillynamestable` (
  `name` char(40) NOT NULL default '',
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_unbantable`
--

DROP TABLE IF EXISTS `mm_unbantable`;
CREATE TABLE `mm_unbantable` (
  `name` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `mm_usertable`
--

DROP TABLE IF EXISTS `mm_usertable`;
CREATE TABLE `mm_usertable` (
  `name` varchar(20) NOT NULL default '',
  `address` varchar(40) default '',
  `password` varchar(40) default NULL,
  `title` varchar(254) default NULL,
  `realname` varchar(80) default NULL,
  `email` varchar(40) default NULL,
  `race` enum('fox','zombie','wyvern','wolf','turtle','troll','spider','slug','ropegnaw','rabbit','orc','ooze','human','elf','dwarf','duck','deity','chipmunk','buggie','dragon') NOT NULL default 'human',
  `sex` enum('male','female') NOT NULL default 'male',
  `age` varchar(20) default NULL,
  `length` varchar(20) default NULL,
  `width` varchar(40) default NULL,
  `complexion` varchar(40) default NULL,
  `eyes` varchar(40) default NULL,
  `face` varchar(40) default NULL,
  `hair` varchar(40) default NULL,
  `beard` varchar(40) default NULL,
  `arm` varchar(40) default NULL,
  `leg` varchar(40) default NULL,
  `gold` int(9) default '0',
  `silver` int(9) default '0',
  `copper` int(9) default '2',
  `room` int(7) default '1',
  `lok` varchar(40) default NULL,
  `whimpy` int(3) default '0',
  `experience` int(7) default '0',
  `fightingwho` varchar(20) default '',
  `sleep` int(4) default '0',
  `punishment` int(7) default '0',
  `fightable` int(2) default '0',
  `vitals` int(7) default '0',
  `fysically` int(7) default '0',
  `mentally` int(7) default '0',
  `drinkstats` int(4) default '0',
  `eatstats` int(4) default '0',
  `active` int(1) default '0',
  `lastlogin` datetime default NULL,
  `birth` datetime default NULL,
  `god` int(1) default '0',
  `guild` varchar(20) default '',
  `strength` int(7) default '2',
  `intelligence` int(7) default '2',
  `dexterity` int(7) default '2',
  `constitution` int(7) default '2',
  `wisdom` int(7) default '2',
  `practises` int(2) default '0',
  `training` int(2) default '0',
  `bandage` int(2) default '0',
  `alignment` int(3) default '8',
  `manastats` int(7) default '0',
  `movementstats` int(7) default '1000',
  `maxmana` int(7) default '118',
  `maxmove` int(7) default '500',
  `maxvital` int(7) default '118',
  `cgiServerSoftware` varchar(40) default NULL,
  `cgiServerName` varchar(40) default NULL,
  `cgiGatewayInterface` varchar(40) default NULL,
  `cgiServerProtocol` varchar(40) default NULL,
  `cgiServerPort` varchar(40) default NULL,
  `cgiRequestMethod` varchar(40) default NULL,
  `cgiPathInfo` varchar(40) default NULL,
  `cgiPathTranslated` varchar(40) default NULL,
  `cgiScriptName` varchar(40) default NULL,
  `cgiRemoteHost` varchar(40) default NULL,
  `cgiRemoteAddr` varchar(40) default NULL,
  `cgiAuthType` varchar(40) default NULL,
  `cgiRemoteUser` varchar(40) default NULL,
  `cgiRemoteIdent` varchar(40) default NULL,
  `cgiContentType` varchar(40) default NULL,
  `cgiAccept` varchar(40) default NULL,
  `cgiUserAgent` varchar(40) default NULL,
  `jumpmana` int(4) default '1',
  `jumpmove` int(4) default '1',
  `jumpvital` int(4) default '1',
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`name`),
  KEY `owner` (`owner`),
  CONSTRAINT `mm_usertable_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `poll_choices`
--

DROP TABLE IF EXISTS `poll_choices`;
CREATE TABLE `poll_choices` (
  `id` int(11) NOT NULL default '0',
  `pollid` int(11) NOT NULL default '0',
  `choice` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`,`pollid`),
  KEY `pollid_index` (`pollid`),
  CONSTRAINT `poll_choices_ibfk_1` FOREIGN KEY (`pollid`) REFERENCES `polls` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `poll_values`
--

DROP TABLE IF EXISTS `poll_values`;
CREATE TABLE `poll_values` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(20) NOT NULL default '',
  `value` int(11) default NULL,
  `comments` text,
  PRIMARY KEY  (`id`,`name`),
  CONSTRAINT `poll_values_ibfk_1` FOREIGN KEY (`id`) REFERENCES `polls` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `polls`
--

DROP TABLE IF EXISTS `polls`;
CREATE TABLE `polls` (
  `id` int(11) NOT NULL default '0',
  `title` varchar(255) NOT NULL default '',
  `description` varchar(255) NOT NULL default '',
  `closed` int(1) NOT NULL default '0',
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `owner_index` (`owner`),
  CONSTRAINT `polls_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `mm_admin` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `scratchpad`
--

DROP TABLE IF EXISTS `scratchpad`;
CREATE TABLE `scratchpad` (
  `id` int(11) NOT NULL default '0',
  `scratch` longblob NOT NULL,
  `owner` varchar(20) default NULL,
  `creation` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

# SET FOREIGN_KEY_CHECKS=1;
END_OF_DATA
