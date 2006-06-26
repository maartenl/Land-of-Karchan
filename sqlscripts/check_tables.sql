#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# show the fields and associated stuff of a table.
#

check table bugs;
check table characterinfo;
check table family;
check table familyvalues;
check table links;
check table mm_admin;
check table mm_answers;
check table mm_area;
check table mm_bannednamestable;
check table mm_bantable;
check table mm_boardmessages;
check table mm_boards;
check table mm_charattributes;
check table mm_charitemtable;
check table mm_commandlog;
check table mm_commands;
check table mm_errormessages;
check table mm_events;
check table mm_guildranks;
check table mm_guilds;
check table mm_help;
check table mm_ignore;
check table mm_itemattributes;
check table mm_itemitemtable;
check table mm_items;
check table mm_itemtable;
check table mm_log;
check table mm_logonmessage;
check table mm_mailtable;
check table mm_methods;
check table mm_roomattributes;
check table mm_roomitemtable;
check table mm_rooms;
check table mm_shopkeeperitems;
check table mm_sillynamestable;
check table mm_unbantable;
check table mm_usertable;
check table poll_choices;
check table poll_values;
check table polls;
check table scratchpad;

END_OF_DATA;
