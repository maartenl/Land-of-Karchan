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
	if sql(""select %string <= 0"")
		say(""You have died of the plague.<BR>"")
		sayeveryone(""%me moans.<BR>You notice %me double up, fall \\\\
to the ground and see the light slowly fading from %me's eyes.<BR>"")
		sql(""update tmp_usertable set room=1, experience=experience - \\\\
(experience % 1000)/2 where name='%me'"")
		sql(""delete from tmp_attributes where name='plague' and \\\\
objectid='%me' and objecttype=1"")
		sql(""delete from tmp_attributes where name='look' and \\\\
objectid='%me' and objecttype=1"")
		set room=1
		sayeveryone(""%me appears from nowhere.<BR>"")
		show(""select contents from action where id=26"")
	end
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
			# temporarily deleted this, plague no longer contagious
			# remove comments when plague becomes active again.
			# sql(""insert into tmp_attributes values('plague','100','integer','%me',1)"")
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
values(-166, 'building','small','derilict','slightly',
0,0,0,0,'','',0,0,0,0,0,0,'<H1><IMG SRC="/images/gif/mielikki/house8.gif">
The Hut</H1> <IMG
SRC="/images/gif/letters/a.gif" ALIGN=left> slightly derilict, small
building is visible here. Something between a hut and a house, for lack of a
better description.<P>
The entire shack seems to be made of decaying grey wood, except for the
oldfashioned wooden door and the small rectangular glass windows. You might be
able to look inside or open the door.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-166, '','',1,117,'','',0);
replace into items
values(-167, 'door','oldfashioned','wooden','old',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Door</H1> <IMG
SRC="/images/gif/letters/t.gif" ALIGN=left>he door seems to be closed. It is
made of a different wood than the walls of the shack and a brass doorknob is
visible which can probably be used to <I>open</I> the door.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-167, '','',1,117,'','',0);
replace into items
values(-168, 'windows','small','rectangular','glass',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Windows</H1> <IMG
SRC="/images/gif/letters/w.gif" ALIGN=left>indows are visible on both sides
of the door. They are of the typical rectangular sort, with two small beams
through the middle probably for support. The glass pane of the windows seems
to be extremely dirty, and you can hardly make out what is inside, however,
you might be able to <I>look through</I> the windows.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-168, '','',1,117,'','',0);

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
A lot of mess adorns the wooden walls and the old low tables and the 
dirty floor all around you.
It seems the old crone has forgotten about you.<P>
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

replace into answers values("Korinase","whiteblodge plague","The Whiteblodge
Plague, now there is something to tell. Very contagious, that one, and as
far as I knows, I have the only cure in my posession.");
replace into answers values("Korinase","diseases","I have all sorts of
knowledge of diseases. On most of 'em from <I>Whiteblodge Plague</I> to the
<I>common cold</I> and back again.");
replace into answers values("Korinase","cure","I have some cures for the
plagues, however, I don't make cures. I only have receipes. You'll have to
take it up with Vimrilad. Give my regards to the old codger.");
replace into answers values("Korinase","vimrilad","Vimlirad? You don't know
Vimlirad? He's an old Wizard who creates potions on receipe for people. He
has a magic shop in the City of Pendulis. He's old, but he still knows his
stuff.");
replace into answers values("Korinase","i want cure","Sure, deary. Everyone
wants a cure for this and that. Always they come to me, as if I am not busy
enough. Well? What do <I>you</I> want a cure for?");
replace into answers values("Korinase","cure to whiteblodge plague","Ahhh,
now we're gettin' somewhere. A cure for the Whiteblodge Plague, it is you
want, is it? I suppose I could do that... Oh, I don't see why not, but
you'd better look for yourself. The receipe for the cure is in here
somewhere.<BR>Korinase points at the random mess all around.");
replace into answers values("Korinase","hello","Hmmm?");
replace into answers values("Korinase","who are you", "I am Korinase. I live
here in this house, all by myself, and dedicate myself to the healing powers
of plants and fungi found in nature\'s rich surroundings.");

/* tables, walls and floor */
replace into items
values(-169, 'tables','old','low','wooden',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Tables</H1> <IMG
SRC="/images/gif/letters/d.gif" ALIGN=left>ifferent tables are scattered
throughout the room. Each and every one of which is packed with all sorts of
stuff.<P>
Upon closer examination, the <I>stuff</I> referred to seems to be evil
looking potions, old and dusty books, as well as different items that
facilitate the creation of potions as well. The old woman seems to be a
regular medicine woman. One of the older books seems to catch your
attention. It is a leather-bound large heavy old book.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-169, '','',1,562,'','',0);
replace into items
values(-170, 'walls','wooden','simple','wooden',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Walls</H1> <IMG
SRC="/images/gif/letters/h.gif" ALIGN=left>anging on the walls are different
utensils for creating potions. Besides that the walls look quite common.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-170, '','',1,562,'','',0);

replace into items
values(-171, 'floor','dirty','wooden','old',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Floor</H1> <IMG
SRC="/images/gif/letters/a.gif" ALIGN=left>pparently the different tables do
not provide enough surface for everything, and a lot of evil looking
potions, old and dusty books, as well as different items that facilitate the
creation of potions are scattered across said floor.<P>
One of the older books seems to catch your
attention. It is a leather-bound large heavy old book.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-171, '','',1,562,'','',0);

replace into items
values(-172, 'book','leather-bound','large','heavy',
0,0,0,0,'','',0,0,0,0,0,0,'<H1>The Leather-bound Book</H1> <IMG
SRC="/images/gif/letters/y.gif" ALIGN=left>ou gently pick up the book,
looking askance at Korinase to see if that is all righty. She doesn\'t
appear to pay attention though. It is a leather-bound large heavy old book.
In large letters on the cover of the book it says "<I>Diseases and
Cures</I>".<P>
You should be able to read it.<P>
','', 0,0,0,0,0,0,0,0);
replace into tmp_itemtable values(-172, '','',1,562,'','',0);

replace into items
values(153, 'recipe','paper','medicine','old',
0,0,0,0,'','',0,0,1,1,1,0,'<H1>The Medicine Recipe</H1> <IMG
SRC="/images/gif/letters/y.gif" ALIGN=left>es! This appears to be just what
you need. It contains detailed instructions on how to create a potion that
is said to cure the contagious Whiteblodge Plague. All you need to do is get
this thing to a responsible Wizard that knows a thing or two and get him or
her to create the potion for you.<P>
Too bad you cannot make it yourself. 
You should be able to read it.<P>
',' <H1>Reading The Medicine Recipe</H1> <IMG
SRC="/images/gif/letters/y.gif" ALIGN=left>ou make a valid attempt to read
the instructions on the paper in order to understand how to make the potion.
You cannot make out what the language is, probably Latin and ofcourse this
sort of complicates matters. The only thing you are able to read is a list
of ingredients required:<P>
<I><UL>
<LI>4 leaves Astragalus membranaceous
<LI>7 leaves of Blessed Thistle
<LI>2 leaves of Burdock
<LI>3 leaves of Hyssop
<LI>7 Fenugreek seeds
<LI>some Marshmallow
<li>water
<LI>vinegar
<LI>a little lemon juice (in order to stomach the potion)
</UL></I>
Besides that the recipe has no knowledge for your eyes.
You cannot make it yourself. <P>', 0,0,0,0,1,0,0,0);

replace into items
values(148, 'leaves','blessed','thistle','herbal',
0,0,0,0,'','',0,0,1,1,1,0,'<H1>The Blessed Thistle Leaves</H1> <IMG
SRC="/images/gif/letters/t.gif" ALIGN=left>his seems to be what appears to
be a pesky weed at first. Then you notice that it seems to be made of small
dark-green leaves. A strong smell is emmanating from it, that temporarily
refreshes you.<P>
','', 0,0,0,0,1,0,0,0);
replace into respawningitemtable values(148, 'road', '', 1, 3, '', '', 0);
replace into respawningitemtable values(148, 'road', '', 1, 32, '', '', 0);
replace into respawningitemtable values(148, 'road', '', 1, 35, '', '', 0);
replace into tmp_itemtable values(148, 'road', '', 1, 3, '', '', 0);
replace into tmp_itemtable values(148, 'road', '', 1, 32, '', '', 0);
replace into tmp_itemtable values(148, 'road', '', 1, 35, '', '', 0);
replace into items
values(150, 'leaves','herbal','marshmallow','some',
0,0,0,0,'','',0,0,1,1,1,0,'<H1>The Marshmallow Leaves</H1> <IMG
SRC="/images/gif/letters/t.gif" ALIGN=left>hese leaves seem to be a bit
hairy, have a dark-green hue and are brownish at the rim of the leaves. Some
ugly blotches, like rust almost, seem to be gathered around them.<P>
','', 0,0,0,0,1,0,0,0);
replace into respawningitemtable values(150, 'shrubbery', '', 1, 565, '', '', 0);
replace into tmp_itemtable values(150, 'shrubbery', '', 1, 565, '', '', 0);
replace into items
values(151, 'potion','red','herbal','whiteblodge-plague',
0,0,0,0,'','',0,0,1,1,1,0,'<H1>The Red Herbal Potion</H1> <IMG
SRC="/images/gif/letters/y.gif" ALIGN=left>ou are holding a small bottle in
your hand containing a darkish-red yet transparant potion. On the small
bottle there is a label saying <I>Cure to Whiteblodge Plague</I> written
in black ink in an unsteady hand.<P>
','', 0,0,0,0,1,0,0,0);

replace into commands values(806, 'readspecialbook',1,'read % book',
'readspecialbook','',562);
replace into methods values(99, 'readspecialbook',
"sayeveryone(""%me attempts to read an old book called &quot; \\\\
<I>Diseases and Cures</I>&quot;.<BR>"") 
say(""You open the book &quot;<I>Diseases and Cures</I>&quot;."")
if sql(""select 1 from tmp_itemtable where id=153 and belongsto='%me'"")
	say(""It doesn't seem to contain anything interesting and you toss it \\\\
down on the table again.<BR>"")
else
	say(""Instead of starting to read you notice that there is a loose page \\\\
in the book and you take it out. It appears to be a recipe for a cure \\\\
against the Whiteblodge Plague.<BR>"")
	sql(""insert into tmp_itemtable values(153, '', '%me', 1, 0, '', '', 0)"")
end
showstandard
return
");

replace into methclaimed values(6,'300-399','Karn');

replace into commands values(807, 'makerecipe',1,'give %recipe to vimrilad',
'makerecipe','',228);
replace into methods values(300, 'makerecipe',
"# check if person has recipe
if sql(""select 1 from tmp_itemtable where belongsto='%me' and id=153"")
	sayeveryone(""%me gives Vimrilad an old medicine recipe.<BR>"")
	say(""You give Vimrilad an old medicine recipe.<BR>"")
	# check if person has enough money (1 gold coin outta do it)
	if sql(""select 1 from tmp_usertable where name='%me' and \\\\
gold*100+silver*10+copper >= 100"")
		# check if wizard has all necessary herbs
		if sql(""select 1 from tmp_itemtable where id = 148 and belongsto='Vimrilad'"")
			if sql(""select 1 from tmp_itemtable where id = 150 and belongsto='Vimrilad'"")
				# checks okay-> remove herbs from wizard inventory, display action, 
				sql(""update tmp_itemtable set amount=amount-1 where belongsto='Vimrilad' and id in (148, 150)"")
				sql(""delete from tmp_itemtable where belongsto='Vimrilad' and id in (148, 150) and amount = 0"")
				# add potion to inventory
				if sql(""select 1 from tmp_itemtable where id=151 and belongsto='%me'"")
					sql(""update tmp_itemtable set amount=amount+1 where id=151 and belongsto='%me'"")
				else
					sql(""insert into tmp_itemtable (id, search, belongsto, amount, wearing, wielding) \\\\
values(151, '', '%me', 1, '', '')"")
				end
				sql(""update tmp_usertable set gold=gold-1 where name='%me'"")
				sql(""update tmp_usertable set silver=silver+10*gold, gold=0 where name='%me' and gold<0"")
				sql(""update tmp_usertable set copper=copper+10*silver, silver=0 where name='%me' and silver<0"")
				sayeveryone(""%me receives a red, herbal potion from Vimrilad.<BR>"")
				say(""You receive a red, herbal potion from Vimrilad.<BR>"")
				showstandard
			else
				sayeveryone(""Vimrilad says [to %me]: I'm sorry, but I am all out \\\\
of Marshmallow leaves. And I have no idea where to get them. I'm \\\\
sorry.<BR>He returns the recipe to %me.<BR>"")
				say(""Vimrilad says [to you]: I'm sorry, but I am all out \\\\
of Marshmallow leaves. And I have no idea where to get them. I'm \\\\
sorry.<BR>He returns the recipe to you.<BR>"")
				showstandard
			end
		else
			sayeveryone(""Vimrilad says [to %me]: I'm sorry, but I am all out \\\\
of Blessed Thistle leaves. And I have no idea where to get them. I'm \\\\
sorry.<BR>He returns the recipe to %me.<BR>"")
			say(""Vimrilad says [to you]: I'm sorry, but I am all out \\\\
of Blessed Thistle leaves. And I have no idea where to get them. I'm \\\\
sorry.<BR>He returns the recipe to you.<BR>"")
			showstandard
		end
	else
		sayeveryone(""Vimrilad says [to %me]: I'm sorry, but my fee for \\\\
potion creation is 100 copper coins.<BR>Vimlirad gives %me the \\\\
recipe.<BR>"")
		say(""Vimrilad says [to you]: I'm sorry, but my fee for \\\\
potion creation is 100 copper coins.<BR>Vimlirad returns the recipe.<BR>"")
		showstandard
	end
end
return
");

update rooms set south=563, contents='<H1><IMG
SRC="/images/jpeg/forest2.jpg">The
Forest</H1> <!113> <IMG
SRC="/images/gif/letters/y.gif" ALIGN=left>ou are in
the middle of a huge forest. To the west and south your way is blocked by a
huge ring of mountains, surrounding the forest. To the south you see a small
passage leading into a secluded area of the forest. Every other way leads deeper
into the forest.<P>
Upon further inspection, you notice that the ground to the south appears to
become much much more moist.<P>
' where id=113;
replace into rooms values(563, 0, 0, 113, 564, 0, 0, '<H1>The Beginnings of a
Swamp</H1> 
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou are still standing in the
forest. However, due to the south the ground seems to become more moist and
moist. The number of insects, buzzing around your head seems to have
increased, and the flora around you seems to be of the creeping low kind and
the stunted bald trees surrounding you do not provide much in the way of
entertainment either.<P>
The ground seems to be a bit moist and your footing is less certain than it
once was.<P>
');
replace into rooms values(564, 0, 0, 563, 565, 0, 0, '<H1>The Swamp</H1> 
<IMG SRC="/images/gif/letters/y.gif" ALIGN=left>ou are no longer standing in
a forest, but rather in a swamp. Left and right you see large puddles of
weed-covered stagnant water. Marsh grass seems to be the most prolific here.
The buzzing of the insects is slowly driving you a little crazy and the
sickly smell of decay is nauseating. To the south you are able to penetrate
deeper into the swamps.<P>
You are currently on the only thing that seems to provide some support in
this swamp. It seems to be a rather small strip of solid ground that can
support your weight. As far as you can tell it leads both north and south.
<P>
');
replace into rooms values(565, 0, 0, 564, 0, 0, 0, '<H1>The Swamp</H1> 
<IMG SRC="/images/gif/letters/t.gif" ALIGN=left>he swamp seems to end here
right against the very mountains. Surrounding you are still puddles of
water, and some small stunted trees not worth mentioning.<P>
Next to your feet you notice a dark-green shrubbery that seems to fit well
into the surroundings.
<P>
');

replace into commands values(808, 'drinkplaguepotion',1,'drink %potion',
'drinkplaguepotion','',0);
replace into methods values(301, 'drinkplaguepotion',
"# check if person has potion
if sql(""select 1 from tmp_itemtable where belongsto='%me' and id=151"")
	sql(""update tmp_itemtable set amount=amount-1 where id=151 and belongsto='%me'"")
	sql(""delete from tmp_itemtable where amount=0 and id=151 and belongsto='%me'"")
	sql(""delete from tmp_attributes where (name='plague' or name='look') and objectid='%me'"")
	sql(""replace into tmp_attributes values('cure', 'true', 'string','%me',1)"")
	sql(""update tmp_usertable set vitals=0 where name='%me'"")
	sayeveryone(""%me drinks the red potion, and feels healthy again.<BR>"")
	say(""You drink the red potion and you feel healthy again.<BR>"")
	showstandard
end
return
");

replace into action values(26,"<H1>The Emd</H1>
<IMG SRC=""/images/gif/letters/y.gif"" ALIGN=left>ou feel your body tumble
slowly to the ground, unable to fight any more against the Whiteblodge
Plague. You feel the darkness creep up on you. Slowly the world fades out of
existance.<P>
You are dead. You are at the moment in a dark room. You can't see anything.
You hear sighs.<P>
A voice says (behind you): Oh no, not another one...<P>
Another voice sighs : They just keep on comin'.<P>
Suddenly, in the distance, you see a light which slowly comes closer. As it
comes closer, you see that it is a lantern which is being held by somebody
(or something you can't really make it out). That someone is getting closer.
When he is standing right before you, you have a good view of who he is. You
can't see his face, because that is hidden by a cap. He is all in black.<P>
You say: Hey, did somebody just die?<P> 
He is carrying a heavy axe with him.
This, you gather, Must be Mr. Death himself. He swings back his axe, and you
hide your hands behind your face. (That shouldn't however help much) Than
the axe comes crushing down.<P>
(Type <B>look around</B>)<P>");



END_OF_DATA
