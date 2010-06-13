<%--
-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-----------------------------------------------------------------------

--%>

<%@ page language="java"%>
<%@ page language="java" import="javax.naming.InitialContext"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Land of Karchan - New Character</title>
        <link rel="stylesheet" type="text/css" href="/css/karchangame.css" />
        <script language="JavaScript" src="/karchan/js/karchan.js"></script>
    </head>
    <body>
<h1>The Room of Lost Souls</h1>

<p>You are presently in the room of lost souls. It is dark all around you, but
you see transparent souls everywhere. You are yourself a soul. A sign is
visible on the west-wall. You make an attempt to read it. After a while you
figure it out. This is what it says:</p>
<i><p>Hello</p>
<p>
Let me introduce myself. I am Karn. You probably haven't heard of me, but I
am the diety of this place, and I am here to see that everything goes
according to the rules that I have made.</p>
<p>You are just a spirit, a soul, a ghost, a what-ever-you-call-it. You have no
substance whatsoever. You can't go about playing with no body! So here you can mould and form your
own body, just the way <b>you</b> like it.
</p>
There are certain rules which you have to abide by if you want to be able to
play without any problems:
<ul>
<li>leave this game every time you play it with the commando <B>quit</B>. If you
don't, your session will continue without you.
<li>do not start another session while you are still playing in one, that
will result in an error
<li>read the book in the cave
<li>do not go to another link while you are playing, this will make it
extremely hard to come back!
<li>remember that this game is in the development stages yet, al lot of
things won't work, but this game will keep getting better
</ul>
That is about it. More detailed information can be found in the book
mentioned. If you have entered everything, please hit the <B>Leave</B>
button. Or hit <b>Clear</b> for a new try...</p>

Karn, Ruler of Karchan, Keeper of the Key to the Room of Lost Souls
</i>

<form METHOD="GET" ACTION="/karchan/scripts/newchar.jsp">
<h2>Character Creation
Details</h2>
(Fictional) Name: <input type="text" name="name" value="" size="19"
MAXLENGTH="19"></p><p>
Password:
<input type="password" name="password" value="" size="10"
MAXLENGTH="39"></p><p>
Password (again for verification):
<input type="password" name="password2" value="" size="10"
MAXLENGTH="39"></p><p>
Title (Except name):
<input type="text" name="title" value="" size="50"
MAXLENGTH="79"></p><p>
Real Name (optional): <input type="text" name="realname" value="" size="49"
MAXLENGTH="49"></p><p>
Email address (optional): <input type="text" name="email" value="" size="49"
MAXLENGTH="80"></p>

<h3>Race</h3>
<p>Race:
<select name="race">
<option selectED>human
<option>dwarf
<option>elf
</select></p>

<p>Sex:
<select name="sex">
<option selectED>male
<option>female
</select></p>

<h3>Age and Build</h3>
<p>Age:
<select name="age">
<option value="young" selectED>Young
<option value="middle-aged">Middle-aged
<option value="old">Old
<option value="very old">Very old
</select></p>

<p>Length:
<select name="length">
<option value="very small">Very Small
<option value="small">Small
<option value="medium hight">Medium
<option value="tall" selectED>Tall
<option value="very tall">Very Tall
<option>none
</select></p>


<p>Width:
<select name="width">
<option value="very thin">Very Thin
<option value="thin">Thin
<option value="transparent">Transparent
<option value="skinny">Skinny
<option value="slender" selectED>Slender
<option value="medium">Medium
<option value="corpulescent">Corpulescent
<option value="fat">Fat
<option value="very fat">Very Fat
<option value="ascetic">Ascetic
<option value="athlethic">Athlethic
<option>none
</select></p>

<h3>Appearance</h3>
<p>Complexion:
<select name="complexion">
<option>black
<option>brown-skinned
<option>dark-skinned
<option>ebony-skinned
<option>green-skinned
<option>ivory-skinned
<option>light-skinned
<option>pale
<option>pallid
<option selectED>swarthy
<option>white
<option>none
</select></p>

<p>Eyes:
<select name="eyes">
<option selectED>black-eyed
<option>blue-eyed
<option>brown-eyed
<option>dark-eyed
<option>gray-eyed
<option>green-eyed
<option>one-eyed
<option>red-eyed
<option>round-eyed
<option>slant-eyed
<option>squinty-eyed
<option>yellow-eyed
<option>none
</select></p>

<p>Face:
<select name="face">
<option>big-nosed
<option>chinless
<option>dimpled
<option>double-chinned
<option>furry-eared
<option>hook-nosed
<option>jug-eared
<option>knobnosed
<option selectED>long-faced
<option>pointy-eared
<option>poppy-eyed
<option>potato-nosed
<option>pug-nosed
<option>red-nosed
<option>roman-nosed
<option>round-faced
<option>sad-faced
<option>square-faced
<option>square-jawed
<option>stone-faced
<option>thin-faced
<option>upnosed
<option>wide-mouthed
<option>none
</select></p>

<p>Hair:
<select name="hair">
<option>bald
<option>balding
<option selectED>black-haired
<option>blond-haired
<option>blue-haired
<option>brown-haired
<option>chestnut-haired
<option>dark-haired
<option>gray-haired
<option>green-haired
<option>light-haired
<option>long haired
<option>orange-haired
<option>purple-haired
<option>red-haired
<option>white-haired
<option>none
</select></p>

<p>Beard:
<select name="beard">
<option>black-bearded
<option>blond-bearded
<option>blue-bearded
<option>brown-bearded
<option>clean
<option>shaven
<option>fork-bearded
<option>gray-bearded
<option>green-bearded
<option>long bearded
<option>mustachioed
<option>orange-bearded
<option>purple-bearded
<option>red-bearded
<option>thinly-bearded
<option>white-bearded
<option>with a ponytale
<option selectED>none
</select></p>

<p>Arms:
<select name="arms">
<option>long-armed
<option>short-armed
<option>thick-armed
<option>thin-armed
<option selectED>none
</select></p>

<p>Legs:
<select name="legs">
<option>bandy-legged
<option>long-legged
<option>short-legged
<option>skinny-legged
<option>thin-legged
<option>thick-legged
<option selectED>none
</select></p>

<p><input type="submit" value="Submit">
<input type="reset" value="Clear">
</p>
</form>

<p><a href="/karchan/index.jsp" onMouseOver="changeImage('back')"
onMouseOut="changeImage('back')">
<img alt="Backitup!" src="/images/gif/webpic/buttono.gif" border="0"
id="back" name="back"><br></a>
</p>
<hr>
<div align=right>Last Updated $Date: 2006-01-15 10:25:22 +0100 (Sun, 15 Jan 2006) $
</div>
</body>
</html>
