#!/bin/sh

echo name of script is $0
echo first argument is $1
echo second argument is $2
echo all arguments is $@
echo number of arguments is $#

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
update mm_methods
set src="
function command(person,totalcommand)
{
  /* 
   * command syntax : pounce someone
   * person = the person issuing the command
   * totalcommand = the command, for example \"pounce karn\"
   */
  var command = totalcommand.split(\" \"); 
  var toperson = persons.find(command[1]);
  if (toperson === null || toperson.room.id !== person.room.id)
  {
      person.personal(\"You cannot find that person.<br/>\");
      return;
  }
  if (toperson.name === person.name)
  {
      person.personal(\"You can't pounce yourself, you silly goose!<br/>\");
      return;
  }       
  person.sendMessage(toperson, \"%SNAME grin%VERB2 maliciously at %TNAME and pounce%VERB2 on %THIMHER. After tussling for a   minute, %SNAME and %TNAME both sit up, breathing hard.<br/>\");
}
"
where name="pounce";

update mm_methods
set src="
function command(person,totalcommand)
{
  /* command syntax : yawn (to someone)
   * person = the person issuing the command
   * command = an array containing all the words in the command, for example:
   * \"yawn to cedri\" would become: 
   * command[0]=\"yawn\", command[1]=\"to\", command[2]=\"Cedri\" command.length=3
  */
  var command = totalcommand.split(\" \"); 
  if (command.length == 3)
  {
      // yawn to someone
      toperson = persons.find(command[2]);
      if (toperson == null || toperson.room.id !== person.room.id)
      {
           person.personal(\"You cannot find that person.<br/>\");
           return;
      }
      if (toperson.name == person.name)
      {
          person.personal(\"You yawn to yourself. <br/>\");
          person.sendMessageExcl(\"%SNAME yawns to %SHIMHERself. <br/>\");
          return;
      }
      person.sendMessage(toperson, \"%SNAME yawn%VERB2 to %TNAME. <br/>\");
  }
  else
  {
      // yawn
      person.sendMessage(\"%SNAME yawn%VERB2 loudly.<br/>\");
  }
}  
"
where name="yawn";

update mm_methods
set src="
function command(person,totalcommand)
{
  // command syntax : goose someone
  var command = totalcommand.split(\" \"); 
  toperson = persons.find(command[1]);
  if (toperson == null || toperson.room.id != person.room.id)
  {
    person.personal(\"You cannot find that person.<br/>\");
    return;
  }
  if (toperson.name == person.name)
  {
    person.personal(\"That is sick, you pervert!  Do that someplace private!<br/>\");
    return;
  }
  person.sendMessage(toperson, \"%SNAME goose%VERB2 %TYOUPOSS behind.<br/>\");
}"
where name="goose";

update mm_methods
set src="
function command(person, command)
{
  var mydescription = \"You are now in the Inn &quot;The Twisted Dwarf&quot;. It is dark, as always \" +
    \"in these places. The windows are of a dark blue color, which doesn't allow \" +
    \"much light to enter the room. A lot of woodwork, wooden tables, chairs and a \" +
    \"rather large bar on the right of you are visible. Almost against the back \" +
    \"of the room is a comparatively big cupboard. The cupboard appears to \" +
    \"be open. Behind the bar a norse small ugly dwarf is cleaning some glasses. \" +
    \"On the same bar you see a sign on a piece of wood, apparently this\" +
    \"is the menu for the day.<P> Scattered among the tables are groups of \" +
    \"people, playing what seems to be a dwarfish version of Poker. You see a sign\" +
    \"on the wall behind the counter.\";
  var myitemdescription = \"<H1><IMG SRC=\\"/images/gif/herberg5.gif\\">The \" +
    \"Cupboard</H1><HR>\" +
    \"You look at the cupboard. It is very old and wormeaten. With one knock you\" +
    \"could probably knock it down, but I doubt if the barman would appreciate\" +
    \"this much. It is open. Both doors of the cupboard are ajar. In it you can\" +
    \"see, amazingly, a staircase leading to the north and up into a hidden\" +
    \"room.<P>\";
  /* command syntax : open cupboard
  */
  if (person.room.north != null)
  {
      person.personal(\"The cupboard is already open.<br/>\");
      return;
  }
  hiddenroom=rooms.find(20);
  person.room.north = hiddenroom;
  person.room.description = mydescription;
  person.room.picture = \"/images/gif/herberg4.gif\";
  hiddenroom.south = person.room;
  myitem = person.room.getItems(-32);
  if (myitem.length == 1) 
  {
      myitem[0].setAttribute(\"description\", myitemdescription);
  }
  person.sendMessage(\"%SNAME open%VERB2 the door of the cupboard.<br/>\");
  person.personal(\"[<A HREF=backslashquote/images/mpeg/her.mpgbackslashquote>MPEG</A>]<br/>\"); 
}"
where name = 'open_cupboard';

update mm_methods
set src="
function command(person, command)
{
  var mydescription = \"You are now in the Inn &quot;The Twisted Dwarf&quot;. It is dark, as always  \" +
    \"in these places. The windows are of a dark blue color, which doesn't allow \" +
    \"much light to enter the room. A lot of woodwork, wooden tables, chairs and a    \" +
    \"rather large bar on the right of you are visible. Almost against the the back            \" +
    \"of the room is a comparatively big cupboard.  Behind the bar a norse            \" +
    \"small ugly dwarf is cleaning some glasses. On the same bar you            \" +
    \"see a sign on a piece of wood, apparently this is the menu for the \" +
    \"day.<br/> Scattered among the tables are groups of people, playing what \" +
    \"seems to be a dwarfish version of Poker. You see a sign on the wall behind \" +
    \"the counter.\";
  /* command syntax : close cupboard */
  if (person.room.north == null)
  {
      person.personal(\"The cupboard is already closed.<br/>\");
      return;
  }
  hiddenroom=rooms.find(20);
  person.room.description = mydescription;
  person.room.picture = \"/images/gif/herberg1.gif\";
  person.room.north = null;
  hiddenroom.south = null;
  myitem = person.room.getItems(-32);
  if (myitem.length == 1)
  {
      myitem[0].removeAttribute(\"description\");
  }
  person.sendMessage(\"%SNAME close%VERB2 the door of the cupboard.<br/>\");
  person.personal(\"[<A HREF=backslashquote/images/mpeg/her2.mpgbackslashquote>MPEG</A>]<br/>\");
}"
where name = 'close_cupboard';

update mm_methods
set src="
function command(person, command)
{
  var myitems = person.room.getItems(-3);
  if (myitems.length != 1)
  {
    person.personal(\"You feel very confused.<br/>\");
    return;
  }
  var myitem = myitems[0];
  if (myitem.isAttribute(\"ispulled\"))
  {
    person.sendMessage(\"%SNAME pull%VERB2 vehemently on a chain in the wall.<br/>\");
    person.personal(\"But nothing happens.<br/>\");
    return;
  }
  person.sendMessage(\"%SNAME pull%VERB2 at the chain in the wall. You hear the ominous sounds of wheels churning and rope snapping, yet the sound suddenly stops again.<br/>\");
  person.sendMessage(\"%SNAME let%VERB2 go of the chain.<br/>\");
  person.personal(\"You wonder what that was all about.</br>\");
  myitem.setAttribute(\"ispulled\", true);
  return {title: \"The Chain\",
    image: \"/images/gif/chain.gif\",
    body: world.getAttribute(\"scripts.commands.karn.pulling.on.chain\")};
//    \"You take the chain into both your hands \" +
//    \"and you give it a good yank! Something amazing happens. You pull the chain a \" +
//    \"few centimeters out of the wall. At the same moment behind the wall, if you \" +
//    \"listen very well, you hear the cracking of wood, the squaking of rope and \" +
//    \"other ominous sounds. My god! What have you done! What devilish boobytrap \" +
//    \"will within a few moments crush you to atoms? You stand still unable to get \" +
//    \"away from the fate that awaits you.<p>However, within a few minutes, the \" +
//    \"sounds stop and everything is back in its usual order. Nothing happened, but \" +
//    \"what was that all about?</p>\"};
}"
where name = 'pull_chain';

replace into mm_worldattributes 
(name, contents, creation, type, owner)
values("scripts.commands.karn.pulling.on.chain", "You take the chain into both your hands 
and you give it a good yank! Something amazing happens. You pull the chain a 
few centimeters out of the wall. At the same moment behind the wall, if you 
listen very well, you hear the cracking of wood, the squaking of rope and   
other ominous sounds. My god! What have you done! What devilish boobytrap   
will within a few moments crush you to atoms? You stand still unable to get 
away from the fate that awaits you.<p>However, within a few minutes, the 
sounds stop and everything is back in its usual order. Nothing happened, but 
what was that all about?</p>", now(), "string", "Karn");

# update mm_methods set src = replace(src, "backslashquote", "\\""");

END_OF_DATA

