#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# update logonmessage
#

UPDATE logonmessage set message="<B>Logonmessage</B>
<BR>
<IMG SRC=\"http://www.karchan.org/images/gif/letters/w.gif\"
ALT=\"W\" ALIGN=left>
elcome to the Land of Karchan MUD, a land filled with mystery 
and enchantment, where weapons, magic, intelligence, and common 
sense play key roles in the realm. Where love and war can be one 
and the same. Where elves coexist peacefully with the humans, and 
make war with the dwarves. Where the sun rises, and the moon falls. 
Where one can change into a hero with a single swipe of his 
sword.<P>

The current deputies are: 
Kass, Milady, Kel, Bladestorm, Blackfyre, Legolas, Nate<P>

<FONT COLOR=\"red\">The quest is \"closed\".</Font>. People will no longer contract the
Whiteblodge Plague. However, people that are currently suffering from the
disease will still die if they do not find the cure! Now it is waiting until 
the next quest.
<HR>Results: <I>What did You think of the Whiteblodge
Plague?</I><P>
<TABLE>
   </TR><TD></TD><TD>209 people voted</TD></TR>
	<TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Way cool! Do more of those!</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"133\" ALT=\"39%\">62 / <FONT COLOR=red>39%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Was fun.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"62\" ALT=\"13%\">29 / <FONT COLOR=red>13%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Was okay, I guess.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"71\" ALT=\"15%\">33 / <FONT COLOR=red>15%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Didn't like it.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"30\" ALT=\"6%\">14 / <FONT COLOR=red>6%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Dude, you suck!</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"30\" ALT=\"6%\">14 / <FONT COLOR=red>6%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">What Plague?</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"94\" ALT=\"21%\">44 / <FONT COLOR=red>21%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Huh? Polls?</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"26\" ALT=\"6%\">12 / <FONT COLOR=red>6%</FONT>
      </NOBR></TD>
</TABLE>

<HR>
Regards and have fun!<P>
<I>Karn (Ruler of Karchan, Keeper of the Key to the Room of Lost
Souls)</I><P>

<I>To contact a Deputy, report a bug, contact Karn, or use email, <A
HREF=\"http://www.karchan.org/karchan/help/faq.html#38\" TARGET=\"_blank\">click here</A>.</I>
<P>

<I>Please read <A
HREF=\"http://www.karchan.org/karchan/help/thelaw.html\" TARGET=\"_blank\"><FONT
COLOR=red>The LAW (the most important Rules and Regulations of the game)</FONT></A>.</I>
<P>

If you are new to this game, and need help, please type <B>help</B> for a
listing 
of the commands, and <B>help &lt;command&gt;</B> for help on a specific 
command or subject. <P>
" where id=0;

END_OF_DATA

