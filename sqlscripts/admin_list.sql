#!/bin/sh
. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# Show the admins
#

select *
from mm_admin;

END_OF_DATA

