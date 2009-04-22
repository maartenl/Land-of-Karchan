#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# update the description of a specific board.
#

select concat("\"", name, "\"") as name, lastlogin
from mm_usertable 
where lastlogin < date_sub(curdate(),interval 6 month)
and god < 2;

END_OF_DATA

