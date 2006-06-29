#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# delete all tables
#

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

drop table if exists bugs;
drop table if exists characterinfo;
drop table if exists family;
drop table if exists familyvalues;
drop table if exists links;
drop table if exists mm_admin;
drop table if exists mm_answers;
drop table if exists mm_area;
drop table if exists mm_bannednamestable;
drop table if exists mm_bantable;
drop table if exists mm_boardmessages;
drop table if exists mm_boards;
drop table if exists mm_charattributes;
drop table if exists mm_charitemtable;
drop table if exists mm_commandlog;
drop table if exists mm_commands;
drop table if exists mm_errormessages;
drop table if exists mm_events;
drop table if exists mm_guildranks;
drop table if exists mm_guilds;
drop table if exists mm_help;
drop table if exists mm_ignore;
drop table if exists mm_itemattributes;
drop table if exists mm_itemitemtable;
drop table if exists mm_items;
drop table if exists mm_itemtable;
drop table if exists mm_log;
drop table if exists mm_logonmessage;
drop table if exists mm_mailtable;
drop table if exists mm_methods;
drop table if exists mm_roomattributes;
drop table if exists mm_roomitemtable;
drop table if exists mm_rooms;
drop table if exists mm_shopkeeperitems;
drop table if exists mm_sillynamestable;
drop table if exists mm_unbantable;
drop table if exists mm_usertable;
drop table if exists poll_choices;
drop table if exists poll_values;
drop table if exists polls;
drop table if exists scratchpad;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

END_OF_DATA

