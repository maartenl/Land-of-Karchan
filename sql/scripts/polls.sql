#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

replace into commands values(809, "polls", 1, "poll %", "polls", "", 0);

replace into methods values(302, "polls", 
"# verify if second word is integer or not
if sql(""select %amount >=2"")
	if sql(""select '%02' between minvalue and maxvalue from \\\\
polls where id = 1"")
		sql(""replace into poll_values values(1, '%me', %02, '')"")
		say(""Poll value accepted...<BR>"")
	else
		sql(""update poll_values set comments = ""%*"" where id=1 and \\\\
name='%me'"")
		say(""Poll comments accepted...<BR>"")
	end
showstandard
end
return
");

END_OF_DATA
