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
Poll:<P>
<TABLE>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Way cool! Do more of those!</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="351" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Was fun.</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Was okay, I guess.</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Didn't like it.</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Dude, you suck!</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">What Plague?</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Huh? Polls?</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
   <TR>
      <TD WIDTH="100" ALIGN="RIGHT">Was fun.</TD>
      <TD WIDTH="450"><NOBR>
      <IMG SRC="mainbar2.jpg" HEIGHT=20 width="251" ALT="37%">4522 / <FONT COLOR=red>37%</FONT>
      </NOBR></TD>
   </TR>
</TABLE>



<P>

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

