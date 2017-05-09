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

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# remove the administrator from the administrator table
#
delete from mm_admin_mm_groups where name = "$1";
delete from mm_admin where name = "$1";

END_OF_DATA

