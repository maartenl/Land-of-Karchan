#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# show the fields and associated stuff of a table.
#

repair table bugs;
repair table characterinfo;
repair table family;
repair table familyvalues;
repair table links;
repair table mm_admin;
repair table mm_answers;
repair table mm_area;
repair table mm_bannednamestable;
repair table mm_bantable;
repair table mm_boardmessages;
repair table mm_boards;
repair table mm_charattributes;
repair table mm_charitemtable;
repair table mm_commandlog;
repair table mm_commands;
repair table mm_errormessages;
repair table mm_events;
repair table mm_guildranks;
repair table mm_guilds;
repair table mm_help;
repair table mm_ignore;
repair table mm_itemattributes;
repair table mm_itemitemtable;
repair table mm_items;
repair table mm_itemtable;
repair table mm_log;
repair table mm_logonmessage;
repair table mm_mailtable;
repair table mm_methods;
repair table mm_roomattributes;
repair table mm_roomitemtable;
repair table mm_rooms;
repair table mm_shopkeeperitems;
repair table mm_sillynamestable;
repair table mm_unbantable;
repair table mm_usertable;
repair table poll_choices;
repair table poll_values;
repair table polls;
repair table scratchpad;

END_OF_DATA
