#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

update items set room=40 where id<=-68 and id>=-78;

replace into tmp_itemtable
select id, '', '',1,room,'','',0
from items 
where id<0 
and room<>0;

update items
set description='<H1><IMG
SRC="/images/gif/bib3.gif">
The Books</H1>
You look carefully at the bookcase. You can make out the following
titles.<P>
<UL>
<LI>Map to the Castle
<LI>A Century of Studying the Southern Oracle - by Professor Engywoeck
<LI>The Red Book of Westernesse - by B. Baggins
<LI>Some notes on Elvish Languages - by B. Baggins
<LI>Beowulf - writer unknown
<LI>War &amp; Peace
<LI>Crime &amp; Punishment
<LI>The Book of Spells - by H. M. Wordsworth
<LI>Secrets
<LI>Diseases through the Ages (Diseases)
<LI>Dr. Ioxus Compenium of Herbs (Herblore)
</UL>
<P>
You should be able to <B>read</B> the books..<P>
'
where id=-69;

update items
set description='<H1><IMG
SRC="/images/gif/bib3.gif">
The Books</H1>
You look carefully at the bookcase. You can make out the following
titles.<P>
<UL>
<LI>Map to the Castle
<LI>A Century of Studying the Southern Oracle - by Professor Engywoeck
<LI>The Red Book of Westernesse - by B. Baggins
<LI>Some notes on Elvish Languages - by B. Baggins
<LI>Beowulf - writer unknown
<LI>War &amp; Peace
<LI>Crime &amp; Punishment
<LI>The Book of Spells - by H. M. Wordsworth
<LI>Secrets
<LI>Diseases through the Ages (Diseases)
<LI>Dr. Ioxus Compenium of Herbs (herblore)
</UL>
<P>
You should be able to <B>read</B> the books..<P>
'
where id=-70;

update claimed 
set deity='Karn'
where id=4;

replace into rooms
values(550,0,0,0,40,0,551,'<H1>Between the Walls</H1>
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou are currently between the
walls of the library. It is a very narrow corridor you are in, with just
enough room for one man, or woman as the case may be. 
Apparently the castle has double walls for extra
protection and although this was clearly not intented for normal use, some
people apparently found it interesting enough to create a secret passage to.
However, it can\'t have been used much, judging from all the dust on the
floor.
<P>Behind you the walls become too narrow. In front of you a rather steep
staircase, apparently made of very rough wooden beams, leeds down at a
rather precarious angle. In the wall next to you, you notice the outline of a
door leading south.<P>');
replace into items
values(-126,
'walls',
'narrow',
'double',
'stone',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Double Stone Walls</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he walls one both sides of you seem to be part of the castle. They are very
thick and strong but don\'t leave much room for you to move around.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-126, '','',1,550,'','',0);
replace into items
values(-127,
'dust',
'old',
'floor',
'on',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Dust</H1>
<IMG SRC=""/images/gif/letters/d.gif"" ALIGN=left>
ust lies thickly on the floor. Apparently these passages have not been used
much. You doubt that anyone still knows that they are there.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-127, '','',1,550,'','',0);
replace into items
values(-128,
'staircase',
'old',
'wooden',
'steep',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Wooden Staircase</H1>
<IMG SRC=""/images/gif/letters/a.gif"" ALIGN=left>
 wooden staircase is here. It is constructed quite haphazardly, simply by
ankoring rather large wooden beams into both sides of the walls. These beams
seem to be made of the same material as common roofs. The staircase is quite
steep, due to the rather large wooden beams as well as the space in which
they are separated from each other. Tread carefully is the word here.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-128, '','',1,550,'','',0);

replace into rooms
values(551,0,0,0,0,550,552,'<H1>Between the Walls</H1>
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou stand between the
walls of the castle. It is a very narrow corridor you are in, with just
enough room for one man, or woman as the case may be. 
Apparently the castle has double walls for extra
protection and although this was clearly not intented for normal use, some
people apparently found it interesting enough to create a secret passage to.
However, it can\'t have been used much, judging from all the dust on the
floor.
<P>You are currently standing on a very steep staircase made of wooden
beams leading both up and down. Up, you estimate, would get you behind the
Castle\'s library, and down would proceed towards the Castle\'s basement.
However, you seem to remember that Castles do not have basements, or, if
they do, they are called differently.<P>');
replace into tmp_itemtable
values(-126, '','',1,551,'','',0);
replace into tmp_itemtable
values(-127, '','',1,551,'','',0);
replace into tmp_itemtable
values(-128, '','',1,551,'','',0);

replace into rooms
values(552,553,0,0,0,551,0,'<H1>Between the Walls</H1>
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou stand between the
walls of the castle. It is a very narrow corridor you are in, with just
enough room for one man, or woman as the case may be. In front of you, you
notice a small hole in the wall. You might be able to <I>look through</I>
it.
<P>The only way out seems to be towards a very steep staircase made of wooden
beams leading up. To the west the darkness makes it impossible to see if
an exit is there.<P>');
replace into items
values(-129,
'hole',
'small',
'wall',
'front',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Hole in the Wall</H1>
<IMG SRC=""/images/gif/letters/y.gif"" ALIGN=left>
ou look at the hole. It seems to have been put there on purpose. Carefully
you apply your eye to the hole, and you are able to see the room on the
other side of the wall. The view is not very clear, other then that it
appears to be the dungeon chamber.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-129, '','',1,552,'','',0);
replace into tmp_itemtable
values(-126, '','',1,552,'','',0);
replace into tmp_itemtable
values(-127, '','',1,552,'','',0);
replace into tmp_itemtable
values(-128, '','',1,552,'','',0);

replace into rooms
values(553,554,552,0,0,0,0,'<H1>A Small Corridor</H1>
<IMG SRC="/images/gif/letters/d.gif" ALIGN=left>arkness seems to surround
you. The only light seems to be coming from the east where is staircase is
visible and from the west where magical light emitting balls are visible.
That way seems to come out into a small room.
<P>');
replace into tmp_itemtable
values(-128, '','',1,553,'','',0);
replace into items
values(-130,
'balls',
'magical',
'light',
'emitting',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>Magical Light Emitting Balls</H1>
<IMG SRC=""/images/gif/letters/l.gif"" ALIGN=left>
ight flows from these, obviously magical, balls. You do not detect any scent
of oil used. However, you find them quite convenient as they spread enough
light for you to look where you are going. Obviously they seem to be the
next big thing after torches.<P>
These light balls seem to have an engraving printed upon them. It looks like
an ice crystal.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-130, '','',1,553,'','',0);

replace into rooms
values(554,0,553,0,0,0,0,'<H1>The Inner Chamber</H1>
<IMG SRC="/images/gif/letters/t.gif" ALIGN=left>his chamber is different 
from the ones you have encountered so far. The walls seem to have a clear
cold blue look. Dust covers the floor thickly. In the centre of the room is a
depression in the floor, a shallow bowl seems to have been hollowed out
there. Scattered throughout the hole are pieces of wood. 
Four magical light balls are fixed against the walls, dispersed evenly
throughout the room.<P>
The only way out seems to be the same way you came in.
<P>');
replace into tmp_itemtable
values(-130, '','',1,554,'','',0);
replace into items
values(-131,
'dust',
'thick',
'old',
'floor',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Dust</H1>
<IMG SRC=""/images/gif/letters/d.gif"" ALIGN=left>
ust covers the floor thickly, as previous described. In the centre of the
room, in the shallow bowl, the dust seems to cover some pieces of wood.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-131, '','',1,554,'','',0);
replace into items
values(-132,
'walls',
'sickly',
'yellow',
'old',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Clear Blue Walls</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he walls have the colour Blue. It is a very light blue that seems to make
the chamber a little cooler then the corridor outside. They are totally
uninteresting except for the fact that here, the walls seem to be markedly
different from the ones outside in the corridor where they are just common
castle stone walls.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-132, '','',1,554,'','',0);
replace into items
values(-133,
'bowl',
'hollow',
'shallow',
'old',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Bowl</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he bowl seems to be just a depression in the floor. However, it seems to be
purposefully man-made. It seems to form an exact duplicate of the design of
the chamber itself. In the middle of the bowl you notice several pieces of
wood, covered by a thick layer of dust. It seems as the wooden pieces were
part of something that was standing in the middle of the chamber here.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-133, '','',1,554,'','',0);
replace into items
values(-134,
'wood',
'pieces',
'of',
'dust-covered',
0,0,0,0,"",'',0 /*554*/,0,0,0,0,
0,"<H1>The Pieces of Wood</H1>
<IMG SRC=""/images/gif/letters/i.gif"" ALIGN=left>
n the middle of the room, in the bowlshaped depression in the floor, you
carefully examine the dust-covered pieces of wood. It seems that the pieces
of wood, though old and difficult to see among the dust, were part of a
<I>standard</I>. Apparently there used to be a standard of wood in the
middle of this room, right in the centre of the bowlshaped hollow. Probably
there was some important artifact on top of this wooden standard, but the
wooden standard has long since deteriorated into these ancient pieces of wood.
You theorize that, if the wooden standard fell down, the important artifact
it was meant to display can be found either between these same wooden pieces
or under the dust in the room.<P>
",'',0,0,0,0,0,0,0,0);
replace into tmp_itemtable
values(-134, '','',1,554,'','',0);

replace into action
values(18,"<H1>A Secret Entrance</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he book won't come out of the bookcase. You become impatient and give it a
good yank. You hear a click.<P>
Suddenly the entire bookcase swings forwards revealing a dark and narrow
passageway. Tentatively you venture inside. You jump up from 
the soft thud behind you as the bookcase settles back into its more familiar
position.<P>");

delete from tmp_itemtable where id=-160;
delete from itemtable where id=-160;
delete from tmp_itemtable where room in (1965, 1451, 1300, 1213, 1207, 1203,
1202, 1200 ,688 ,687, 684, 659);
delete from itemtable where room in (1965, 1451, 1300, 1213, 1207, 1203,
1202, 1200 ,688 ,687, 684, 659);

replace into items
values(-109,
'stand',
'small',
'market',
'wooden',
0,0,0,0,"<H1>A Market Stand</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he stand in front of you seems to be relatively stable. On it various items
are visible. Right now it seems that you are not very interested in anything
that is lying there. Perhaps later.<P>
The salesman gives you a shifty eye. You see him estimating you as to what
you are, buyer,looker or thief.<P>",'',0 /*554*/,0,0,0,0,
0,'','',0,0,0,0,0,0,0,0);

replace into items
values(61,
'ring',
'magical',
'ice',
'strong',
0,0,0,0,"",'',0 /*554*/,0,1,1,1,
0,"<H1>A Magical Ice Ring</H1>
<IMG SRC=""/images/gif/letters/t.gif"" ALIGN=left>
he ring is very cold to the touch. Almost colder than ice, yet it doesn't
seem to affect you. It obviously is a rather magical item and you are not
certain of it's purpose. Could be dangerous, you know.<P>
Upon closer inspection you notice that on the outer rim there can be
discerned the symbol of a white star, or maybe not. It seems to be a white
star made up of little white stars, and the longer you look at it, the more
of a headdache you seem to develop.<P>",'',3,3,0,0,1,0,0,0);

replace into respawningitemtable
values(61, 'dust','',1,554,'','',0);

replace into commands 
values(95, "read_books", 1, "read %", "read_books", '', 40);

replace into methods
values(88, "read_books", "if sql(""select '%*' = 'read secrets'"")
	# reading secrets
	sayeveryone(""%me tries to pull a book from the bookcase. Somehow, \\\\
 what happens next is that the entire bookcase swings outwards and you \\\\
 notice %me disappearing into a dark corridor that previously was not \\\\
there.<BR>The bookcase swings shut again.<BR>"")
	set room=550
	sql(""update tmp_usertable set room=550 where name='%me'"")
	sayeveryone(""%me enters from the hidden passage in the library.<BR>"")
	show(""select contents from action where id=18"")
end
if sql(""select '%*' = 'read diseases'"")
	# reading diseases
	sayeveryone(""%me pulls a book called '<I>Diseases through the \\\\
Ages</I>' from the bookcase and begins to read.<BR>"")
	show(""select contents from action where id=21"")
end
if sql(""select '%*' = 'read herblore'"")
	# reading herblore
	sayeveryone(""%me pulls a book called '<I>Dr. Ioxus Compenium of Herbs</I>' from the bookcase and begins to read.<BR>"")
	show(""select contents from action where id=22"")
else
   # reading something else.
	say(""You cannot read that book from the library.<BR>"")
	showstandard
end
return
");   

END_OF_DATA