#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

/* small, unrelated changes, due to methods that have to be changed to make
use of the new itemtable format */

REPLACE INTO methods (id, name, src) VALUES (56,'givestick','if sql(\"select 1 from tmp_itemtable tmpitems where (tmpitems.id=56) and \\
(tmpitems.room = 0) and \\
(tmpitems.search = \'\') and \\
(tmpitems.belongsto = \'%me\') and \\
(tmpitems.wearing = \'\') and \\
tmpitems.wielding = \'\'\")
	if sql(\"select 1 from tmp_itemtable tmpitems where (tmpitems.id=39) and \\
	(tmpitems.room = 0) and \\
	(tmpitems.search = \'\') and \\
	(tmpitems.belongsto = \'%me\') and \\
	(tmpitems.wearing = \'\') and \\
	tmpitems.wielding = \'\'\")
		return
	else
		say(\"You give a stick to Karaoke.<BR>\")
		sayeveryone(\"%me gives a stick to Karaoke.<BR>\")
		say(\"You receive a key from Karaoke.<BR>You now have an old rusty key.<BR>\")
		sayeveryone(\"Karaoke gives a key to %me.<BR>\")
		sql(\"update tmp_itemtable set amount=amount-1 where id=56 and \\
		room = 0 and \\
		search = \'\' and \\
		belongsto = \'%me\' and \\
		wearing = \'\' and \\
		wielding = \'\'\")
		sql(\"delete from tmp_itemtable where id=56 and \\
		amount = 0 and \\
		room = 0 and \\
		search = \'\' and \\
		belongsto = \'%me\' and \\
		wearing = \'\' and \\
		wielding = \'\'\")
		sql(\"insert into tmp_itemtable values(39,\'\',\'%me\', 1, 0,  \'\', \'\',0)\")
		show(\"select contents from action where id=11\")
	end
end
return
');
REPLACE INTO methods (id, name, src) VALUES (68,'openaccount','sayeveryone(\"%me says [to Kroonz] :  Can you open an account for me?<BR>\")
if sql(\" select 1 from tmp_itemtable where id=36 and search=\'%me\' and belongsto=\'\' and room=-1 and wearing=\'\' and wielding=\'\'\")
	say(\"Kroonz says [to you] : You already have an account here.<BR>\")
	sayeveryone(\"Kroonz says [to %me] :  You already have an account here.<BR>\")
	showstandard
else
	say(\"Kroonz says [to you] : You now have an account.<BR>\")
	sayeveryone(\"Kroonz says [to %me] : You now have an account.<BR>\")
	sql(\"insert into tmp_itemtable values(36, \'%me\', \'\', 0, -1, \'\',\'\',0)\")
	sql(\"insert into tmp_itemtable values(37, \'%me\', \'\', 0, -1, \'\',\'\',0)\")
	sql(\"insert into tmp_itemtable values(38, \'%me\', \'\', 0, -1, \'\',\'\',0)\")
	showstandard
end
return
');
REPLACE INTO methods (id, name, src) VALUES (75,'getkey','\r
sayeveryone(\"%me takes the key from the pillar, yet another immediately appears.<BR> When you look around you, %me seems to have suddenly disappeared.<BR>\")\r
sql(\"insert into tmp_itemtable values (125,\'\',\'%me\',1,0,\'\',\'\',0)\")\r
sql(\"update tmp_usertable set room=182 where name=\'%me\'\")\r
set room=182\r
say(\"You get the key from the pillar, and immediately  you appear to be back in the sewers. How odd, it just happened, and you didn\'t notice.<BR>\")\r
sayeveryone(\"%me fades into existance.<BR>\")\r
showstandard\r
return');



/* put rope in chest, if it seems to be empty */

update items set id=57 where id=53;
update tmp_itemtable set id=57 where id=53;
update itemtable set id=57 where id=53;
update olditem set id=57 where id=53;
update containeditems set id=57 where id=53;

update items 
set container=10
where id = -91;

replace into events
values(17, "replenish_rope",-1,-1,-1,1, -1, 1,
"replenish_rope","",20);

replace into items values(58, 'rope','thick','piece of'
,'long',0,0,0,0,'','',0,0,1,1,1,0,'<H1>Piece of thick Rope</H1>
<IMG SRC="http://www.karchan.org/images/gif/letters/t.gif" ALIGN=left>
his seems to be a thick piece of rope, white, and made to carry a lot of 
weight. It is approximately 10 meters long. It is excellently made by a
master roper. It contains of three different strands all interwoven with
each other for durability and strength.<P>','',0,0,0,0,
4,0,0,0);

replace into methods
values(80, "replenish_rope", "debug
if sql(""select 1 from tmp_itemtable where id=-91 and room=20 and containerid=0"")
	sql(""update tmp_itemtable set containerid=-1 where id=-91 and room=20"")
	sql(""insert into containeditems (id, amount, containedin) values(58, 1, -1)"")
end
");

/* look in chest, when chest is close/open */

replace into commands
values(86, "look_in_chest", 1, "look%chest", "look_in_chest", '', 20);

replace into methods
values(82, "look_in_chest", "
if sql(""select 1 from items where id=-91 and adject1<>'open'"")
	if sql(""select \'%*\' like \'look in%\'"")
		say(""You cannot look in the chest, it\'s closed.<BR>"")
		showstandard
	else
		show(""select description from items where id=-91"")
	end
end
return
");

/* look in well */
replace into commands
values(84, "look_in_well", 1, "look in well", "look_in_well", '', 5);
replace into commands
values(85, "look_in_well2", 1, "look in dried up well", "look_in_well", '', 5);

replace into methods
values(81, "look_in_well", "say(""You look down the well.<BR>"")
sayeveryone(""%me looks down the well.<BR>"")  
show(""select contents from action where id=15"")
return
");

replace into action
values(15, "<H1>Down the well</H1>
<IMG SRC=""http://www.karchan.org/images/gif/letters/f.gif"" ALIGN=left>
rom here it is impossible to discern the bottom of the well. It is quite
dark down there, yet you should have enough light due to the opening of the
well. However, from up here nothing remarkable can be discerned below.<P>
You do notice, however, that the well seems to have been standing dry
for quite some time.<P>");

update rooms set up=0, contents="<H1>
<IMG SRC=""http://www.karchan.org/images/gif/donwell.gif"">At
the bottom of the Well</H1> <IMG
SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>ou are
presently at the bottom of the well. It is very dark in here. 
To the north, south and west corridors the darkness seems to stretch
towards some strange unknown destiny.<P>
" where id=24;

/* attach rope to well */
replace into commands
values(87, "attach_rope", 1, "use rope with well", "attach_rope", '', 5);

replace into methods
values(83, "attach_rope", "
# three possibilities:
#   1: well driedup-norope
#   2: well driedup-rope
#   3: well filled-norope
/* need to check for rope */
if sql(""select 1 from items where id=-12 and adject1=\'dried\' and adject3=\'rope\'"")
	# well driedup, rope present
	say(""A rope is already attached to the well.<BR>"")
	showstandard
end
if sql(""select 1 from items where id=-12 and adject1=\'dried\'"")
	# well driedup, rope not present	
		# check for rope in inventory
		if sql(""select 1 from tmp_itemtable where id=58 and \\\\
belongsto='%me' and wearing='' and wielding='' and search='' and room=0"")
			sql(""update tmp_itemtable set amount=amount-1 where id=58 and \\\\
belongsto='%me' and wearing='' and wielding='' and search='' and room=0"")
			sql(""delete from tmp_itemtable where amount=0 and id=58 and \\\\
belongsto='%me' and wearing='' and wielding='' and search='' and room=0"")
			# fix room description 5
			sql(""update rooms set contents='<H1>\\\\
	<IMG SRC=""http://www.karchan.org/images/gif/well.gif"">\\\\
	The Village</H1> <IMG\\\\
	 SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>ou are\\\\
	 standing in the middle of what seems to be the village square. In the middle\\\\
	 of this square is a dried up well which hasn\\\'t been used for quite some\\\\
	 time. There is a rope attached to the well, and it seems to provide a way\\\\
	 down. To the north there is an old dirty tavern called ""The Twisted Dwarf""\\\\
	 where you could get something to drink, if you had any money. In the east a\\\\
	 small shop can be seen which handles in about anything. In the west a tree\\\\
	 is casting his shadow over a small bench on which a very old dwarf is\\\\
	 seated. In the south there is a road that seems to lead nowhere.<P>' where id=5"")
			# fix room description 24
			sql(""update rooms set contents='<H1>\\\\
	<IMG SRC=""http://www.karchan.org/images/gif/donwell.gif"">At\\\\
	 the bottom of the Well</H1> <IMG\\\\
	 SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>ou are\\\\
	 presently at the bottom of the well. It is very dark in here. \\\\
	 To the north, south and west corridors the darkness seems to stretch\\\\
	 towards some strange unknown destiny. Right in front of you, \\\\
	 you notice a rope. Apparently it is fastened to the well above you \\\\
	 and you should be able to climb up.<P>\\\\
	' where id=24"")
			# fix look/at/well description
			sql(""update items set description='<H1><IMG\\\\
	 SRC=""http://www.karchan.org/images/gif/well1.gif"">\\\\
	The Well</H1><HR>\\\\
	 In the middle of the square is a dryed up well which hasn\\\'t been used for\\\\
	 quite some time. It is made of rough rocks put together, and although it\\\\
	 isn\\\'t beautiful to look at it sure is solid. Above the well a wooden\\\\
	 cylinder is placed. Right now you notice that a rope is attached to it.\\\\
	 The rope is leading downwards into the unused well.<P>' where id=-12"")
			sql(""update items set adject3=\'rope\' where id=-12"")
			say(""You attach the rope to the well. It appears you created a way down.<BR>"")
			sayeveryone(""%me attaches a rope to the well.<BR>"")
			showstandard
		else
			say(""You do not have a rope.<BR>"")
			showstandard
		end
	else
		showstandard
	end
end
return
");

/* remove rope from well */
replace into commands
values(88, "remove_rope", 1, "get rope%", "remove_rope", "", 5);


replace into methods
values(84, "remove_rope", "
if sql(""select 1 from items where id=-12 and adject1=\'dried\' and adject3=\'rope\'"")
	# well driedup, rope present
		# add rope to inventory
		if sql(""select 1 from tmp_itemtable where id=58 and \\\\
belongsto='%me' and wearing='' and wielding='' and search='' and room=0"")
			sql(""update tmp_itemtable set amount=amount+1 where id=58 and \\\\
belongsto='%me' and wearing='' and wielding='' and search='' and room=0"")
			else
			sql(""insert into tmp_itemtable \\\\
(id, search, belongsto, amount, room, wearing, wielding) \\\\
values(58, '', '%me', 1, 0, '', '')"")
			end
		# fix room description 5
		sql(""update rooms set contents='<H1>\\\\
<IMG SRC=""http://www.karchan.org/images/gif/well.gif"">\\\\
The Village</H1> <IMG\\\\
 SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>ou are\\\\
 standing in the middle of what seems to be the village square. In the middle\\\\
 of this square is a dried up well which hasn\\\\'t been used for quite some\\\\
 time. To the north there is an old dirty tavern called ""The Twisted Dwarf""\\\\
 where you could get something to drink, if you had any money. In the east a\\\\
 small shop can be seen which handles in about anything. In the west a tree\\\\
 is casting his shadow over a small bench on which a very old dwarf is\\\\
 seated. In the south there is a road that seems to lead nowhere.<P>' where id=5"")
		# fix room description 24
		sql(""update rooms set contents='<H1>\\\\
<IMG SRC=""http://www.karchan.org/images/gif/donwell.gif"">At\\\\
 the bottom of the Well</H1> <IMG\\\\
 SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>ou are\\\\
 presently at the bottom of the well. It is very dark in here. \\\\
 To the north, south and west corridors the darkness seems to stretch\\\\
 towards some strange unknown destiny.<P>\\\\
' where id=24"")
		# fix look/at/well description
		sql(""update items set description='<H1><IMG\\\\
 SRC=""http://www.karchan.org/images/gif/well1.gif"">\\\\
The Well</H1><HR>\\\\
 In the middle of the square is a dryed up well which hasn\\\\'t been used for\\\\
 quite some time. It is made of rough rocks put together, and although it\\\\
 isn\\\\'t beautiful to look at it sure is solid. Above the well a wooden\\\\
 cylinder is placed, probably in old times a rope was attached to it,\\\\
 because you still can see the marks on the wood. With the rope there\\\\
 was always the possibility of somebody being able to get some water\\\\
 but nowadays, without the rope it is impossible to extract any water\\\\
 from the well.<P>' where id=-12"")
	sql(""update items set adject3=\'\' where id=-12"")
	say(""You untie the rope from the well. You now have a rope.<BR>"")
	sayeveryone(""%me unties the rope from the well.<BR>"")
	# add rope to inventory
	showstandard
end
return
");

/* go down well, if rope is there */
replace into commands
values(89, "go_down_well", 1, "down", "go_down_well", '', 5);

replace into commands
values(90, "go_down_well2", 1, "go down", "go_down_well", '', 5);

replace into methods
values(85, "go_down_well", "
if sql(""select 1 from items where id=-12 and adject1=\'dried\' and adject3=\'rope\'"")
	# well driedup, rope present
	sayeveryone(""%me uses the rope attached to the well, to gently climb down.<BR>"")
	set room=24
	sayeveryone(""%me appears from above, via the rope attached to the well.<BR>"")
	sql(""update tmp_usertable set room=24 where name=\'%me\'"")
	show(""select contents from action where id=16"")
end
return
");

replace into action
values(16,"<H1>You climb down the well.</H1>
<IMG SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>
ou take the rope in both hands and sit on the edge of the well. After one
last look at where the rope is attached to the well, in order to make sure
that it is fastened securely, you transfer all of your weight onto the rope.
<P>You wrap your legs around the rope and slowly and rather carefully you
work your way down, into this stuffy old and disused well.<P>
After some time you see what appears to be the ground, below you. You let go
of the rope and the last half meter you gracefully land, creating stuffy
clouds of dust.<P>
");

/* go up, if rope is there */
replace into commands
values(91, "go_up_well", 1, "up", "go_up_well", '', 24);

replace into commands
values(92, "go_up_well2", 1, "go up", "go_up_well", '', 24);

replace into methods
values(85, "go_up_well", "
if sql(""select 1 from items where id=-12 and adject1=\'dried\' and adject3=\'rope\'"")
	# well driedup, rope present
	sayeveryone(""%me uses the rope attached to the well, to gently climb up.<BR>"")
	set room=5
	sayeveryone(""%me climbs with lots of effort from the well in the square and drops \\\\
	particularly ungracefully upon the ground next to it.<BR>"")
	sql(""update tmp_usertable set room=5 where name=\'%me\'"")
	show(""select contents from action where id=17"")
end
return
");

replace into action
values(17,"<H1>You climb out of the well.</H1>
<IMG SRC=""http://www.karchan.org/images/gif/letters/t.gif"" ALIGN=left>
he rope is apparently still attached to the well, as you see is in front of
you, leading upwards. With both hands you take hold of the rope and give it
a good tug, just to make sure.<P>
You jump up, grab the rope with both hands, and wrap your legs around it as
well. Slowly and laborously you work your way up until you reach the edge of
the well.<P>
With what well may be your last bit of strength you let go of the rope, grab
onto the edge of the well and pull yourself across. You let yourself drop
particulary ungracefully upon the ground besides the well, thankfull that
that at least is behind you.<P>
");

/* shouting above well */
replace into commands
values(93, "shout_well", 1, "shout %", "shout_well", '', 5);

replace into methods
values(86, "shout_well", "if sql(""select '%02' = 'to'"")
	# shouting to someone
	if sql(""select 1 from tmp_usertable where room=5 and name='%03'"")
		set room=24
		sayeveryone(""You hear someone shouting to someone else. You have no idea \\\\
	where it is coming from.<BR>"")
		set room=5
	end
else
	# shouting to no one in particular
	set room=24
	getstring(""select substring('%*', 7)"")
	sayeveryone(""You hear someone shouting '%string'. You have no idea where \\\\
it is coming from.<BR>"")
	set room=5
end
return
");

/* shouting in well */
replace into commands
values(94, "shout_well2", 1, "shout %", "shout_well2", '', 24);

replace into methods
values(87, "shout_well2", "if sql(""select '%02' = 'to'"")
	# shouting to someone
	if sql(""select 1 from tmp_usertable where room=24 and name='%03'"")
		set room=5
		sayeveryone(""You hear someone shouting to someone else. You have no idea \\\\
	where it is coming from.<BR>"")
		set room=24
	end
else
	# shouting to no one in particular
	set room=5
	getstring(""select substring('%*', 7)"")
	sayeveryone(""You hear someone shouting '%string'. You have no idea where \\\\
it is coming from.<BR>"")
	set room=24
end
return
");



END_OF_DATA