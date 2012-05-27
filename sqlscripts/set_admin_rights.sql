#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done periodically to set the proper admin rights.
#

update mm_usertable set god = 1 
where exists (
	select * 
	from mm_admin 
	where mm_admin.name = mm_usertable.name 
	and validuntil > now());

update mm_usertable set god = 0 
where exists (
	select * 
	from mm_admin 
	where mm_admin.name = mm_usertable.name 
	and validuntil < now());

END_OF_DATA

