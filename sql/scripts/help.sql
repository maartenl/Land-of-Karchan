#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

insert into help values('get','<H1>Get</H1>
<DL>
<DT><B>NAME</B>
<DD><B>get</B> - get an item that is lying on the ground<P>

<DT><B>SYNOPSIS</B>
        <DD><B>get</B> &lt;item&gt;

<DT><B>DESCRIPTION</B>
        <DD><B>Get</B> makes your character pick up an item lying on the
floor. It is possible that the item is too heavy or is not supposed to be
picked up, in which case you will receive a message telling you so.
<P>
<DT><B>EXAMPLES</B>
        <DD>
&quot;get leather jerkin&quot;<P>
You: <TT><B>You pick up a black, leather jerkin.</B></TT><BR>
Marvin: <TT><B>Hotblack picks up a black, leather jerkin.</B></TT><P>

<DT><B>SEE ALSO</B>
        <DD>drop, remove, wield, unwield<P>
</DL>
');
insert into help values('drop','<H1>Drop</H1>
<DL>
<DT><B>NAME</B>
<DD><B>drop</B> - drops an item from your inventory onto the ground<P>

<DT><B>SYNOPSIS</B>
        <DD><B>drop</B> &lt;item&gt;

<DT><B>DESCRIPTION</B>
        <DD><B>Drop</B> makes your character drop an item out of your
inventory and onto the ground. Once it is lying on the ground, it can be
picked up again by anyone coming by. An added effect is that you have to
carry around less stuff.
<P>
<DT><B>EXAMPLES</B>
        <DD>
&quot;drop leather jerkin&quot;<P>
You: <TT><B>You drop up a black, leather jerkin.</B></TT><BR>
Marvin: <TT><B>Hotblack drops up a black, leather jerkin.</B></TT><P>

<DT><B>SEE ALSO</B>
        <DD>get, remove, wield, unwield<P>
</DL>
');
insert into help values('put','<H1>Put</H1>

<DL>
<DT><B>NAME</B>
<DD><B>put</B> - put an item into a container<P>

<DT><B>SYNOPSIS</B>
        <DD><B>put</B> &lt;item&gt; <B>in</B> &lt;container&gt;<P>

<DT><B>DESCRIPTION</B>
        <DD><B>Put</B> makes your character put one of his/her items into
a container. A container is simply another item but with the added
possiblity of storing other items. The container in question may be in your
inventory or might be lying on the floor in the current room somewhere.<P>
You should be able to look at the container to see what it contains.<P>
Be aware that not all items are containers.
<P>
<DT><B>EXAMPLES</B>
        <DD>
&quot;put orange juice in brown leather sack&quot;<P>
You: <TT><B>You put a fresh, orange juice in the brown, leather sack.</B></TT><BR>
Marvin: <TT><B>Hotblack puts a fresh, orange juice in the brown, leather sack.</B></TT><P>

<DT><B>SEE ALSO</B>
        <DD>retrieve<P>
</DL>
');
insert into help values('retrieve','<H1>Retrieve</H1>

<DL>
<DT><B>NAME</B>
<DD><B>retrieve</B> - retrieve an item from a container<P>

<DT><B>SYNOPSIS</B>
        <DD><B>retrieve</B> &lt;item&gt; <B>from</B> &lt;container&gt;<P>

<DT><B>DESCRIPTION</B>
        <DD><B>Retrieve</B> makes your character retrieve one of his/her items
from
a container. A container is simply another item but with the added
possiblity of storing other items. The container in question may be in your
inventory or might be lying on the floor in the current room somewhere.<P>
You should be able to look at the container to see what it contains.<P>
Be aware that not all items are containers.
<P>
<DT><B>EXAMPLES</B>
        <DD>
&quot;retrieve orange juice from brown leather sack&quot;<P>
You: <TT><B>You pull a fresh, orange juice out of the brown, leather sack.</B></TT><BR>
Marvin: <TT><B>Hotblack pulls a fresh, orange juice out of the brown, leather
sack.</B></TT><P>

<DT><B>SEE ALSO</B>
        <DD>put<P>
</DL>
');


END_OF_DATA