update methods
set src = "say(\"You yawn.<BR>\")
sayeveryone(\"%me yawns.<BR>\")
showstandard
return
" where id = 2;

update methods
set src = "debug
if sql(""select 1 from rooms where id=20 and south=0"")
	say(""You succeed in opening the door of the cupboard. \\
		 [<A HREF=""http://www.karchan.org/images/mpeg/her.mpg"">\\
		MPEG</A>]<BR>"")
	sayeveryone(""%me  opens the door of the cupboard.<BR>"")
	sql(""update items set description='<H1><IMG SRC=""http://www.karchan.org/images/gif/herberg5.gif"">The Cupboard</H1><HR>\\
		You look at the cupboard. It is very old and wormeaten. With one\\
		knock you could probably knock it down, but I doubt if the barman would appreciate \\
		this much. It is open. Both doors of the cupboard are ajar. In it you can \\
		see, amazingly, a staircase leading to the north and up into a hidden\\
		room.<P>', adject1='comparatively',\\
		adject2='big', name='cupboard' where id=-32"")
	sql(""update rooms set contents='<IMG SRC=""http://www.karchan.org/images/gif/herberg4.gif"" ALIGN=CENTER> \\
		<H1>The Taverne &quot;The Twisted Dwarf&quot;</H1>\\
		<IMG SRC=""http://www.karchan.org/images/gif/letters/y.gif"" ALIGN=left>\\
		ou are now in the Inn &quot;The Twisted Dwarf&quot; . It is \\
		dark, as always in these places. The windows are of a dark blue color, which \\
		doesn\\'t allow any light to enter the room. A lot of woodwork, wooden tables, \\
		chairs and a rather large bar on the right of you is situated almost against the \\
		the back of the room a comparatively big cupboard is visible. The cupboard \\
		appears to be open. Behind the bar a norse small ugly dwarf is cleaning some \\
		glasses at the bar. On the same bar you see a sign on a piece of wood, \\
		apparently this is the menu for the day.<BR> Scattered among the tables are \\
		groups of people, playing what seems to be a dwarfish version of Poker.You\\
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
