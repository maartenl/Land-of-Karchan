#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# show instances of items that are no longer referenced by people/rooms/bags/etc.
#

# Issue:
# what if there's an item contained in a bag, but the bag is in the void.
# problem: item cannot be deleted as it is referenced in the bag,
#          bag cannot be deleted, as it contains items
# solution: empty the bag first.
select "Void bags";
select *
from mm_itemitemtable
where not exists (select id from mm_charitemtable where mm_charitemtable.id=mm_itemitemtable.containerid) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.id=mm_itemitemtable.containerid) and
      not exists (select id from mm_roomitemtable where mm_roomitemtable.id=mm_itemitemtable.containerid);

# Issue:
# what if an item still has attributes?
# solution: remove the attributes, firstly.
select "Item attributes of void items";
select *
from mm_itemattributes
where not exists (select id from mm_charitemtable where mm_charitemtable.id=mm_itemattributes.id) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.id=mm_itemattributes.id) and
      not exists (select id from mm_roomitemtable where mm_roomitemtable.id=mm_itemattributes.id);

# Issue:
# what if an item no longer belongs to either a character, bag or room?
# solution: remove item
select "Void items";
select *
from mm_itemtable 
where not exists (select id from mm_charitemtable where mm_charitemtable.id=mm_itemtable.id) and
      not exists (select id from mm_itemitemtable where mm_itemitemtable.id=mm_itemtable.id) and
      not exists (select id from mm_roomitemtable where mm_roomitemtable.id=mm_itemtable.id);
            
END_OF_DATA
            
            
            
            
            