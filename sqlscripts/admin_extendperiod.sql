#!/bin/sh
. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done periodically to extend the proper admin rights.
#

select concat("Expiration date for ", mm_admin.name, " was ", validuntil, ".")
from mm_admin
where mm_admin.name = "$1";

# adds a month... always
update mm_admin set validuntil = date_add(validuntil, interval 1 month) where name = "$1";

# if with added month date is still less than a month from now, change it to one month from now
update mm_admin set validuntil = date_add(now(), interval 1 month) where name = "$1" and validuntil < date_add(now(), interval 1 month);

select concat("Expiration date for ", mm_admin.name, " changed to ", validuntil, ".")
from mm_admin
where mm_admin.name = "$1";

# addendum is also possible, but not necessary here.
insert into mm_log
(message, name) 
select concat("Expiration date for ", mm_admin.name, " changed to ", validuntil, "."), mm_admin.name
from mm_admin
where mm_admin.name = "$1";

END_OF_DATA

