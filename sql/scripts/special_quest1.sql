#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

/* some notes concerning the plague

Plague in attributes: plague, 100, integer, playername, 1
Cure in attributes: plaguecure, true, string, playername, 1

First manner of business-> make tmp_attributes, done

Provide somebody with the plague:
insert into tmp_attributes values("plague","100","integer","tanth",1)


*/

replace into commclaimed
values(11,"800 - 899",'Karn');

replace into commands
values(800, "plague1", 1, "%", "plague1", "", 0);

replace into methods 
values(92, "plague1", "# if you look at the appropriate command in the table
# commands you will see that this particular method will ALWAYS be performed!
if sql(""select 1 \\\\
from tmp_usertable tmp1, tmp_usertable tmp2, tmp_attributes attr1 \\\\
where tmp2.name = '%me' \\\\
and tmp1.room = tmp2.room \\\\
and tmp1.name <> tmp2.name \\\\
and attr1.name='plague' \\\\
and attr1.objectid = tmp1.name \\\\
and attr1.objecttype=1"")
	# someone in the room (not me) has the plague
	if sql(""select 1 from tmp_attributes attr where \\\\
((attr.name='cure' and attr.value='true') or (attr.name='plague')) \\\\
and attr.objectid = '%me' and attr.objecttype=1"")
		# me has cure or is already infected
		return
	else
		# me has no cure 
		if sql(""select round(rand()*10)=1"")
			# me got stuck with it
			sql(""insert into tmp_attributes values('plague','100','integer','%me',1)"")
		end
	end
end
return
");

replace into commands
values(801, "plague2", 1, "quit", "plague2", "", 0);
replace into methods
values(93, "plague2", "# this method is always run upon logging out of the game.
sql(""update tmp_attributes set value=value-1 where mod(value, 2)=0 and objectid='%me' and objecttype=1 and name='plague' and value>0"")
return
");

replace into events
values(19, "plague1", -1, -1, 12, 00, -1, 1, "plague3", "", 0);
replace into methods
values(94,"plague3",
"# once a day, diminish plague by one if user logged on today
sql(""update tmp_attributes set value=value-1 where mod(value, 2)=1 and objecttype=1 and name='plague' and value>0"")
sql(""update attributes set value=value-1 where mod(value, 2)=1 and objecttype=1 and name='plague' and value>0"")
return
");

END_OF_DATA
