#!/bin/sh
. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# Because the game went down, we are extending ALL deps until the admin pages are back.
#

select concat("Expiration date for ", mm_admin.name, " was ", validuntil, ".")
from mm_admin
where mm_admin.name in ("Eilan", "Ephinie", "Midevia", "Mya", "Victoria");

# adds a month... always
update mm_admin set validuntil = date_add(validuntil, interval 1 month) where name in ("Eilan", "Ephinie", "Midevia", "Mya", "Victoria");

# if with added month date is still less than a month from now, change it to one month from now
update mm_admin set validuntil = date_add(now(), interval 1 month) 
where name in ("Eilan", "Ephinie", "Midevia", "Mya", "Victoria")  
and validuntil < date_add(now(), interval 1 month);

select concat("Expiration date for ", mm_admin.name, " changed to ", validuntil, " because admin pages don't work due to crash.")
from mm_admin
where mm_admin.name in ("Eilan", "Ephinie", "Midevia", "Mya", "Victoria");

# addendum is also possible, but not necessary here.
insert into mm_log
(message, name) 
select concat("Expiration date for ", mm_admin.name, " changed to ", validuntil, " because admin pages don't work due to crash."), mm_admin.name
from mm_admin
where mm_admin.name in ("Eilan", "Ephinie", "Midevia", "Mya", "Victoria");

END_OF_DATA

