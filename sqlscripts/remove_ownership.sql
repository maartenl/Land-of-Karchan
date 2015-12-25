#!/bin/sh

echo name of script is $0
echo first argument is $1
echo second argument is $2
echo all arguments is $@
echo number of arguments is $#

if [ "$#" -ne 1 ]
then
    echo "need an administrator name"
    exit 0
fi
                  
. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done before the deputy/administrator is
# removed from the administrator table.
#

update polls set owner=null where owner="$1";
update mm_guilds set owner=null where owner="$1";
update mm_methods set owner=null where owner="$1";
update mm_commands set owner=null where owner="$1";
update mm_events set owner=null where owner="$1";
update mm_items set owner=null where owner="$1";
update mm_rooms set owner=null where owner="$1";
update mm_boards set owner=null where owner="$1";
update mm_usertable set owner=null where owner="$1";
update mm_area set owner=null where owner="$1";

END_OF_DATA

