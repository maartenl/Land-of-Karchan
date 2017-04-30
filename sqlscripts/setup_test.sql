#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This is the script that prepares the database to run a number of tests.
#

# set password to "secret".

# make sure there is no guild "test" or something.
delete from mm_guildranks where guildname='disaster';
update mm_usertable set guild=null where guild='disaster';
delete from mm_guilds where name='disaster';

END_OF_DATA

