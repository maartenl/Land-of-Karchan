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

replace into action
values(21, "<Center><H1>Diseases through the Ages</H1></Center>
You notice quite some information about the most nasty looking diseases
around along with a historical annotation on when they started to occur for
the first time and when they stopped, because people found a medicine or
magic that worked.<P>
One part in particular seems to catch your eye. A particular virulent
disease that seemed to be very contagious, causing a large number of
casualties. Fascinated you read on:
<P>
<H2>The Whiteblodge Plague</H2>
<TABLE ALIGN=top>
<TR ALIGN=top><TD><B>Parts of Body effected:</B></TD><TD>The Skin and the Lungs, the
lungs are the most to suffer. Breathing becomes a little more difficult and
lots of coughing takes place. Besides an itchiness and the appearance of
nasty white blodges, the skin is relatively unharmed.
<TR ALIGN=top><TD><B>Internal Characteristics:</B></TD><TD>There are a number of stages that one goes through.
They are briefly described here:
<UL><LI>Apparently sudden loss of balance, due to loss of lung capacity
<LI>coughing, in the last stages this is accompanied with blood
<LI>sudden bouts of fainting, due to loss of lung capacity
</UL>
</TD></TR>
<TR ALIGN=top><TD><B>External Characteristics:</B></TD><TD>From an external view, a
number of characteristics can be visible to the carefull observer. These
are: a distinct paleness, red rimmed, unfocused and feverish eyes, and pale
white blodges covering the skin of the entire body.
</TD></TR>
<TR ALIGN=top><TD><B>History:</B></TD><TD> from year 1342 until 1350 of the Second Age. It has been known to
reoccur at frequent periods in time, but never so great again as during the
previous period when the disease first was registered.<BR></TD></TR>
<TR ALIGN=top><TD><B>Result:</B></TD><TD>Ultimately the result of the disease is
death. It has a 100% death rate on non-treated people.</TD></TR>
<TR ALIGN=top	><TD><B>Cure:</B></TD><TD>No cure exists at the moment, though it is
rumoured that an old witch in the forest has a cure. Nobody has been able to
retrieve it from her, though.</TD></TR>
</TABLE>
");

replace into action
values(22, "<Center><H1>Dr. Ioxus Compenium of Herbs</H1></Center>
You notice quite some information about the most interesting of herbs. The
list is quite extensive, so quickly you leaf through the book towards the
Reference.<P>
<P>
<H2>Astragalus (<I>Astragalus membranaceous</I>)</H2>
<B>MEDICINAL: </B>Astragalus strengthens metabolism and
digestion, raises metabolism, aids in strengthening the immune system, and
is used in the healing of wounds and injuries. It is often
                   cooked with broths, rice, or beans for a boost to the
healing energies during those illnesses that prevent one from eating
normally.
<P>
<H2>Blessed Thistle (<I>Cnicus benedictus</I>)</H2>
<B>MEDICINAL:</B> Blessed Thistle is used to strengthen the
heart, and is useful in all remedies for lung, kidney, and liver problems.
It is also used as a brain food for stimulating the memory. It is
                   used in remedies for menopause and for menstrual
cramping. Often used by lactating women to stimulate blood flow to the
mammary glands and increases the flow of milk. <BR>

<B>GROWING:</B> Blessed Thistle is generally found along
roadsides and in wastelands. It is an annual, and reaches to 2 feet tall.
Most folks consider this a pesky weed, so cultivation is not
                   common. Try gathering some for yourself from the wild, if
you dare the stickers.<P>
<H2>Burdock (<I>Arctium lappa</I>)</H2>
<B>MEDICINAL:</B> Burdock Root is used to treat skin diseases,
boils, fevers, inflammations, hepatitis, swollen glands, some cancers, and
fluid retention. It is an excellent blood purifier. A tea
                   made of the leaves of Burdock is also used for
indigestion. Very useful for building the systems of young women. Helps
clear persistent teenage acne if taken for three to four weeks. Used   
                   with dandelion root for a very effective liver cleanser
and stimulator.<BR>
<B>RELIGIOUS:</B> Used to ward off all sorts of negativity,
making it invaluable for protective amulets and sachets. Add to potpourri in
the house.<P>
<H2>Hyssop (<I>Hysoppus officinalis</I>)</H2>
<B>MEDICINAL:</B> Hyssop is used in treating lung ailments. The
leaves have been applied to wounds to aid in healing. The tea is also used 
to soothe sore throats. It has been used to inhibit the
                   growth of the herpes simplex virus. 
<BR>
<B>                   RELIGIOUS:</B> Hyssop is used in purification baths and
rituals, and used to cleanse persons and objects.

<BR><B>                   GROWING:</B> Hyssop prefers dry conditions, tolerates most
soils, and full sun. It is a member of the mint family. It is a perennial
shrubby plant growing to 3 feet tall. <P>

<H2>Fenugreek (<I>Trigonella foenum-graecum</I>)</H2>
<B>
                   MEDICINAL:</B> Fenugreek is used to soften and expel mucous.
It has antiseptic properties and will kill infections in the lungs. Used   
with lemon and honey, it will help reduce a fever and will
                   soothe and nourish the body during illness. It has been
used to relax the uterus, and for this reason should not be taken by
pregnant women.<BR>

                   <B>RELIGIOUS:</B> Adding a few fenugreek seeds to the mop water
used to clean your household floors will bring money into the household.   
<BR>
                   <B>GROWING:</B> Fenugreek likes dry, moderately fertile soil in
a sunny location. It is an annual, and grows to 1 - 3 feet tall.
<P>
<H2>Marshmallow (<I>Althea officinalis</I>)</H2>
<B>
                   MEDICINAL:</B> Marshmallow aids in the expectoration of
difficult mucous and phlegm. It helps to relax and soothe the bronchial
tubes, making it valuable for all lung ailments. It is an
                   anti-irritant and anti-inflammatory for joints and the
digestive system. It is often used externally with cayenne to treat blood
poisoning, burns, and gangrene.<BR>

<B>                   GROWING:</B> Marshmallow needs marshes and swamps to grow. It
is a perennial growing to 4 feet tall. <P>

");

replace into claimed values(0, '0 - 399','Karn');

update rooms set contents ='<H1><IMG SRC="/images/gif/mielikki/house8.gif">The
Hut</H1> <IMG
SRC="/images/gif/letters/a.gif" ALIGN=left> slightly derilict, small
building is visible here. Something between a hut and a house, for lack of a
better description.<P>
You can either make a closer inspection of this building, surrounded by the
forest and the mountains or you can retreat back to the south.<P>
' where id=117;

replace into items
values(145, 'building','small','derilict','slightly',
0,0,0,0,'','',0,0,0,0,0,0,'<H1><IMG SRC="/images/gif/mielikki/house8.gif">
The Hut</H1> <IMG
SRC="/images/gif/letters/a.gif" ALIGN=left> slightly derilict, small
building is visible here. Something between a hut and a house, for lack of a
better description.<P>
The entire shack seems to be made of decaying grey wood, except for the
oldfashioned wooden door and the small rectangular glass windows. You might be
able to look inside or open the door.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(145, '','',1,117,'','',0);
replace into items
values(146, 'door','oldfashioned','wooden','old',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Door</H1> <IMG
SRC="/images/gif/letters/t.gif" ALIGN=left>he door seems to be closed. It is
made of a different wood than the walls of the shack and a brass doorknob is
visible which can probably be used to <I>open</I> the door.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(146, '','',1,117,'','',0);
replace into items
values(147, 'windows','small','rectangular','glass',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Windows</H1> <IMG
SRC="/images/gif/letters/w.gif" ALIGN=left>indows are visible on both sides
of the door. They are of the typical rectangular sort, with two small beams
through the middle probably for support. The glass pane of the windows seems
to be extremely dirty, and you can hardly make out what is inside, however,
you might be able to <I>look through</I> the windows.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(147, '','',1,117,'','',0);

replace into action
values(23, "<H1>Looking Through The Windows</H1>
<IMG
SRC=\"/images/gif/letters/i.gif\" ALIGN=left>nside you see a whole mess of
stuff. A very very old crone seems to be shuffeling this way and that
muttering to herself, although you are too far away to hear anything
inside.<P>
Suddenly she looks directly at you, startling you rather. Her intense gaze
upon you makes you draw back from the windows. In absolutely no time at all,
the door of the little shack opens and the old crone shows herself, yells
'Get Lost!', and slams the door of the shack shut.<P>
");

replace into action
values(24, "<H1>Opening the Door</H1>
<IMG
SRC=\"/images/gif/letters/y.gif\" ALIGN=left>ou attempt to open the door, by
grabbing the brass knop on the door and giving it a good tug. Alas, the door
seems to be closed and locked.<P>
However, there suddenly is some audible noise inside, and all of a sudden
the door is, basically, torn open rather quickly, and an old old crone looks
suspiciously at your face.<P>
Her intense gaze upon you makes you draw back a little.<P>
\"<I>And what do we think we are doing, hmm? Just going ahead and trying to see
if the door is open? And what if it was open? Hmmm? Fancy yourself inviting
yourself inside and looking about? Hmmm?</I>\"<P>
She pokes you thoroughly in the chest. You feel yourself blushing a nicely
deep crimson.<P>
\"<I>And what if I wasn't here? What if the door wasn't locked? Would you have
let yourself in? Hmm? Maybe look around a bit? Maybe take some stuff that
doesn't belong to you, but you would fancy that the old crone no longer
needs anyway? Hmmm?</I>\"<P>
She is squinting at you.<P>
\"<I>The next time I see you trying this thing again, I'm going to turn you into
a toad.</I>\", she mutters, \"<I>An ugly one!!!</I>\". At that last remark, she slams the
door shut again.<P>
Well, that was embarassing, that was.<P>
");

replace into action
values(25, "<H1>Knocking at the Door</H1>
<IMG
SRC=\"/images/gif/letters/y.gif\" ALIGN=left>ou have your doubts, but you grab
your courage by the scruff of its neck and give a gentle knocking at the
door.<P>
An old crone opens the door, looks at you suspiciously. You flash your most
ingratiating smile at her. She frowns but beckons you inside.<P>
Once you are inside, she immediately slams the door shut again.<P>
With a morose expression on her face she get some beaten up old crockery
from a shelf above her and dashes it all violently in the appropriate way
onto a small table. <P>
\"Tea?\", she shouts at you unexpectedly. Without even waiting on your answer
she continues with what she was doing.<P>
");

replace into commands values(802, 'lookwindow',1,'look through window%',
'lookwindow','',117);
replace into methods values(95, 'lookwindow',
"sayeveryone(""%me tries to look through the windows. Suddenly you see %me \\\\
drawing back.<BR>The door of the shack opens, an old and wrinkled crone \\\\
shows herself briefly, spits to %me '<I>Get Lost!</I>' and slams the door shut \\\\
again.<BR>"")
show(""select contents from action where id=23"")
return
");

replace into commands values(803, 'opendoor',1,'open door',
'opendoor','',117);
replace into methods values(96, 'opendoor',
"sayeveryone(""%me gives the brass knob on the door a good tug.<BR> \\\\
There suddenly is some audible noise inside, and all of a sudden \\\\
the door is, basically, torn open rather quickly, and an old old crone looks \\\\
out suspiciously.<BR> \\\\
She gazes intensely upon %me and %me draws back a little.<BR> \\\\
<B>Old crone says [to %me] :</B>\"<I>And what do we think we are doing, hmm? Just going ahead and trying to see \\\\
if the door is open? And what if it was open? Hmmm? Fancy yourself inviting \\\\
yourself inside and looking about? Hmmm?</I>\"<BR> \\\\
The old crone pokes %me thoroughly in the chest.<BR> \\\\
%me blushes a nice deep crimson.<BR> \\\\
<B>Old crone says [to %me] :</B>\"<I>And what if I wasn't here? What if the door wasn't locked? Would you have \\\\
let yourself in? Hmm? Maybe look around a bit? Maybe take some stuff that \\\\
doesn't belong to you, but you would fancy that the old crone no longer \\\\
needs anyway? Hmmm?</I>\"<BR> \\\\
She squints at %me.<BR> \\\\
<B>Old crone says [to %me] :</B>\"<I>The next time I see you trying this thing again, I'm going to turn you into \\\\
a toad... An ugly one!!!</I>\".<BR> \\\\
The old crone slams the door shut again.<BR>"")
show(""select contents from action where id=24"")
return
");

replace into commands values(804, 'knockdoor',1,'knock on door',
'knockdoor','',117);
replace into methods values(97, 'knockdoor',
"sayeveryone(""%me knocks gently on the door. An old crone opens the door \\\\
and after a couple of extremely suspicious looks from her and some \\\\
particularly sparkling smiles from %me, beckons %me inside.<BR> \\\\
The old crone immediately shuts the door.<BR>"") 
set room=562
sql(""update tmp_usertable set room=562 where name='%me'"")
sayeveryone(""%me enters the room.<BR>"")
show(""select contents from action where id=25"")
return
");

replace into rooms values(562, 0, 0, 0, 0, 0, 0, '<H1>The
Room</H1> 
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou are standing in the
living room as well as the only room of the residence of the old crone.<P>
A lot of mess adorns the walls and the tables and the floor all around you.
Everything from evil looking potions to books to strange implements of which
you do not know the use. It seems the old crone has forgotten about you.<P>
To the south is the door that you entered through that would enable you to
<I>leave</I>.<P>
');

replace into commands values(805, 'exitsouth',1,'leave',
'exitsouth','',562);
replace into methods values(98, 'exitsouth',
"sayeveryone(""%me bids goodbye to the old crone and leaves south.<BR>"") 
say(""You take your leave from the old crone, refuse gently another round \\\\
of tea and you exit through the door south.<BR>"")
set room=117
sql(""update tmp_usertable set room=117 where name='%me'"")
sayeveryone(""%me appears out of the rundown house.<BR>"")
showstandard
return
");

replace into tmp_usertable 
(name, title, race, sex, age, length, width, complexion, eyes, face, hair,
beard, arm, leg, room, god)
values("Korinase", "Old Crone", "human", "female", "very old", "small",
"thin", "none", "black-eyed", "none", "black-haired", "none", "none",
"none", 562, 2);

END_OF_DATA
