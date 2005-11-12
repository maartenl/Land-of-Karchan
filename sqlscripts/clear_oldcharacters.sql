#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# totally delete a specific user from the database.
#


delete from mm_charattributes
where charname = "$1";

delete from mm_usertable
where name = "$1";

delete from mm_itemtable, mm_charitemtable
where mm_charitemtable.belongsto = "$1";

/* error! What do we do with the damn items in a container? */

insert into mm_log (name, message) values("$1", "deleted during cleanup, not logged on since $2.");

END_OF_DATA

