#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

replace into commclaimed
values(11,"800 - 899",'Karn');

replace into commands
values(800, "plague1", 1, "%", "plague1", "", 0);

replace into methods 
values(92, "plague1", "# if you look at the appropriate command in the table
# commands you will see that this particular method will ALWAYS be performed!

# every minute, check to see if person is below 50, and minute is the same.

if sql(""select 1 from tmp_attributes \\\\
where name ='plague' \\\\
and objectid='%me' \\\\
and objecttype=1"")
	getstring(""select tmp_attributes.value from tmp_attributes \\\\
	where name ='plague' \\\\
	and objectid='%me' \\\\
	and objecttype=1"")
	if sql(""select %string <= 50 and rand()<=0.005"")
		if sql(""select %string >=40"")
			say(""You feel temporarily faint and have to take care not to fall down.<BR>"")
			sayeveryone(""You notice %me suddenly swaying uncertainly. With some effort %me seems to refind the balance.<BR>"")
		end
		if sql(""select %string < 40 and %string >= 30"")
			if sql(""select rand()<0.5"")
				say(""You cough suddenly.<BR>"")
				sayeveryone(""%me coughs suddenly.<BR>"")
			else
				say(""You moan painfully.<BR>"")
				sayeveryone(""%me moans painfully.<BR>"")
			end
		end
		if sql(""select %string < 30 and %string >= 20"")
			say(""Black spots appear before your eyes and you faint.<BR>"")
			sayeveryone(""You see %me crash down to the ground in a dead faint.<BR>"")
		end
		if sql(""select %string < 20"")
			say(""You fold double from chestpains, coughing up blood.<BR>"")
			sayeveryone(""%s suddenly folds almost double, and has a nasty fit of coughing. Blood seems to dribble from %mes mouth.<BR>"")
		end
		sql(""update tmp_usertable set vitals=maxvital-%string where name='%me'"")
		return
	end
end

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
if sql(""select 1 from tmp_attributes where objectid='%me' and objecttype=1 and name='plague' and value>0"")
	getstring(""select value from tmp_attributes where objectid='%me' and objecttype=1 and name='plague' and value>0"")
	if sql(""select %string < 10"")
		sql(""replace into tmp_attributes values('look','looking unearthly pale, with red-rimmed and unfocused feverish eyes. Lips seem to be \\\\
pressed together in a permanent grimace of pain. Unsightly white blodges are spread across the arms and face.','string','%me',1)"")
	end
	if sql(""select %string >= 10 and %string < 20"")
		sql(""replace into tmp_attributes values('look','looking unearthly pale, with red-rimmed and unfocused feverish eyes. Unsightly white \\\\
blodges are spread across the arms and face.','string','%me',1)"")
	end
	if sql(""select %string >= 20 and %string < 30"")
		sql(""replace into tmp_attributes values('look','looking pale with red-rimmed eyes and unsightly white blodges spread across the arms and face.','string','%me',1)"")
	end
	if sql(""select %string >= 30 and %string < 40"")
		sql(""replace into tmp_attributes values('look','looking pale, with red-rimmed eyes.','string','%me',1)"")
	end
	if sql(""select %string >= 40 and %string < 50"")
		sql(""replace into tmp_attributes values('look','looking paler than usual.','string','%me',1)"")
	end
end
return
");

replace into events
values(19, "plague1", -1, -1, 12, 0, -1, 1, "plague3", "", 0);
replace into methods
values(94,"plague3",
"# once a day, diminish plague by one if user logged on today
sql(""update tmp_attributes set value=value-1 where mod(value, 2)=1 and objecttype=1 and name='plague' and value>0"")
sql(""update attributes set value=value-1 where mod(value, 2)=1 and objecttype=1 and name='plague' and value>0"")
return
");

END_OF_DATA
