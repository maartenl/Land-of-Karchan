#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# This is the script that prepares the database to run a number of tests.
#

# set password to "secret".
update mm_usertable set password='e5e9fa1ba31ecd1ae84f75caaa474f3a663f05f4', room = 1
where name in ('Karn','Marvin','Hotblack','Slartibartfast');

# make sure there is no guild "test" or something.
delete from mm_guildranks where guildname='disaster';
update mm_usertable set guild=null where guild='disaster';
delete from mm_guilds where name='disaster';

END_OF_DATA

