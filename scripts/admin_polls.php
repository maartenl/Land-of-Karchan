<?
/*-------------------------------------------------------------------------
cvsinfo: $Header$
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
-------------------------------------------------------------------------*/
?>
<HTML>
<HEAD>
<TITLE>
Mmud - Admin
</TITLE>
</HEAD>
                                                                                                                      
<BODY>
<BODY BGCOLOR=#FFFFFF BACKGROUND="/images/gif/webpic/back4.gif">
<H1>
<IMG SRC="/images/gif/dragon.gif">
Item <?php echo $_REQUEST{"item"} ?></H1>

<?php
include $_SERVER['DOCUMENT_ROOT']."/scripts/admin_authorize.php";
/* the following constraints need to be checked before any kind of update is
to take place:

*/

if (isset($_REQUEST{"createnewpoll"}))
{
	// compute maximum new id.
	$result = mysql_query("select max(id) + 1 as maxid from polls"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	$maxid = 1;
	while ($myrow = mysql_fetch_array($result)) 
	{
		$maxid = $myrow["maxid"];
	}
	// create new poll.
    $query = "insert into polls (id, closed, owner, creation) values("
       .quote_smart($maxid)
    .",0,'"
       .quote_smart($_COOKIE["karchanadminname"])
       ."',now())";
    mysql_query($query, $dbhandle)
    or die("Query (".$query.") failed : " . mysql_error());
    writeLogLong($dbhandle, "Created new poll.", $query);
    printf("Created new poll.<P>");
}

if (isset($_REQUEST{"change_pollid"}))
{
    // change_pollid
    // change_title
    // change_description
    // change_closed
    // change_addchoice
    // change_deletechoice

	// change existing poll.
    $query = "update polls set title = '"
        .quote_smart($_REQUEST{"change_title"})
        ."', description = '"
        .quote_smart($_REQUEST{"change_description"})
        ."', closed = "
        .quote_smart($_REQUEST{"change_closed"})
        ." where id = "
        .quote_smart($_REQUEST{"change_pollid"})
		." and owner = '"
       .quote_smart($_COOKIE["karchanadminname"])
		."'";
    mysql_query($query, $dbhandle)
    or die("Query (".$query.") failed : " . mysql_error());
    writeLogLong($dbhandle, "Changed poll ".$_REQUEST{"change_pollid"}.".", $query);
    printf("Changed poll.<P>");

    // delete choice if applicable
    if (trim($_REQUEST{"change_deletechoice"}) == "1")
    {
		// compute maximum new id.
		$result = mysql_query("select max(id) as maxid from poll_choices "
     		."where pollid = "
	        .quote_smart($_REQUEST{"change_pollid"})
			, $dbhandle)
			or die("Query failed : " . mysql_error());
		$maxid = 1;
		while ($myrow = mysql_fetch_array($result)) 
		{
			$maxid = $myrow["maxid"];
		}
		// delete last poll choice.
	    $query = "delete from poll_choices where id = "
	       .quote_smart($maxid)
	       ." and pollid = "
	       .quote_smart($_REQUEST{"change_pollid"});
	    mysql_query($query, $dbhandle)
	    or die("Query (".$query.") failed : " . mysql_error());
	    writeLogLong($dbhandle, "Deleted poll choice for poll ".quote_smart($_REQUEST{"change_pollid"}).".", $query);
	    printf("Deleted poll choice.<P>");
    }
    // add choice if applicable
    if (trim($_REQUEST{"change_addchoice"}) != "")
    {
		// compute maximum new id.
		$result = mysql_query("select max(id) + 1 as maxid from poll_choices"
		    ." where pollid = "
            .quote_smart($_REQUEST{"change_pollid"})
			, $dbhandle)
			or die("Query failed : " . mysql_error());
		$maxid = 1;
		while ($myrow = mysql_fetch_array($result)) 
		{
			$maxid = $myrow["maxid"];
		}
		if ($maxid == "") 
		{
		    $maxid = 1;
		}
		// create new poll choice.
	    $query = "insert into poll_choices (id, pollid, choice) values("
	       .quote_smart($maxid)
	       .", "
	       .quote_smart($_REQUEST{"change_pollid"})
     	   .",'"
	       .quote_smart($_REQUEST{"change_addchoice"})
	       ."')";
	    mysql_query($query, $dbhandle)
	    or die("Query (".$query.") failed : " . mysql_error());
	    writeLogLong($dbhandle, "Created new poll choice for poll ".quote_smart($_REQUEST{"change_pollid"}).".", $query);
	    printf("Created new poll choice.<P>");
    }
    
}

$result = mysql_query("select *,  date_format(creation, \"%Y-%m-%d %T\") as creation2 from polls"
	, $dbhandle)
	or die("Query failed : " . mysql_error());
while ($myrow = mysql_fetch_array($result)) 
{
	if ($myrow["owner"] == null || $myrow["owner"] == "" ||
		$myrow["owner"] == $_COOKIE["karchanadminname"])   
	{
		printf("<b>id:</b> <A HREF=\"/scripts/admin_polls.php?pollid=%s\">%s</A><BR>", $myrow["id"], $myrow["id"]);
	}
	else
	{
		printf("<b>id:</b> %s<BR>", $myrow["id"]);
	}
	printf("<b>title:</b> %s<BR>", $myrow["title"]);
	printf("<b>description:</b> %s<BR>", $myrow["description"]);
	printf("<b>owner:</b> %s<BR>", $myrow["owner"]);
	printf("<b>creation:</b> %s<BR>", $myrow["creation2"]);
	printf("<b>closed:</b> %s<P>", ($myrow["closed"]=="0" ? "no":"yes"));
}

if (isset($_REQUEST{"pollid"}))
{
	$found = false;
	$result = mysql_query("select *,  date_format(creation, \"%Y-%m-%d %T\") as creation2 ".
	"from polls ".
	"where id = ".
    quote_smart($_REQUEST{"pollid"}).
    " and (owner is null or owner = '".
    quote_smart($_COOKIE["karchanadminname"]).
    "')"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		$found = true;
		// we found one!
?>
<FORM METHOD="GET" ACTION="/scripts/admin_polls.php">
<INPUT TYPE="hidden" NAME="change_pollid" VALUE="<?php echo $_REQUEST{"pollid"} ?>">
<b>id:</b> <?php echo $myrow["id"] ?><BR>
<b>title:</b> <INPUT TYPE="text" NAME="change_title" 
VALUE="<?php echo $myrow["title"] ?>" SIZE="40" MAXLENGTH="40"><BR>
<b>description:</b><BR>
<TEXTAREA NAME="change_description" ROWS="10" COLS="85">
<?php echo htmlspecialchars($myrow["description"]) ?></TEXTAREA><BR>
<TABLE><TR><TD><b>closed:</b> </TD><TD>

<INPUT TYPE="radio" NAME="change_closed" VALUE="1" 
<?php echo ($myrow["closed"] == 1? "checked" : "") ?>>yes
<BR><INPUT TYPE="radio" NAME="change_closed" VALUE="0" 
<?php echo ($myrow["closed"] == 0? "checked" : "") ?>>no
</TD></TR></TABLE>
<b>add choice:</b> <INPUT TYPE="text" NAME="change_addchoice" 
VALUE="" SIZE="40" MAXLENGTH="40"><BR>
<TABLE><TR><TD><b>delete last choice:</b> </TD><TD>

<INPUT TYPE="radio" NAME="change_deletechoice" VALUE="1">yes
<BR><INPUT TYPE="radio" NAME="change_deletechoice" VALUE="0" checked>no
</TD></TR></TABLE><BR>

<INPUT TYPE="submit" VALUE="Change Poll">
</b>   
</FORM>
<?php
	}
	if ($found)
	{
		$result = mysql_query("select * ".
		"from poll_choices ".
		"where pollid = ".
	    quote_smart($_REQUEST{"pollid"})
			, $dbhandle)
			or die("Query failed : " . mysql_error());
		while ($myrow = mysql_fetch_array($result)) 
		{
			printf("<b>id:</b> %s ", $myrow["id"]);
			printf("<b>choice:</b> %s<BR>", $myrow["choice"]);
		}
	}

	// print the values of the poll -> included in any forms.
	printf("<P>");
	$result = mysql_query("select * ".
	"from poll_values ".
	"where id = ".
    quote_smart($_REQUEST{"pollid"}).
    " and trim(comments) <> \"\" and comments is not null"
		, $dbhandle)
		or die("Query failed : " . mysql_error());
	while ($myrow = mysql_fetch_array($result)) 
	{
		printf("<b>name:</b> %s ", $myrow["name"]);
		printf("<b>comments:</b> %s<BR>", $myrow["comments"]);
	}
	printf("<P>You can see the results of the poll <A HREF=\"/scripts/poll.php?number=%s\">here</A>.<P>",
	quote_smart($_REQUEST{"pollid"}));
}

mysql_close($dbhandle);
?>

<FORM METHOD="GET" ACTION="/scripts/admin_polls.php">
<INPUT TYPE="hidden" NAME="createnewpoll" VALUE="yes">
<INPUT TYPE="submit" VALUE="Create New Poll">
</b>   
</FORM>

</BODY>
</HTML>
