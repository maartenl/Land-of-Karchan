#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

/*drop table polls
drop table poll_values

create table polls
(id int not null primary key,
title varchar(255) not null,
description varchar(255) not null,
minvalue int not null,
maxvalue int not null)

create table poll_values
(id int not null,
name varchar(20) not null,
value int,
comments text,
primary key (id, name))

*/

replace into polls values(1, "Plague appreciation poll", "determine if people
liked the plague idea. Values:
1. Way cool! Do more of those!
2. Was fun.
3. Was okay, I guess.
4. Didn't like it.
5. Dude, you suck!
6. What Plague?
7. Huh? Polls?
", 1, 7);

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
