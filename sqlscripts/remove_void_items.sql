#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# totally remove instances of items that are no longer referenced by people/rooms/bags/etc.
#

select "Deleting item attributes...";
delete from mm_itemattributes
where not exists (select id from mm_charitemtable where mm_charitemtable.id=mm_itemattributes.id) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.id=mm_itemattributes.id) and
      not exists (select id from mm_roomitemtable where mm_roomitemtable.id=mm_itemattributes.id);

select "Deleting items themselves...";
delete from mm_itemtable
where not exists (select id from mm_charitemtable where mm_charitemtable.id=mm_itemtable.id) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.id=mm_itemtable.id) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.containerid=mm_itemtable.id) and
      not exists (select id from mm_roomitemtable where mm_roomitemtable.id=mm_itemtable.id);

END_OF_DATA
            
            
            
            
            