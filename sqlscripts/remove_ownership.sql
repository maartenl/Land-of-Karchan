#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done before the deputy/administrator is
# removed from the administrator table.
#

update polls set owner=null where owner="Crissy";
update mm_guilds set owner=null where owner="Crissy";
update mm_methods set owner=null where owner="Crissy";
update mm_commands set owner=null where owner="Crissy";
update mm_events set owner=null where owner="Crissy";
update mm_items set owner=null where owner="Crissy";
update mm_rooms set owner=null where owner="Crissy";
update mm_boards set owner=null where owner="Crissy";
update mm_usertable set owner=null where owner="Crissy";
update mm_area set owner=null where owner="Crissy";

#
# remove the administrator from the administrator table
#
delete from mm_admin where name="Crissy";

END_OF_DATA

