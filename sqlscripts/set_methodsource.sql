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

END_OF_DATA

