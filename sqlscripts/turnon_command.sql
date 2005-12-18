#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done in case a certain command generates exceptions.
#

update mm_commands set callable=1 where id=53;
update mm_usertable set room=3305 where name="Karn";

END_OF_DATA

