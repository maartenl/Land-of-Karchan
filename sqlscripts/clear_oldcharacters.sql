#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# totally delete a specific user from the database.
#

# insert into mm_log (name, message) ("$1", "deleted, not logged on since $2");

select name, "$2" as lastlogin
from mm_usertable
where name = "$1";

END_OF_DATA

