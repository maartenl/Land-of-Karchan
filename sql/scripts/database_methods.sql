#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

update methods
set src = "say(\"You yawn.<BR>\")
sayeveryone(\"%me yawns.<BR>\")
showstandard
return
" where id = 2;

update methods
set src = "if sql(\"select 1 from tmp_usertable where name='%me' and room in (1,3,164)\")
	say(\"Fighting is not allowed in this area.<BR>\")
	showstandard
return
" where id = 4;

update methods
set src = "if sql(""select 1 from rooms where id=20 and south=0"")
	say(""You succeed in opening the door of the cupboard. \\\\
		 [<A HREF=""http://www.karchan.org/images/mpeg/her.mpg"">\\\\
		MPEG</A>]<BR>"")
	sayeveryone(""%me  opens the door of the cupboard.<BR>"")
	sql(""update items set description='<H1><IMG SRC=""http://www.karchan.org/images/gif/herberg5.gif"">The Cupboard</H1><HR>\\\\
		You look at the cupboard. It is very old and wormeaten. With one\\\\
		knock you could probably knock it down, but I doubt if the barman would appreciate \\\\
		this much. It is open. Both doors of the cupboard are ajar. In it you can \\\\
		see, amazingly, a staircase leading to the north and up into a hidden\\\\
		room.<P>', adject1='comparatively',\\\\
		adject2='big', name='cupboard' where id=-32"")
	sql(""update rooms set contents='<IMG SRC=""http://www.karchan.org/images/gif/herberg4.gif"" ALIGN=CENTER> \\\\
		<H1>The Taverne &quot;The Twisted Dwarf&quot;</H1>\\\\
		<IMG SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>\\\\
		ou are now in the Inn &quot;The Twisted Dwarf&quot; . It is \\\\
		dark, as always in these places. The windows are of a dark blue color, which \\\\
		doesn\\\\'t allow any light to enter the room. A lot of woodwork, wooden tables, \\\\
		chairs and a rather large bar on the right of you is situated almost against the \\\\
		the back of the room a comparatively big cupboard is visible. The cupboard \\\\
		appears to be open. Behind the bar a norse small ugly dwarf is cleaning some \\\\
		glasses at the bar. On the same bar you see a sign on a piece of wood, \\\\
		apparently this is the menu for the day.<BR> Scattered among the tables are \\\\
		groups of people, playing what seems to be a dwarfish version of Poker.You\\\\
		see a sign on the wall behind the counter.<P>', north=20 where id=9"")
	sql(""update rooms set south=9 where id=20"")
	showstandard
#	show(""select contents from action where id=7"")
else
	say(""The cupboard is already open.<BR>"")
	showstandard
end
return
" where id=1;

update methods
set src = "if sql(""select 1 from rooms where id=20 and south=9"")
	say(""You succeed in closing the door of the cupboard. \\\\
		 [<A HREF=""http://www.karchan.org/images/mpeg/her2.mpg"">\\\\
		MPEG</A>]<BR>"")
	sayeveryone(""%me  closes the door of the cupboard.<BR>"")
	sql(""update items set description='<H1><IMG SRC=""http://www.karchan.org/images/gif/herberg3.gif"">The Cupboard</H1><HR>\\\\
		You look at the cupboard. It is very old and wormeaten. With one\\\\
		knock you could probably knock it down, but I doubt if the barman would appreciate \\\\
		this much. It hasn\\\\'t got a lock so you could open it, I doubt if the barman \\\\
		would mind.<P>', adject1='comparatively',\\\\
		adject2='big', name='cupboard' where id=-32"")
	sql(""update rooms set contents='<IMG SRC=""http://www.karchan.org/images/gif/herberg1.gif"" ALIGN=CENTER> \\\\
		<H1>The Taverne &quot;The Twisted Dwarf&quot;</H1>\\\\
		<IMG SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>\\\\
		ou are now in the Inn &quot;The Twisted Dwarf&quot; . It is \\\\
		dark, as always in these places. The windows are of a dark blue color, which \\\\
		doesn\\\\'t allow any light to enter the room. A lot of woodwork, wooden tables, \\\\
		chairs and a rather large bar on the right of you is situated almost against the \\\\
		the back of the room a comparatively big cupboard is visible. \\\\
		Behind the bar a norse small ugly dwarf is cleaning some \\\\
		glasses at the bar. On the same bar you see a sign on a piece of wood, \\\\
		apparently this is the menu for the day.<BR> Scattered among the tables are \\\\
		groups of people, playing what seems to be a dwarfish version of Poker.You\\\\
		see a sign on the wall behind the counter.<P>', north=0 where id=9"")
	sql(""update rooms set south=0 where id=20"")
	showstandard
#	show(""select contents from action where id=7"")
else
	say(""The cupboard is already closed.<BR>"")
	showstandard
end
return
" where id=3;

update methods
set src = "sayeveryone(""%me says [to Kainian] :  Please heal me, good priest.<BR>"")
if sql(""select 1 from tmp_usertable where name='%me' and \\\\
	copper+silver*10+gold*100>=10000"")
	sayeveryone(""Kainian prays to the Almighty Karn for his divine influence \\\\
	in the restoration of the health of %me. Suddenly %me is struck with an \\\\
	unearthly light!<BR>"")
	say(""Kainian prays to the Almighty Karn for his divine influence in the \\\\
	restoration of your health. Suddenly you feel yourself full of new \\\\
	energy!<BR>"")
	sql(""update tmp_usertable set gold=gold-100 where name='%me'"")
	sql(""update tmp_usertable set silver=silver+gold*10, gold=0 where \\\\
	name='%me' and gold<0"")
	sql(""update tmp_usertable set copper=copper+silver*10, silver=0 where \\\\
	 name='%me' and silver<0"")
	sql(""update tmp_usertable set vitals=vitals-125 where name='%me'"")
	sql(""update tmp_usertable set vitals=0 where name='%me' and vitals<0"")
else
	say(""Kainian says [to you] : You do not have enough money.<BR>"")
	sayeveryone(""Kainian says [to %me] : You do not have enough money.<BR>"")
end
showstandard
" where id = 5;

update methods
set src = "sayeveryone(""%me says [to Kainian] :  Please heal me, good priest.<BR>"")
if sql(""select 1 from tmp_usertable where name='%me' and \\\\
	copper+silver*10+gold*100>=2500"")
	sayeveryone(""Kainian prays to the Almighty Karn for his divine influence \\\\
	in the restoration of the health of %me. Suddenly %me is struck with an \\\\
	unearthly light!<BR>"")
	say(""Kainian prays to the Almighty Karn for his divine influence in the \\\\
	restoration of your health. Suddenly you feel yourself full of new \\\\
	energy!<BR>"")
	sql(""update tmp_usertable set gold=gold-25 where name='%me'"")
	sql(""update tmp_usertable set silver=silver+gold*10, gold=0 where \\\\
	name='%me' and gold<0"")
	sql(""update tmp_usertable set copper=copper+silver*10, silver=0 where \\\\
	 name='%me' and silver<0"")
	sql(""update tmp_usertable set vitals=vitals-40 where name='%me'"")
	sql(""update tmp_usertable set vitals=0 where name='%me' and vitals<0"")
else
	say(""Kainian says [to you] : You do not have enough money.<BR>"")
	sayeveryone(""Kainian says [to %me] : You do not have enough money.<BR>"")
end
showstandard
" where id = 6;

update methods
set src = "if sql(""select 1 from items where id=-3 and adject3!='loose'"")
	sql(""update items set adject3='loose' where id=-3"")
	say(""You let go of the chain, wondering what that was all about.<BR>"")
	sayeveryone(""%me pulls at the chain in the wall. You hear the ominous \\\\
	sounds of wheels churning and rope snapping, yet the sound suddenly stops \\\\
	again.<BR>"")
	show(""select contents from action where id=1"")
else
	say(""You pull vehemently, but nothing exciting happens.<BR>"")
	sayeveryone(""%me pulls vehemently on a chain in the wall.<BR>"")
	showstandard
end
return
" where id = 7;

update methods
set src = "if sql(""select 1 from items where id=-3 and adject3!='loose'"")
	say(""You appear from nowhere.<BR>"")
	sayeveryone(""%me appears from nowhere.<BR>"")
	show(""select contents from action where id=2"")
else
	say(""You crawl out of the lake, dripping wet.<BR>"")
	sayeveryone(""%me jumps into the lake.<BR>"")
	# This part needs to be fixed, attention
	sql(""update tmp_usertable set room=17 where name='%me'"")
	sayeveryone(""%me crawls out of the lake.<BR>"")
	show(""select contents from action where id=3"")
end
return
" where id = 8;

update methods
set src = "debug
# Karcas travels from shop to square
if sql(""select 1 from tmp_usertable where room = 16 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves west.<BR>"")
	sql(""update tmp_usertable set room=5 where name='Karcas'"")
	set room=5
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 9;

update methods
set src = "debug
# Karcas travels from square to road
if sql(""select 1 from tmp_usertable where room = 5 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves south.<BR>"")
	sql(""update tmp_usertable set room=3 where name='Karcas'"")
	set room=3
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 10;

update methods
set src = "debug
# Karcas travels from road to mountains
if sql(""select 1 from tmp_usertable where room = 3 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves south.<BR>"")
	sql(""update tmp_usertable set room=4 where name='Karcas'"")
	set room=4
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 11;

update methods
set src = "debug
# Karcas travels from mountains back to road
if sql(""select 1 from tmp_usertable where room = 4 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves north.<BR>"")
	sql(""update tmp_usertable set room=3 where name='Karcas'"")
	set room=3
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 12;

update methods
set src = "debug
# Karcas travels from road back to square
if sql(""select 1 from tmp_usertable where room = 3 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves north.<BR>"")
	sql(""update tmp_usertable set room=5 where name='Karcas'"")
	set room=5
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 13;

update methods
set src = "debug
# Karcas travels from square back to shop
if sql(""select 1 from tmp_usertable where room = 5 and name='Karcas' and hour(now()) % 2 = 0"")
	sayeveryone(""Karcas leaves west.<BR>"")
	sql(""update tmp_usertable set room=16 where name='Karcas'"")
	set room=16
	sayeveryone(""Karcas appears from nowhere.<BR>"")
end
return
" where id = 14;

update methods set src="debug
sql(\"update tmp_usertable set vitals = vitals - jumpvital - round(3*eatstats/30) where vitals>0\")
sql(\"update tmp_usertable set manastats = manastats - jumpmana - round(eatstats/30) where manastats>0\")
sql(\"update tmp_usertable set movementstats = movementstats - jumpmove - round(3*eatstats/30) where movementstats>0\")

sql(\"update tmp_usertable set vitals = vitals - round(3*drinkstats/30) where vitals>0 and drinkstats>0\")
sql(\"update tmp_usertable set manastats = manastats - round(drinkstats/30) where manastats>0 and drinkstats>0\")
sql(\"update tmp_usertable set movementstats = movementstats -round(3*drinkstats/30) where movementstats>0 and drinkstats>0\")

sql(\"update tmp_usertable set vitals = 0 where vitals<0\")
sql(\"update tmp_usertable set manastats = 0 where manastats<0\")
sql(\"update tmp_usertable set movementstats = 0 where movementstats<0\")

sql(\"update tmp_usertable set drinkstats = drinkstats - 1 where drinkstats>0\")
sql(\"update tmp_usertable set drinkstats = drinkstats + 1 where drinkstats<0\")
sql(\"update tmp_usertable set eatstats = eatstats - 1 where eatstats>0\")

# Corpses, decaying

sql(\"delete from tmp_itemtable where id=55\")
sql(\"delete from bogus_itemtable\")
sql(\"insert into bogus_itemtable \\\\
select 55, \'\', \'\', count(amount), room, \'\', \'\' \\\\
from tmp_itemtable where id>=40 and id<=54 group by room\")
sql(\"insert into tmp_itemtable select * from bogus_itemtable\")
sql(\"delete from tmp_itemtable where id>=40 and id<=54\")
return
" where id=50;

update methods
set src="debug
sql(""insert into tmp_itemtable select * from respawningitemtable"")
return
" where id=51;

update methods
set src="debug
sql(""update bantable set days = days - 1 where days>0"")
sql(""delete from bantable where days=0"")
return
" where id=52;

update methods
set src="debug
sql(""insert into tmp_usertable select * from bottable"")
return
" where id=53;

update methods
set src="debug
set room=8
sayeveryone(""Karaoke mutters to himself inaudibly.<BR>"")
set room=16
sayeveryone(""Karcas shouts: Goods for sale! Nice goods! Goods for sale!<BR>"")
set room=9
sayeveryone(""Bill sets the glass down he was cleaning, picks up another \\\\
glass, and continues rubbing it with the dirty towel.<BR"")
set room=63
sayeveryone(""Kainian kneels down before the altar and prays a little.<BR>"")
return
" where id=54;

update methods
set src="if sql(""select 1 from tmp_itemtable tmpitems where (tmpitems.id=2) and \\\\
(tmpitems.room = 0) and \\\\
(tmpitems.search = '') and \\\\
(tmpitems.belongsto = '%me') and \\\\
(tmpitems.wearing = '') and \\\\
tmpitems.wielding <> ''"")
	if sql(""select 1 from rooms where id=4 and south!=0"")
		say(""There are only small pebbles present.<BR>"")
	else
		say(""You are hacking away at the rocks, using your pickaxe with \\\\
			a skill of which most people can only dream.<BR>"")
		sql(""update items set description='<H1>The Pebbles</H1><HR>\\\\
			You look at the pebbles. They are solid and very small and \\\\
			they are not blocking your entrance to the south. \\\\
			Somebody apparently has been hacking away at them, or \\\\
			they wouldn\\\\'t have this small a size.<P>'\\\\
  			, adject1='small', adject2='light', name='pebbles' where id=-16"")
		sql(""update items set description='<H1>The Pebbles</H1><HR>\\\\
			You look at the pebbles. They are solid and very small and \\\\
			they are not blocking your entrance to the north. \\\\
			Somebody apparently has been hacking away at them, or \\\\
			they wouldn\\\\'t have this small a size.<P>'\\\\
  			, adject1='small', adject2='light', name='pebbles' where id=-40"")
		sql(""update rooms set contents='<H1>The Other Side</H1><Center>\\\\
			<IMG ALT=""Mountain""\\\\
			SRC=""http://www.karchan.org/images/jpeg/berg.jpg""></Center><BR> You \\\\
			appear to be on the other side of a big range of mountains, which \\\\
			make out a lonely valley. This appears to be a whole range of \\\\
			mountains, shutting the people out from the outside world. At \\\\
			least, that would be the case if the passage you see to the north \\\\
			was blocked. Now, however, is seems to provide a clear exit to the \\\\
			north. Only some insignificant little pebbles bar your way.<P>' \\\\
			, north=4 where id=23"")
		sql(""update rooms set contents='<H1>The Road</H1> <Center><IMG \\\\
			ALT=""Mountain"" \\\\
			SRC=""http://www.karchan.org/images/jpeg/berg.jpg""></Center><BR>\\\\
			<IMG SRC=""http://www.karchan.org/images/gif/letters/y.gif"" \\\\
			ALIGN=left>ou are standing on a road with leads North and \\\\
			South. Towards the south the road used to be blocked by heavy \\\\
			stones, but now there are only little light pebbles visible. You \\\\
			have a clear way towards the south now. Towards the north the \\\\
			road is tolerably well and would make travelling an easy matter. \\\\
			To the west a forest stretches out. It is impossible to go to \\\\
			the east due to the large mountains that are that way. <P>',\\\\
			south=23 where id=4"")
		show(""select contents from action where id=0"")
	end
else
	say(""You are not wielding a pick.<BR>"")
end
showstandard
return
" where id=55;

update methods
set src="if sql(""select 1 from tmp_itemtable tmpitems where (tmpitems.id=56) and \\\\
(tmpitems.room = 0) and \\\\
(tmpitems.search = '') and \\\\
(tmpitems.belongsto = '%me') and \\\\
(tmpitems.wearing = '') and \\\\
tmpitems.wielding = ''"")
	if sql(""select 1 from tmp_itemtable tmpitems where (tmpitems.id=39) and \\\\
	(tmpitems.room = 0) and \\\\
	(tmpitems.search = '') and \\\\
	(tmpitems.belongsto = '%me') and \\\\
	(tmpitems.wearing = '') and \\\\
	tmpitems.wielding = ''"")
		return
	else
		say(""You give a stick to Karaoke.<BR>"")
		sayeveryone(""%me gives a stick to Karaoke.<BR>"")
		say(""You receive a key from Karaoke.<BR>You now have an old rusty key.<BR>"")
		sayeveryone(""Karaoke gives a key to %me.<BR>"")
		sql(""update tmp_itemtable set amount=amount-1 where id=56 and \\\\
		room = 0 and \\\\
		search = '' and \\\\
		belongsto = '%me' and \\\\
		wearing = '' and \\\\
		wielding = ''"")
		sql(""delete from tmp_itemtable where id=56 and \\\\
		amount = 0 and \\\\
		room = 0 and \\\\
		search = '' and \\\\
		belongsto = '%me' and \\\\
		wearing = '' and \\\\
		wielding = ''"")
		sql(""insert into tmp_itemtable values(39,'','%me', 1, 0,  '', '')"")
		show(""select contents from action where id=11"")
	end
end
return
" where id=56;

update methods
set src="if sql(""select 1 from tmp_usertable where name='%me' and gold*100+silver*10+copper>10"")
	say(""You pay Keanur and leave %02.<BR>"")
	sayeveryone(""You notice %me paying Keanur and leaving %02.<BR>"")
	if sql(""select '%02' = 'west'"")
		sql(""update tmp_usertable set room=89, copper=copper-10 where name='%me'"")
		set room=89
	else
		sql(""update tmp_usertable set room=45, copper=copper-10 where name='%me'"")
		set room=45
	end
	sql(""update tmp_usertable set silver=silver-1, copper=copper+10 where name='%me' and copper<0"")
	sql(""update tmp_usertable set gold=gold-1, silver=silver+10 where name='%me' and silver<0"")
	showstandard
else
	say(""You do not have enough money.<BR>"")
	showstandard
end
return
" where id=57;

update methods
set src="if sql(""select (%amount = 2 and '%02'='west') or (%amount = 1 and ('%01'='w' or '%01'='west'))"")
	say(""You try to go west, yet Keanur hinders you.<BR>"")
	say(""'I am sorry, this isn't a toll-free bridge, and while passing onto \\\\
	the bridge is free, leaving it costs 1 silver coin. Use <B>pay west</B>.', \\\\
	chuckles Keanur.<BR>"")
else
	say(""You try to go east, yet Keanur hinders you.<BR>"")
	say(""'I am sorry, this isn't a toll-free bridge, and while passing onto \\\\
	the bridge is free, leaving it costs 1 silver coin. Use <B>pay east</B>.', \\\\
	chuckles Keanur.<BR>"")
end
showstandard
return
" where id=58;

update methods
set src="if sql(""select 1 from tmp_usertable where name='Karcas' and room=16"")
	say(""You try to move behind the counter, but Karcas immediately intervenes.<BR>"")
	sayeveryone(""%me tries to move behind the counter, but Karcas immediately intervenes.<BR>"")
	showstandard
else
	if sql(""select 1 from items where id=-50 and adject2='open'"")
		say(""You go down the hatch.<BR>"")
		sayeveryone(""%me disappears behind the counter.<BR>"")
		sql(""update tmp_usertable set room=26 where name='%me'"")
		set room=26
		sayeveryone(""%me appears from the hatch.<BR>"")
		showstandard
	end
end
return
" where id=59;

update methods
set src="if sql(""select 1 from tmp_usertable where name='Karcas' and room=16"")
	say(""You try to open the hatch, but Karcas threateningly moves closer, and you decide to do something else.<BR>"")
	sayeveryone(""%me tries to move behind the counter, but Karcas moves threateningly closer and %me decides to hide in a corner instead.<BR>"")
	showstandard
else
	if sql(""select 1 from items where id=-50 and adject2='open'"")
		say(""The hatch appears to be already open.<BR>"")
		showstandard
	else
		say(""You open the hatch.<BR>"")
		sayeveryone(""%me disappears behind the counter. You hear some metalic noises and %me appears again.<BR>"")
		sql(""update items set adject2='open' where id=-50"")
		show(""select contents from action where id=7"")
	end
end
return
" where id=60;

update methods
set src="if sql(""select 1 from tmp_usertable where name='Karcas' and room=16"")
	say(""You try to close the hatch, but Karcas threateningly moves closer, and you decide to do something else.<BR>"")
	sayeveryone(""%me tries to move behind the counter, but Karcas moves threateningly closer and %me decides to hide in a corner instead.<BR>"")
	showstandard
else
	if sql(""select 1 from items where id=-50 and adject2='wooden'"")
		say(""The hatch appears to be already closed.<BR>"")
		showstandard
	else
		say(""You close the hatch.<BR>"")
		sayeveryone(""%me disappears behind the counter. You hear some metalic noises and %me appears again.<BR>"")
		sql(""update items set adject2='wooden' where id=-50"")
		showstandard
	end
end
return
" where id=61;

update methods
set src="if sql(""select 1 from items where id=-91 and adject1='open'"")
	say(""The chest is already open.<BR>"")
	showstandard
else
	if sql(""select 1 from tmp_itemtable where id=39 and belongsto='%me'"")
		say(""You open the chest.<BR>"")
		sayeveryone(""%me opens the chest.<BR>"")
		sql(""update items set description='<H1><IMG \\\\
			SRC=""http://www.karchan.org/images/gif/hidden04.gif"">The \\\\
			 Chest</H1><HR>On the floor on the far side of the hidden \\\\
			room above the main room of the Inn you see a solid iron chest. \\\\
			It happens to be very old and very sturdy. It\\\\'s lock is rusted \\\\
			and the lid is standing wide open. Everybody can look in the \\\\
			chest if they wanted to.<P>', adject1='open' where id=-91"")
		sql(""update rooms set contents='<H1><IMG \\\\
			SRC=""http://www.karchan.org/images/gif/hidden03.gif""> \\\\
			The Hidden Room</H1> <font size=+3>Y</font>ou are in the middle of \\\\
			a hidden room on the upper floor of the Inn. It is very dusty \\\\
			and all around sticky old cobwebbs have been made by the usual \\\\
			little insects. On the far side of the room a strong iron chest \\\\
			can be seen. It seems to be open. Above you you can see a lot \\\\
			of wooden stuff to hold up the roof. To the south there is a way \\\\
			out, down the staircase that is visible there.<P>' \\\\
			where id=20"")
		show(""select contents from action where id=12"")
	else
		say(""The chest appears to be locked. You do not have the key.<BR>"")
		sayeveryone(""%me tries to open the chest, but fails for lack of a key.<BR>
		showstandard
	end
end
return
" where id=62;

update methods
set src="if sql(""select 1 from items where id=-91 and adject1!='open'"")
	say(""The chest is already closed.<BR>"")
else
	say(""You close the chest. [<A HREF=""http://www.karchan.org/images/mpeg/hid2.mpg"">MPEG</A>]<BR>"")
	sayeveryone(""%me closes the chest.<BR>>"")
	sql(""update items set description='<H1>The Lock</H1><HR>\\\\
		You look carefully at the lock of the chest. It is very well made. \\\\
		Somebody at least knows his business. You can\\\\'t open it, that is \\\\
		for sure.  Why don\\\\'t you try finding the key? There is probably no \\\\
		other way to open this chest but with the key.<P>'\\\\
		, adject1='rusty'  where id=-91"")
	sql(""update rooms set contents='<H1><IMG \\\\
		SRC=""http://www.karchan.org/images/gif/hidden01.gif"">\\\\
		The Hidden Room</H1><font size=+3>Y</font>ou are in the middle of \\\\
		a hidden room on the upper floor of the Inn. It is very dusty and all \\\\
		around sticky old cobwebbs have been made by the usual little \\\\
		insects. On the far side of the room a strong iron chest can be seen. \\\\
		Above you you can see a lot of wooden stuff to hold up the roof. To \\\\
		the south there is a way out, down the staircase that is visible \\\\
		there.<P>' where id=20"")
end
showstandard
return
" where id=63;

END_OF_DATA