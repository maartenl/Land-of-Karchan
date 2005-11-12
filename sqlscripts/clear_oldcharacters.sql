#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# totally delete a specific user from the database.
#


# delete all items in containers that belong to the user
create temporary table tmptable
select mm_itemitemtable.id 
from mm_charitemtable, mm_itemitemtable
where mm_charitemtable.belongsto = "$1" and
mm_itemitemtable.containerid = mm_charitemtable.id;

delete from mm_itemattributes
where id in (select id from tmptable);
delete from mm_itemitemtable
where id in (select id from tmptable);
delete from mm_itemtable
where id in (select id from tmptable);

drop table tmptable;
# end of 'delete all items in containers that belong to the user'

# delete all items that belong to the user
create temporary table tmptable
select mm_charitemtable.id 
from mm_charitemtable
where mm_charitemtable.belongsto = "$1";

delete from mm_itemattributes
where id in (select id from tmptable);
delete from mm_charitemtable
where id in (select id from tmptable);
delete from mm_itemtable
where id in (select id from tmptable);

drop table tmptable;
# end of 'delete all items that belong to the user

# delete all mudmail from and to the user
delete from mm_mailtable where
name = "$1" or toname = "$1";
# end of 'delete all mudmail from and to the user

delete from mm_charattributes
where charname = "$1";

delete from mm_usertable
where name = "$1";

/* error! What do we do with the damn items in a container? */

insert into mm_log (name, message) values("$1", "deleted during cleanup, not logged on since $2 $3.");

END_OF_DATA

