#!/bin/sh

echo name of script is $0
echo first argument is $1
echo second argument is $2
echo all arguments is $@
echo number of arguments is $#

if [ "$#" -ne 2 ]
then
    echo "need [oldguildname] [newguildname]"
    exit 0
fi
                  
. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# A rename is basically a copy. And then the members of the guild are updated to the new guild,.
#

insert into mm_guilds select '$2' as name,
 title,
 daysguilddeath,
 maxguilddeath,
 minguildmembers,
 minguildlevel,
 guilddescription,
 guildurl,
 bossname,
 active,
 creation,
 owner,
 logonmessage,
 colour,
 imageurl
 from mm_guilds
where name='$1';

insert into mm_guildranks select title, 
 guildlevel, 
 '$2' as guildname, 
 accept_access, 
 reject_access, 
 settings_access, 
 logonmessage_access
from mm_guildranks
where guildname = '$1';

update mm_usertable set guild='$2' where guild='$1';

delete from mm_guildranks where guildname='$1';

delete from mm_guilds where name='$1';


END_OF_DATA

