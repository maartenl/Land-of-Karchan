#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

replace into rooms
values(27,0,0,555,24,0,0,'<h1><img src="/images/gif/hallgate.gif">Before the
Gate</h1><img src="/images/gif/letters/y.gif" align=left>ou are presently in
a small room.  It is rather dismal and dreary.  The walls at one time have
been moist; they still show signs of being so.  There is a large wooden gate
to the north. Peering through the door, you can make out a staircase and
passages.  To the south, you can see a cross-section and if you look
carefully, some light from above appears to come filtering through.<p>
');
replace into items
values(-135,
'walls',
'natural',
'stone',
'shaped', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Walls</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he walls seem to have been formed by nature rather then by the hand of man.
It seems that many a time water was beating upon these walls, shaping them
over the course of much time. They still show the signs of having been moist
at one time. Set into the north wall is a large wooden gate.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-135, '','',1,27,'','',0);
replace into items
values(-136,
'gate',
'large', 
'wooden',
'strong',   
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Wooden Gate</H1>
<IMG SRC=""/images/gif/letters/b.gif"" ALIGN=left>
uilt into the wall is a large wooden gate. Peering through, you can
make out a staircase and passages. The gate does not prohibit you from 
entering, or it would have been locked.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-136, '','',1,27,'','',0);
replace into rooms
values(555,0,0,556,27,0,0,'<H1>Behind the Gate</H1>
<IMG SRC="/images/gif/letters/g.gif" ALIGN=left>ently you step behind the
gate. Apparently the walls seem to have a red glow about them, and the
temperature behind this gate seems to have risen a few degrees. Ahead of you
a stone corridor, shaped as if water has been passing along it for a long
time, is visible.<P>');
replace into items
values(-137,
'walls',
'red-glowing',
'stone',
'nature-shaped', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Walls</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he walls seem to have been formed by nature rather then by the hand of man.
It seems that many a time water was beating upon these walls, shaping them
over the course of much time. They still show the signs of having been moist
at one time. The walls seem to have a red glow to them, of warmth and heat.
Perhaps it is due to the rise in temperature you are feeling. You see no
other explanation.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-137, '','',1,555,'','',0);


replace into rooms
values(556,557,0,0,555,0,0,'<H1>The Way</H1>
<IMG SRC="/images/gif/letters/s.gif" ALIGN=left>o far you have been
fortunate. You are currently well behind the gate, the red glow against the
rocks on either side of you seems to have deepened and a deep rumble can be
heard further down the corridor. The corridor takes a sharp turn to the west
here.<P>');
replace into tmp_itemtable
values(-137, '','',1,556,'','',0);

replace into rooms
values(557,558,556,0,0,0,0,'<H1>The Porch</H1>
<IMG SRC="/images/gif/letters/t.gif" ALIGN=left>his seems to be a natural
pocket in the rocks. It forms a sort of hollow chamber. The red glow seems
to be appearing stronger from the way west where you see a larger hall. The
temperature has risen to quite a fenominal height and you are sweating a
little. You can feel a rumble all the way in your gut and it seems to be
coming from the large hall west.<P>');
replace into items
values(-138,
'hall',
'larger',
'west',
'stone', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The West Hall</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
o the west you notice an exit into a larger hall. It seems to be a large
hall, but it is very hard to see from here. In the distance you see large
pillars of stone, scattered throughout this hall.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-138, '','',1,557,'','',0);

replace into rooms
values(558,559,557,0,0,0,0,'<H1>The Firedragon in the Central Hall</H1>
<IMG SRC="/images/gif/letters/t.gif" ALIGN=left>he hall is much larger then
at first appears. The temperature is almost unbearable. Large stone pillars
that seemed to be created by accident by nature itself are spread all
around. The solid wall to the left and right of you seems to be part of
the hall. To the west you notice a wall that has been caved in, taking part
of the roof with it. A large body of water is visible in the hall, extending
from the caved in west wall.<P>

In front of this rocky caved in wall you see a rather large and vicious Fire
dragon, resting on the rocks. There
is almost a foot of water all around the hall. 
<P>In the corner of the room to your left, you notice what appears to be a,
 rather blackened, old skeleton. <P>
The more foolhardy of people might be able to venture more to the west, 
however the temperature there must be quite high, not regarding the huge
firedragon sitting there, ready to unleash huge bolts of fire when you get
too close.<P>');
replace into items
values(-139,
'skeleton',
'rather',
'blackened',
'old', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Rather Blackened Skeleton</H1>
<IMG SRC=""/images/gif/letters/i.gif"" ALIGN=left>
n the corner of the room to your left, you notice what appears to be a
rather blackened skeleton. Upon closer examination you notice that the 
skeleton belonged to a man, rather old, and some rags that seem to have
survived the firebreathing dragon somehow point in the direction of a  
wizard. What also seems to be in one piece is a small leather booklet that
is lying a few feet next to him. You wonder what he was doing here.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-139, '','',1,558,'','',0);
replace into items
values(97,
'booklet',
'small',
'leather',
'old', 
0,0,0,0,"",'',0 /*554*/,0,1,1,1,
0,"<H1>The Small Leather Booklet</H1>
<IMG SRC=""/images/gif/letters/y.gif"" ALIGN=left>
ou look at the small leather booklet. Apparently it used to be at one time
the diary or notebook of someone. It is rather old, and made of old
cowleather. You should be able to read it.<P>
"," <H1>The Small Leather Booklet</H1>
<IMG SRC=""/images/gif/letters/y.gif"" ALIGN=left>
ou open the leather booklet. Its yellow pages look old and worn. Apparently
the owner used it much. On the first page you can read the following
inscription:<P>
<DIV ALIGN=\"center\"><I>Property of:<BR>
Karranius, <BR>
Archmagus and Defeater of Dragons<P>
</I></DIV>
Apparently, he met his end fighting Dragons. The booklet seems to be a
summary/diary/log of some kind. However, you notice that it only contains a
few items. The beginning of the book seems to be too blurred to be able to 
read, but it gets better further on. You are able to read the following:<P>
<I><DL>
<DT>23 Jule, 1033
<DD>Was approached by the citizens of the Town of Pendulis to combat a Fire
Dragon that has been making a ruckus and being generally unpleasant. To be
more specific, it seems to have eaten quite a number of people and merchants
have started avoiding the city as much as possible. The Merchants Guild of
Pendulis is paying me handsomely to see this nuisance out of this world.<P>
They'd better, because a FireDragon is one of the more fierce dragons
available.<P>
<DT>10 Augustus, 1033
<DD>Found the dragon, it resides in the hills to the southwest of Pendulis.
Now to devise a cunning plan to lure it somewhere and dispose of it.<P>
<DT>25 Augustus, 1033
<DD>I got it! FireDragons are usually very vulnerable to large amounts of
ice cold water. I just need to find a place where there is water in
abundance. Then I'll use my magic to temporarily remove the water, and
create a high temperature. This will lure the dragon. I just need to find a
good spot.<P>
<DT>20 September, 1033
<DD>Found a good spot! There is a small village with a large body of water
underground. It has a well in the central square that should be big enough
for the dragon.<P>
<DT>30 Oktober, 1033
<DD>Have arrived at the town. Townsfolk look with kinda a suspicious eye at
me, but it doesn't phase me. Examined the site, it looks good. I must be
able to create a controlled earthquake, that will collaps the west wall and
block off all the water temporarily. Then I'll raise the temperature with my
magic and wait in the emptied corridor for the dragon. Once the dragon has
arrived, I can simply pierce the blocked west passage and all the water
should come back rushing in.
</DL>
</I>
Here ends the diary of this silly wizard. His methods seem onorthodox and
dangerous and that was probably what did him in in the end. You always have
to be carefull around dragons, because they usually will do something
different from what you expect.<P>
",0,0,0,0,4,0,0,0);
replace into tmp_itemtable
values(97, 'rags','',1,558,'','',0);
replace into items
values(-140,
'water',
'large',
'body',
'of', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Large Body of Water</H1>
<IMG SRC=""/images/gif/letters/a.gif"" ALIGN=left>
pparently there is a large body of water behind the caved in
wall, and small amounts are spilling across the rocks onto the floor. There
is almost a foot of water all around the hall. The water seems to be of a
comfortable temperature like a nice hot bath. The water makes it for the Fire
dragon hard to leave her nest.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-140, '','',1,558,'','',0);
replace into items
values(-141,
'pillars',
'large',
'stone',
'natural', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>Large Stone Pillars</H1>
<IMG SRC=""/images/gif/letters/s.gif"" ALIGN=left>
cattered throughout the hall are large stone pillars that seemed to be
created by accident by nature. They seem to be made of stalactites and
stalagmites growing into each other.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-141, '','',1,558,'','',0);
replace into items
values(-142,
'wall',
'west',
'cavedin',
'rocky', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Caved in West Wall</H1>
<IMG SRC=""/images/gif/letters/d.gif"" ALIGN=left>
ue west, you notice that there is no wall there. Instead, it
seems that a large part of the rock wall has caved in effectively blocking
that way off.<P>
It seems as if the Firedragon has created a nest out of
the very rocks of the west wall. However, the rocks seem to have a second
function. Apparently there is a large body of water behind the caved in
wall, and small amounts are spilling across the rocks onto the floor. The
water cannot reach the dragon due to the rocks of the wall surrounding the
said dragon. Almost a foot of water is spread throughout the hall.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-142, '','',1,558,'','',0);
replace into items
values(-143,
'firedragon',
'rather',
'large',
'vicious', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Ferocious Firedragon</H1>
<IMG SRC=""/images/gif/letters/i.gif"" ALIGN=left>
n front of this rocky caved in wall to the west you see a rather large and vicious Fire
dragon. It\'s eyes are closed to slits, tufts of smoke rise from it\'s nose 
and you seem to have located the source of both the rumbling you heard as   
well as the rise in temperature. It seems as if it has created a nest out of
the very rocks of the west wall. <P>You guess she is protective of her
offspring. She seems to have been here for quite some time and is in pretty
bad shape. She is suffering from malnourishment (what would a Fire dragon
feed on, you wonder, then you have an idea on that and take a step back) and
her fire, though still fiery, seems to have diminished. Quite possible by
the large body of water all around. She is issueing most of her fire and
strength into getting the water onto an agreeable temperature. If she did
not, the ice cold water would have finished her off by now.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-143, '','',1,558,'','',0);
replace into items
values(-144,
'rags',
'some',
'old',
'wizards', 
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Rags</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he skeleton must have been wearing clothes, which you can deduce from these
remnants. You examine the rags carefully. They seem to be
part of the robe of the deceased. The fabric used to be red (now it is a
dullish brown) covered in small stars.<P>
The rags you are looking at seem to contain a
pouch of somekind, sewn into it. You notice something rectangular inside,
straining against the fabric. It must be
possible to '<I>search</I>' the rags to retrieve whatever it is.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-144, '','',1,558,'','',0);

replace into rooms
values(559,0,558,0,0,0,0,'<H1>The West Wall</H1>
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou have arrived at the west
wall. This was a bad idea. The west wall has indeed caved in. Between the
cracks of the rocks, you notice very many small streams of water running
through. Apparently, the large body of water seems to be behind this wall.
Removing one or two of the rocks ought to be enough to get the water with
enough force to fill the well again and get rid of the firedragon at the
same time.<P>
The firedragon, however, is much too close for comfort and
she seems to be in a mean mood. This was indeed a bad idea. She decides 
to attack with most of her fire breathing ability and you might 
not last all that long here. Better hurry with what you wish to do.<P>');

replace into rooms
values(560,0,558,0,0,0,0,'<H1>The Flooding West Wall</H1>
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou have arrived at the west
wall. This was a bad idea. The west wall has indeed caved in. Between the
cracks of the rocks, you notice extremely large jets of powerful water
pushing through the cracks of the rocks, making increasingly wide gaping holes in the
wall and neigbouring walls as well. Cracks have appeared in the walls on
your left and right. Apparently, a large body of water seems 
to be behind this wall and is currently under enormous pressure.<P>
The water buildup is <I>dangerous</I> and <I>enormous</I>. You estimate that
you have about three minutes before the whole room as well as every room in
the well is flooded with water and <I>you will be drownded</I>.<P>');

replace into rooms
values(561,8,16,9,3,0,0,'<H1><IMG SRC="/images/gif/well.gif">The
Village</H1> 
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou are standing
in the middle of what seems to be the village square. In the middle of this
square is a well filled to the brim with clear water. Around the well, you
notice the village has a new look to it. The leaves of surrounding trees are
green and there is a pleasant odour in the air that makes all the tiredness
of journeying fall from your limbs.<P>
To the
north there is an old dirty tavern called "The Twisted Dwarf" where you
could get something to drink, if you had any money. In the east a small shop
can be seen which handles in about anything. In the west a tree is casting
his shadow over a small bench on which a very old dwarf is seated. In the
south there is a road that seems to lead nowhere.<P>');

replace into commands 
values(96, "go_west1", 1, "w", "go_west", '', 558);
replace into commands 
values(97, "go_west2", 1, "go west", "go_west", '', 558);
replace into commands 
values(98, "go_west3", 1, "west", "go_west", '', 558);

replace into tmp_usertable
(name, title, 
race, sex, age, length, width, 
complexion, eyes, face, hair, beard, arm, leg, room, experience,
fightable, god, strength, intelligence, dexterity, constitution, wisdom)
values('firedragon1','firedragon',
'dragon','female','ferocious fire','long','none',
'red','yellow-eyed','none','none','none','none','none',
559,5000,1, 3, 10,10,10,10,10);
replace into bottable
(name, title, race, sex, age, length, width, complexion, eyes, face,
hair, beard, arm, leg, room, experience,
fightable, god, strength, intelligence, dexterity, constitution, wisdom)
values('firedragon1','firedragon',
'dragon','female','ferocious fire','long','none',
'red','yellow-eyed','none','none','none','none','none',
559,5000,1, 3, 10,10,10,10,10);

replace into methods
values(89, "go_west", "if sql(""select 1 from tmp_itemtable where id=61 \\\\
and belongsto='%me' and amount=1 and room=0 and (wearing='righthand' or \\\\
wearing='lefthand') and wielding='' and containerid=0"")
	# go west okay
	sql(""update tmp_usertable set fightingwho='%me' where name='firedragon1'"")
	say(""Suddenly, the magic ice ring that you are wearing around your \\\\
hand lights up, and you feel a cool soothing sensation cross your \\\\
entire body. The heat suddenly no longer bothers you.<BR>"")
else
	# go west bad
	sayeveryone(""%me disappears to the west, straight in the direction \\\\
of the firedragon.<BR>"")
	set room=559
	sql(""update tmp_usertable set room=559, vitals=maxvital+10 where name='%me'"")
	sayeveryone(""%me enters from the east and is promptly fried by the firedragon.<BR>"")
	say(""You cannot resist the extreme heat from the firedragon and you die a fiery death.<BR>"")
	show(""select contents from action where id=19"")
end
return
");

replace into action
values(19,"<H1>The encounter with the Firedragon</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he firedragon immediately notices you, as you arrive from the east. You are
getting too close for its comfort and it has decided to take immediate,
drastic and rather lethal action.<P>
It swivels its head in your direction, and opens its mouth. Before you
manage to duck away, a huge and fiery path of flames eats its way through
the air towards you. Your clothes are burning and the heat and pain is
incredible. Luckily you are spared the pain of the last moments, as you lose
consciousness and crumple in a burning heap to the ground.<P>
");

replace into commands 
values(99, "use_pick_with_wall", 1, "use pick with rocks", "use_pick_with_wall", '',
559);

replace into methods
values(90, "use_pick_with_wall", "if sql(""select 1 from tmp_itemtable where id=2 \\\\
and belongsto='%me' and amount=1 and room=0 and wielding<>0 and wearing='' and containerid=0"")
	# use pick okay
	sql(""update commands set callable=0 where id in (96,97,98)"")
	sql(""update events set callable=1, minute = (minute(now())+3) % 60 where eventid = 18"")
	say(""You use your pick axe and large holes appear in the wall, \\\\
creating a willing conduet for the water.<BR>"")
	sayeveryone(""%me is using the pick axe %me has, to make bigger holes \\\\
in the west wall, and succeeding rapidly. Alreay large amounts of water \\\\
find an exit.<BR>"")
	if sql(""select 1 from attributes where name='quest1' and objectid='%me' and objecttype=1"")
		say(""You already completed this quest once, and are awarded no experience points.<BR>"")
	else
		say(""You gain 500 experience points!!!"")
		sql(""update tmp_usertable set \\\\
practises = if(experience % 1000>500, practises+3, practises),  \\\\
training = if(experience % 1000>500, training+1, training), \\\\
experience = experience + 500 where name='%me'"")
		sql(""insert into attributes values('quest1','finished','string','%me',1)"")
	end
	sql(""update tmp_usertable set room=560 where room=559 and name<>'firedragon1'"")
	sql(""update rooms set west=560 where id=558"")
	log(""%me solved QUEST1.<BR>"")
	show(""select contents from action where id=20"")
else
	# no pick found
	say(""You are not carrying a pick axe.<BR>"")
	showstandard
end
return
");

replace into action
values(20,"<H1>The Pick and the Rocks</H1>
<IMG SRC=""/images/gif/letters/e.gif"" ALIGN=left>
xpertly you wield your pick axe, ignoring the Firedragon as much as
possible. The pick pierces the wall and cuts several stones in half.
Immediately, you notice that the streams of water become more numerous.<P>
You continue unabated destroying the caved in wall. Where once there were
streams now there are powerfull jets of water being pushed with force.
Occasionally you lose your balance as the water attempts to push you away,
and soaking you to your very bones.<P>
This, you think should do it. When you look around, you notice that the
firedragon is missing and the nest is empty.<P>
");

replace into events
values(18, "flood_well", -1, -1, -1, -10, -1, 0, "flood_well", "", 0);

replace into methods
values(91, "flood_well", "
sql(""update events set callable=0 where eventid = 18"")
sql(""update commands set callable=1 where id in (96,97,98)"")
sql(""update tmp_usertable set vitals=maxvital+10 where \\\\
name<>'firedragon1' and room in (24, 27, 28, \\\\
555,556,557,558,559,560)"")
sql(""update rooms set north=561 where id=3"")
sql(""update rooms set east=561 where id=8"")
sql(""update rooms set south=561 where id=9"")
sql(""update rooms set west=561 where id=16"")
log(""Someone solved QUEST1 and now the Well is Flooded.<BR>"")
return
");

END_OF_DATA