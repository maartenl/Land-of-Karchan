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
Kass, Kel, Bladestorm, Legolas, Silverblade<P>

I am off to Croatia for two weeks. Will be back sometime around the 28th
of July 2002.
<HR>Results: <I>What happened to you when the mud went down?</I><P>
<TABLE>
   </TR><TD></TD><TD>208 people voted</TD></TR>
	<TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Didn't effect me much, not like I enjoy the game anyway</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"48\" ALT=\"11%\">22 / <FONT COLOR=red>11%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Felt a small twinge inside.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"45\" ALT=\"10%\">21 / <FONT COLOR=red>10%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">I have lots of other muds I play, I simply switched.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"19\" ALT=\"4%\">9 / <FONT COLOR=red>4%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">>Shivers down my back, sweating, paleness. First withdrawal symptoms.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"61\" ALT=\"13%\">28 / <FONT COLOR=red>13%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Cowered beneath the staircase, because the end
of the world could not be far behind.</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"129\" ALT=\"29%\">60 / <FONT COLOR=red>29%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Dude, you suck!</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"30\" ALT=\"7%\">14 / <FONT COLOR=red>7%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH=\"150\" ALIGN=\"RIGHT\">Forget this! When is the fighting finally coming back?</TD>
      <TD WIDTH=\"450\"><NOBR>
      <IMG SRC=\"/images/jpeg/mainbar2.jpg\" HEIGHT=20 width=\"117\" ALT=\"26%\">54 / <FONT COLOR=red>26%</FONT>
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

