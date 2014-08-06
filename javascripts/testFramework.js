// http://stackoverflow.com/questions/1144783/replacing-all-occurrences-of-a-string-in-javascript
String.prototype.replaceAll = function (find, replace) {
  var str = this;
  return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};
       
function loadTestUI()
{
  document.getElementById("testUidiv").innerHTML = 
    "<div id=\"players\" style=\"clear:both;width:100%;\">\n" +
    "    <div style=\"float:left;clear:none;width:50%;\">\n" +
    "        <span>Karn</span><div id=\"Karndiv\" style=\"height:200px;border-style: solid;\"></div>\n" +
    "    </div>    \n" +
    "    <div style=\"float:left;clear:none;width:50%;\">\n" +
    "        <span>Slartibartfast</span><div id=\"Slartibartfastdiv\" style=\"height:200px;border-style: solid;\"></div>    \n" +
    "    </div>\n" +
    "    <div style=\"float:left;clear:left;width:50%;\">\n" +
    "        <span>Hotblack</span><div id=\"Hotblackdiv\" style=\"height:200px;border-style: solid;\"></div>    \n" +
    "     </div>    \n" +
    "    <div style=\"float:left;clear:none;width:50%;\">\n" +
    "        <span>Marvin</span><div id=\"Marvindiv\" style=\"height:200px;border-style: solid;\"></div>    \n" +
    "    </div>\n" +
    "</div>\n" +
    "<div style=\"clear:both;\">\n" +
    "    <span>Systemlog</span>\n" +
    "    <div id=\"systemlog\" style=\"background-color: lightgrey;border-style: solid;\">\n" +
    "    </div>\n" +
    "</div>";
  print("Test framework loaded...");
}

function Person(name, posession, indirect, direct) 
{
  this.name = name;
  this.posession = posession;
  this.indirect = indirect;
  this.direct = direct;
}
        
function Room(id, title)
{
  this.id = id;
  this.title = title;
}

Person.prototype.personal = function(message)
{
  document.getElementById(this.name + "div").innerHTML += message;
}

/**
 * testing function that clears the log.
 */        
Person.prototype.clear = function()
{
  document.getElementById(this.name + "div").innerHTML = "";
}
        
Person.prototype.sendMessage = function(person, message)
{
  if (message === undefined)
  {
      message = person;
      person = null;
      for (i in persons.persons)
      {
        if (persons.persons[i].room.id == this.room.id)
        {
          if (persons.persons[i] === this)
          {
            persons.persons[i].personal(replaceTagsMe(message, this, this));
          }
          else
          {
            persons.persons[i].personal(replaceTagsSomebodyElse(message, this, this));
          }
        }
      }
      return;
  }
  this.personal(replaceTagsMe(message, this, person));
  person.personal(replaceTagsTarget(message, this, person));
  for (i in persons.persons)
  {
    if (persons.persons[i].room.id == this.room.id && persons.persons[i] !== this && persons.persons[i] !== person)
    {
      persons.persons[i].personal(replaceTagsSomebodyElse(message, this, person));
    }
  }
}

Person.prototype.sendMessageExcl = function(person, message)
{
  if (message === undefined)
  {
      message = person;
      person = null;
      for (i in persons.persons)
      {
        if (persons.persons[i].room.id == this.room.id)
        {
          if (persons.persons[i] !== this)
          {
            persons.persons[i].personal(replaceTagsSomebodyElse(message, this, this));
          }
        }
      }
      return;
  }
  for (i in persons.persons)
  {
    if (persons.persons[i].room.id == this.room.id && persons.persons[i] !== this && persons.persons[i] !== person)
    {
      persons.persons[i].personal(replaceTagsSomebodyElse(message, this, person));
    }
  }
}

Room.prototype.sendMessage = function(person, message)
{
  if (message === undefined)
  {
      message = person;
      person = null;
      for (i in persons.persons)
      {
        if (persons.persons[i].room.id == this.id)
        {
          persons.persons[i].personal(message);
        }
      }
      return;
  }
}

var Marvin = new Person("Marvin", "his", "him", "he");
var Hotblack = new Person("Hotblack", "his", "him", "he");
var Slartibartfast = new Person("Slartibartfast", "her", "her", "she");
var Karn = new Person("Karn", "his", "him", "he");

var room1 = new Room( 1, "The Cave");
var room2 = new Room( 2, "Outside The Cave");
var room3 = new Room( 3, "On the road");

function replaceTagsSomebodyElse(message, person, toperson)
{
  return message.replaceAll("%SNAMESELF", person.name).
    replaceAll("%SNAME", person.name).
    replaceAll("%SHISHER", person.posession).
    replaceAll("%SHIMHER", person.indirect).
    replaceAll("%SHESHE", person.direct).
    replaceAll("%SISARE", "is").
    replaceAll("%SHASHAVE", "has").
    replaceAll("%SYOUPOSS", person.name + "s").
    replaceAll("%VERB1", "es").
    replaceAll("say%VERB2", "says").
    replaceAll("y%VERB2", "ies").
    replaceAll("%VERB2", "s").
    replaceAll("%TNAMESELF", toperson.name).
    replaceAll("%TNAME", toperson.name).
    replaceAll("%THISHER", toperson.posession).
    replaceAll("%THIMHER", toperson.indirect).
    replaceAll("%THESHE", toperson.direct).
    replaceAll("%TISARE", "is").
    replaceAll("%THASHAVE", "has").
    replaceAll("%TYOUPOSS", toperson.name + "s");
}

function replaceTagsMe(message, person, toperson)
{
  return message.replaceAll("%SNAMESELF", "yourself").
    replaceAll("%SNAME", "you").
    replaceAll("%SHISHER", "your").
    replaceAll("%SHIMHER", "you").
    replaceAll("%SHESHE", "you").
    replaceAll("%SISARE", "are").
    replaceAll("%SHASHAVE", "have").
    replaceAll("%SYOUPOSS", "your").
    replaceAll("%VERB1", "").
    replaceAll("%VERB2", "").
    replaceAll("%TNAMESELF", toperson.name).
    replaceAll("%TNAME", toperson.name).
    replaceAll("%THISHER", toperson.posession).
    replaceAll("%THIMHER", toperson.indirect).
    replaceAll("%THESHE", toperson.direct).
    replaceAll("%TISARE", "is").
    replaceAll("%THASHAVE", "has").
    replaceAll("%TYOUPOSS", toperson.name + "s");
}

function replaceTagsTarget(message, person, toperson)
{
  return message.
    replaceAll("%SNAMESELF", person.name).
    replaceAll("%SNAME", person.name).
    replaceAll("%SHISHER", person.posession).
    replaceAll("%SHIMHER", person.indirect).
    replaceAll("%SHESHE", person.direct).
    replaceAll("%SISARE", "is").
    replaceAll("%SHASHAVE", "has").
    replaceAll("%SYOUPOSS", person.name + "s").
    replaceAll("%VERB1", "es").
    replaceAll("say%VERB2", "says").
    replaceAll("y%VERB2", "ies").
    replaceAll("%VERB2", "s").
    replaceAll("%TNAMESELF", "yourself").
    replaceAll("%TNAME", "you").
    replaceAll("%THISHER", "your").
    replaceAll("%THIMHER", "you").
    replaceAll("%THESHE", "you").
    replaceAll("%TISARE", "are").
    replaceAll("%THASHAVE", "have").
    replaceAll("%TYOUPOSS", "your");  
}

var persons = {
  persons : [Marvin, Hotblack, Slartibartfast, Karn]
};

persons.find= function(name) 
{
  for (i in this.persons)
  {
    if (this.persons[i].name.toUpperCase() == name.toUpperCase()) 
    {
      return this.persons[i];
    }
  }
  return null;
};

var rooms = {
  rooms : [room1, room2, room3]
};

rooms.find= function(id) 
{
  for (i in this.rooms)
  {
    if (this.rooms[i].id == id)
    {
      return this.rooms[i];
    }
  }
  return null;
};

Marvin.room = room1;
Hotblack.room = room1;
Slartibartfast.room = room1;
Karn.room = room1;

function print(stuff)
{
  document.getElementById("systemlog").innerHTML += 
      "[" + new Date().toLocaleString() + "] : [[" + stuff + "]]<br/>";
}

