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

END_OF_DATA

