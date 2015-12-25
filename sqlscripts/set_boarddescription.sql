#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# update the description of a specific board.
#

update mm_boards set description='<b>Logonmessage</b>
<br>
<img src="/images/gif/letters/w.gif" alt="W" align=left/>
elcome to the Land of Karchan MUD, a land filled with mystery
and enchantment, where weapons, magic, intelligence, and common
sense play key roles in the realm. Where love and war can be one
and the same. Where elves coexist peacefully with the humans, and
make war with the dwarves. Where the sun rises, and the moon falls.
Where one can change into a hero with a single swipe of his
sword.</p>' 
where id=2;

END_OF_DATA

