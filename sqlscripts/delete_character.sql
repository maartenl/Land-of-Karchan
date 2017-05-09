#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# totally delete a specific user from the database.
#

# delete all items in containers that belong to the user
create temporary table tmptable
select contained.id 
from mm_itemtable container, mm_itemtable contained
where container.belongsto = "$1" and
contained.containerid = container.id;

delete from mm_itemattributes
where id in (select id from tmptable);
delete from mm_itemtable
where id in (select id from tmptable);

drop table tmptable;
# end of 'delete all items in containers that belong to the user'

# delete all items that belong to the user
create temporary table tmptable
select mm_itemtable.id 
from mm_itemtable
where mm_itemtable.belongsto = "$1";

delete from mm_itemattributes
where id in (select id from tmptable);
delete from mm_itemtable
where id in (select id from tmptable);

drop table tmptable;
# end of 'delete all items that belong to the user

# delete all entries from mm_ignore
delete from mm_ignore where fromperson = "$1" or toperson = "$1";

# delete all mudmail from and to the user
delete from mm_mailtable where
name = "$1" or toname = "$1";
# end of 'delete all mudmail from and to the user

delete from mm_boardmessages
where name = "$1";

delete from mm_charattributes
where charname = "$1";

delete from mm_usertable
where name = "$1";

/* error! What do we do with the damn items in a container? */

insert into mm_log (name, message) values("$1", "deleted by Karn.");

select "$1 deleted by Karn.";

END_OF_DATA

