#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# update logonmessage
#

update mm_errormessages
set description="
<HTLM>
<HEAD>
<TITLE>Invalid Command</TITLE>
</HEAD>
<BODY BGCOLOR=\"#FFFFFF\" BACKGROUND=\"/images/gif/webpic/back4.gif\">
<h1><img SRC=\"/images/gif/dragon.gif\">
Invalid Command</h1>
The mud was unable to properly parse your command.
This is a very rare error message. Your command was illegal.
<p>
<a HREF=\"/karchan/enter.html\">
<img SRC=\"/images/gif/webpic/buttono.gif\" BORDER=\"0\"></a><p>
</BODY>
</HTML>
"
where msg ="the entire command or part of the command could not be parsed correctly";
END_OF_DATA
