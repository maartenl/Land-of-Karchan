#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# totally remove all references to www.karchan.org. They are unneeded. All paths should be relative.
#

update mm_rooms set contents = replace(contents, "http://www.karchan.org","");

END_OF_DATA

