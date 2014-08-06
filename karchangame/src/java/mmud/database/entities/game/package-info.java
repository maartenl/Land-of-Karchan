/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * <p>
 * Provides the JPA Database Entities.</p>
 * <p>
 * <img src="../../../../images/package-info_gameentities.png"/></p>
 * <p>
 * Mapping of database tables to database entities.<table>
 * <tr><th>database table</th><th>entity</th></tr>
 * <tr><td>mm_usertable</td><td>Person</td></tr>
 * <tr><td>mm_items</td><td>ItemDefinition</td></tr>
 * <tr><td>mm_itemtable</td><td>Item</td></tr>
 * <tr><td>mm_charitemtable</td><td>CharitemTable</td></tr>
 * </table>
 *
 * <H3><A NAME="contents">List of Database Tables</H3>
 * <TABLE>
 *
 * <TR valign=top><TD>Tablename</TD><TD>Description</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_admin">mm_admin</A></TD><TD>Contains the userids and
 * passwords of people administrating the mud.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_answers">mm_answers</A></TD><TD>Contains
 * questions and answers forbots.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_area">mm_area</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Provides
 * an clustering of rooms called an area. Every room is identified as belonging
 * to a area, the main area beingcalled "<I>Main</I>".</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_bannednamestable">mm_bannednamestable</A></TD><TD>Contains names
 * of characters that are not allowed to log on to the mud. Characters that are
 * <I>banned</I>. These are independent of IP address or hostname of the user.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_bantable">mm_bantable</A></TD><TD>A table of
 * persons that are not allowed to log on to the mud. Persons that are
 * <I>banned</I>. These are dependent of IP address or hostname, or even a
 * range of these in the case of dynamic ip/hostnames.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_boardmessages">mm_boardmessages</A></TD><TD>Contains all messages
 * posted on the publicmessage boards.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_boards">mm_boards</A></TD><TD>Identifies individual public message
 * boards,</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_charattributes">mm_charattributes</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Attributes
 * of a certain character (player/non-player).</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_charitemtable">mm_charitemtable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Indicates
 * who is carrying which item instances.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_commandlog">mm_commandlog</A></TD><TD>Logging table, contains
 * everycommand of every user entered with an appropriate timestamp.</TD></TR>
 *
 * <TR valign=top><TD><A HREF="#mm_commands">mm_commands</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Special
 * commands that have to be programmedin methods.</TD></TR> <TR
 * valign=top><TD><A HREF="#mm_errormessages">mm_errormessages</A></TD><TD></TD></TR> <TR
 * valign=top><TD><A HREF="#mm_events">mm_events</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Equal
 * to the linux crontab. Can execute methodsat specific recurring times in the
 * game.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_guildranks">mm_guildranks</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>The different ranks that can exist
 * within a guild</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_guilds">mm_guilds</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>The different guilds that are
 * available in the game</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_help">mm_help</A></TD><TD>Contains help <I>manpages</I> on individual
 * (standard) commands</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_ignore">mm_ignore</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Contains a list of people playing the
 * game that are ignoring other people playing the game.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_itemattributes">mm_itemattributes</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Attributes
 * of a certain item.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_itemitemtable">mm_itemitemtable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Indicates
 * which item instances contain which item instances.</TD></TR> <TR
 * valign=top><TD><A HREF="#mm_items">mm_items</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Provides
 * item definitions. So, basically, if you have a bucket, the definition would
 * be in this table, while actual instances of the bucket being carried around
 * are in the table mm_itemtable. This table should contain characteristics
 * common to all buckets.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_itemtable">mm_itemtable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Provides
 * instances of item definitions.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_log">mm_log</A></TD><TD>Logging table, for describing important
 * events in thelife of characters and administrators.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_oldlog">mm_oldlog</A></TD><TD>Logging table backup, contains
 * everything older than a day.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_macro">mm_macro</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Contains
 * macros created by players in the game.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_mailtable">mm_mailtable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Contains
 * mud mail of characters in the game</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_methods">mm_methods</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Special
 * methods that can be entered in the database to provide characters with
 * special abilities or to be executed periodically. See related tables
 * mm_commands and mm_events.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_roomattributes">mm_roomattributes</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Attributes
 * of a certain room.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_roomitemtable">mm_roomitemtable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Indicates
 * which room contains which item instances </TD></TR>
 * <TR valign=top><TD><A HREF="#mm_rooms">mm_rooms</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Contains
 * room definitions.</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_shopkeeperitems">mm_shopkeeperitems</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Provides
 * which items a shopkeeper isallowed to sell/buy and when the shopkeeper
 * decides to stop buying itemsfrom characters (i.e. when the shop has plenty
 * of those specific items insupply)</TD></TR>
 * <TR valign=top><TD><A HREF="#mm_sillynamestable">mm_sillynamestable</A></TD><TD></TD></TR> <TR
 * valign=top><TD><A HREF="#mm_unbantable">mm_unbantable</A></TD><TD></TD></TR>
 *
 * <TR valign=top><TD><A HREF="#mm_usertable">mm_usertable</A></TD><TD><A HREF="#important"><SUP>(Note)</SUP></A>Contains
 * all characters in the game.</TD></TR>
 * <TR valign=top><TD></TD><TD></TD></TR>
 * </TABLE>
 * <A NAME="important"><B>Note</B>: these tables are really really important as
 * they provide relationships to each other. This effectively means that if
 * data changes in one table, there is a chance that this will effect data in
 * another table, especially when regarding the foreign key to primary key
 * mapping.<P>
 *
 * <H3>List of Fields in Each Table</H3>
 * (<I>* denotes primary key</I>)
 * <H4><A HREF="#contents"><A NAME="mm_methods">mm_methods</A></H4>
 * <DL>
 * <DT>name
 * <DD>the name of the method
 * <DT>src
 * <DD>the source code of the method in Simkin embedded scripting language
 * syntax.
 * <DT>owner
 * <DD>the owner of the object, i.e. the one that is allowed to make changes.
 * If this is NULL, the first one to make any changes owns the item.
 * <DT>creation
 * <DD>timestamp, is updated with the current datetime
 * whenever a change takes place on the record in question.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_admin">mm_admin</A></H4>
 * <DL>
 * <DT>name
 * <DD>the name of the administrator. This is usually related to the
 * <I>owner</I> field in other tables.
 * <DT>passwd
 * <DD>the password of the administrator encoded in SHA1() method.
 * <DT>ip
 * <DD>the ip address last logged of the person entering (currently not used)
 * <DT>created
 * <DD>date/time of creation of the row.
 * <DT>validuntil
 * <DD>the date until which the account is active.
 * <DT>email
 * <DD>email address of the administrator.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_commands">mm_commands</A></H4>
 * <DL>
 * <DT>id
 * <DD>identification of the command
 * <DT>callable
 * <DD>whether or not the command is active. Either 0 (inactive) or 1 (active).
 * <DT>command
 * <DD>the command which triggers this. % as wildcards are permitted.
 * <DT>method_name
 * <DD>the name of the method to be executed. Refers to mm_methods.
 * <DT>room
 * <DD>the room in which this command might be active. In case of NULL, the
 * command is activated in <I>all</I> rooms.
 * <DT>owner
 * <DD>the owner of the object, i.e. the one that is allowed to make changes.
 * If this is NULL, the first one to make any changes owns the item.
 * <DT>creation
 * <DD>timestamp, is updated with the current datetime
 * whenever a change takes place on the record in question.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_events">mm_events</A></H4>
 * <DL>
 * <DT>eventid
 * <DD>unique identification of the event.
 * <DT>name
 * <DD>name of the character issueing the command. Might be NULL, if it is not
 * a specific character issueing the action.
 * <DT>month
 * <DD>month in which to execute this event. -1 is every month.
 * <DT>dayofmonth
 * <DD>dayofmonth in which to execute this event. -1 is every day.
 * <DT>hour
 * <DD>hour in which to execute this event. -1 is every hour.
 * <DT>minute
 * <DD>minute in which to execute this event. -1 is every minute.
 * <DT>dayofweek
 * <DD>dayofweek in which to execute this event. -1 is every dayofweek.
 * Usually, this value is for if an event should take place once a week on a
 * specific weekday.
 * <DT>callable
 * <DD>whether or not the event is active. Either 0 (inactive) or 1 (active).
 * <DT>method_name
 * <DD>the name of the method to be executed. Refers to mm_methods.
 * <DT>room
 * <DD>the room in which the event is to take place. Might be NULL, if the
 * appropriate action to be taken is related to a person and not to a room.
 * <DT>owner
 * <DD>the owner of the object, i.e. the one that is allowed to make changes.
 * If this is NULL, the first one to make any changes owns the item.
 * <DT>creation
 * <DD>timestamp, is updated with the current datetime
 * whenever a change takes place on the record in question.
 * </DL>
 * <A NAME="mm_itemattributes">
 * <A NAME="mm_roomattributes">
 * <A NAME="mm_charattributes">
 * <H4><A HREF="#contents"><A NAME="mm_items">mm_items</A></H4>
 * <DL>
 * <DT>id
 * <DD>identification of this item definition, integer.
 * <DT>wieldable
 * <DD>no longer used
 * <DT>wearable
 * <DD>how this item is to be worn as a bitmask, integer. Values :
 * 1=head,2=neck,4=torso,8=arms,16=leftwrist,32=rightwrist,64=leftfinger,
 * 128=rightfinger,256=feet,512=hands,1024=floating,2048=waist,
 * 4096=legs,8192=eyes,16384=ears,32768=body,65536=wield lefthand,
 * 131072=wield righthand,262144=wielding both hands
 * <DT>container
 * <DD>whether or not this item can server as a container for other items.
 * Possible values are 0 (no container) or 1 (container). Bear in mind that the
 * following fields are only relevant if container is set to 1: <I>capacity,
 * isopenable, keyid, containtype</I>.
 * <DT>getable
 * <DD>whether or not the item can be picked up from the floor. <B>Important:</B> items with a
 * negative id number, are <I>never</I> getable and are mostly used as
 * windowdressing for rooms. In this case the field value is ignored by the mud.
 * <DT>dropable
 * <DD>whether or not the item can be dropped unto the floor. <B>Important:</B> items with a
 * negative id number, are <I>never</I> dropable and are mostly used as
 * windowdressing for rooms. In this case the field value is ignored by the mud.
 * <DT>visible
 * <DD>will make the item visible or invisible when in a room or in an
 * inventory. The default is <I>visible</I>.
 * <DT>owner
 * <DD>the owner of the object, i.e. the one that is allowed to make changes.
 * If this is NULL, the first one to make any changes owns the item.
 * <DT>creation
 * <DD>timestamp, is updated with the current datetime
 * whenever a change takes place on the record in question.
 *
 * <DT>capacity
 * <DD>the maximum weight a container can contain. Only relevant if
 * container=1.
 * <DT>isopenable
 * <DD>whether or not the container can be opened (i.e. has a lid). openable=1,
 * not openable = 0. Only relevant if
 * container=1.
 * <DT>keyid
 * <DD>the item definition id of the item that can be used to lock and unlock
 * the container. If the item definition is 0, the container does not have a
 * lock. Only relevant if
 * container=1.
 * <DT>containtype
 * <DD>the type of items that are allowed to be stored in the container. Only relevant if
 * container=1. (TODO)
 * </DL>
 * <A NAME="mm_shopkeeperitems">
 * <A NAME="mm_itemtable"> * <H4><A HREF="#contents"><A NAME="mm_charitemtable">mm_charitemtable</A></H4>
 * <DL>
 * <DT>id
 * <DD>identification of this item instance
 * <DT>belongsto
 * <DD>who owns this item instance
 * <DT>wearing
 * <DD>are you wearing or wielding the item. wearing==null means that the item
 * is neither being worn nor wielded.
 * </DL>
 * <A NAME="mm_roomitemtable">
 *
 * <A NAME="mm_itemitemtable">
 * <H4><A HREF="#contents"><A NAME="mm_rooms">mm_rooms</A></H4>
 * <DL>
 * <DT>id
 * <DD>identification of the room
 * <DT>west, east, north, south, up, down
 * <DD>ids of neighbouring rooms
 * <DT>contents
 * <DD>description of the room
 * <DT>owner
 * <DD>the owner of the object, i.e. the one that is allowed to make changes.
 * If this is NULL, the first one to make any changes owns the item.
 * <DT>creation
 * <DT>area
 * <DD>the area to which the room belongs.
 * <DT>title
 * <DD>title of the room, it will be visible nicely at the top in header format
 * <DT>picture
 * <DD>the picture of the room
 * </DL>
 *
 * <A NAME="mm_area">
 * <H4><A HREF="#contents"><A NAME="mm_usertable">mm_usertable</A></H4>
 * <DL>
 * <DT>name
 * <DD>identification of this character. May only contain [A-z_a-z].
 * <DT>password
 * <DD>the password of the character/user. This is encoded into the mysql
 * database using the SHA1() method.
 * <DT>race
 * <DD>can be anything, has no effect on the stats of the character.
 * <DT>god
 * <DD>what this character is, 0=default user, 1=god user (not used currently),
 * 2=bot, 3=mob, 4=shopkeeper
 * <DT>alignment
 * <DD>the good or evil aspect of a character, 0 is evil, 8 is good, and
 * anything in between. The default is 8.
 * <DT>movementstats
 * <DD>the ability to move, 0 is exhausted and cannot move, 1000 is fine. The
 * default is 1000.
 * <DT>lok
 * <DD>string containing the session cookie of the user when he is logged on
 * and active.
 * <DT>notes
 * <DD>notes concerning this user. Can be entered by deputies.
 * <DT>state
 * <DD>string containing the state of a user. Can be empty, but is useful for
 * roleplaying to show what is going on with your character.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_commandlog">mm_commandlog</A></H4>
 * <DL>
 * <DT>id
 * <DD>auto-generated id
 * <DT>stamp
 * <DD>timestamp of when the command was entered.
 * <DT>name
 * <DD>name of the user that entered the command.
 * <DT>command
 * <DD>the command entered by the user. Has a maximum of 255 characters. This
 * limitation
 * should not be a problem unless rather large conversations take place.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_macro">mm_macro</A></H4>
 * <DL>
 * <DT>id
 * <DD>the id of the macro, a unique reference id that is automatically
 * assigned.
 * <DT>macroname
 * <DD>the name of the macro, defined by the user.
 * <DT>contents
 * <DD>contents of the macro.
 * <DT>name
 * <DD>name of the user that defined this macro.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_mailtable">mm_mailtable</A></H4>
 * <DL>
 * <DT>id
 * <DD>the id of the mail, a unique reference id that is automatically
 * assigned.
 * <DT>haveread
 * <DD>if the mail items has been read or not. (read=1, not read=0)
 * <DT>newmail
 * <DD>if the mail is new (i.e. has not been read since the previous
 * session.
 * <DT>deleted
 * <DD>indicates that the message has been deleted.
 * <DT>item_id
 * <DD>indicates that this message has its representative item in the game.
 * </DL>
 *
 * <A NAME="mm_sillynamestable">
 *
 * <A NAME="mm_bannednamestable">
 * <A NAME="mm_unbantable">
 *
 * <A NAME="mm_bantable">
 * <A NAME="mm_errormessages">
 * <A NAME="mm_answers">
 * <H4><A HREF="#contents"><A NAME="mm_guilds">mm_guilds</A></H4>
 * <DL>
 * <DT>name
 * <DD>the name of the guild, unique identifies each guild.
 * <DT>daysguilddeath
 * <DD>this is a counter that starts counting the days when the number of guild members
 * falls below the <I>minguildmembers</I> threshhold.
 * session.
 * <DT>maxguilddeath
 * <DD>this is the maximum threshhold. If the <I>daysguilddeath</I> counter
 * reaches this, the guild will be purged from the database automatically.
 * <DT>minguildmembers
 * <DD>the minimum amount of members a guild must have before it becomes
 * active, and to prevent it from being purged.
 * <DT>minguildlevel
 * <DD>the minimum level of a player character before he/she is allowed to
 * apply to the guild.
 * <DT>bossname
 * <DD>the original guild master of the guild.
 * <DT>active
 * <DD>whether or not the guild is active, 1=active, 0=inactive. A new guild is
 * by default inactive and becomes active when it reaches the minimum required
 * amount of guildmembers. See <I>minguildmembers</I>.
 * <DT>logonmessage
 * <DD>the logonmessage that guild members will see when they log on.
 * </DL>
 * <H4><A HREF="#contents"><A NAME="mm_guildranks">mm_guildranks</A></H4>
 * <DL>
 * <DT>title
 * <DD>the title of a rank within the guild, for example 'Head Honcho'.
 * <DT>guildlevel
 * <DD>a number that specifies the position of the rank in the hierarchie
 * of ranks. Usually 0 is lowest rank and 100 is highest rank.
 * <DT>guildname
 * <DD>a reference to the guild to which this rank belongs.
 * <DT>accept_access
 * <DD>NOT USED
 * <DT>reject_access
 * <DD>NOT USED
 * <DT>settings_access
 * <DD>NOT USED
 * <DT>logonmessage_access
 * <DD>NOT USED
 * </DL>
 *
 * <H4><A HREF="#contents"><A NAME="mm_ignore">mm_ignore</A></H4>
 * <DL>
 * <DT>fromperson
 * <DD>the person who is ignoring someone
 * <DT>toperson
 * <DD>the person that is being ignored.
 * </DL>
 *
 * <A NAME="mm_help">
 * <A NAME="mm_log">
 * <A NAME="mm_oldlog">
 *
 * <A NAME="mm_boards">
 * <H4><A HREF="#contents"><A NAME="mm_boardmessages">mm_boardmessages</A></H4>
 * <DL>
 * <DT>boardid
 * <DD>indicates on which board this message belongs, see mm_boards.
 * <DT>name
 * <DD>the person posting a message
 * <DT>posttime
 * <DD>timestamp on whn the person posted the message
 * <DT>message
 * <DD>the message itself
 * <DT>removed
 * <DD>0 or 1, 1 if message has been removed for offending content by a deputy
 * </DL>
 *
 * @startuml package-info_gameentities.png
 * title Figure 1. Relationships between Primary Tables
 *
 * mm_area "1" *-- "many" mm_rooms : contains
 * mm_rooms "1" *-- "many" mm_usertable : contains
 * mm_mailtable "name" -- "name" mm_usertable : contains
 * mm_mailtable "toname" -- "name" mm_usertable : contains
 * mm_boards "1" *-- "many" mm_boardmessages : contains
 * mm_boardmessages "1" *-- "1" mm_usertable : contains
 * mm_guilds "1" *-- "many" mm_usertable : contains
 * mm_guilds "1" *-- "many" mm_guildranks : contains
 * mm_ignore "1" *-- "2" mm_usertable : contains
 * mm_answers "many" *-- "1" mm_usertable : contains
 * mm_commands "many" *-- "1" mm_methods : uses
 * mm_events "many" *-- "1" mm_methods : uses
 * class mm_usertable {
 * -id
 * -name
 * -description
 * -creation_date
 * -parent_id
 * -highlight
 * -sortorder
 * }
 * class mm_rooms {
 * -id
 * -name
 * -description
 * -gallery_id
 * -photograph_id
 * -sortorder
 * }
 * class mm_items {
 * -id
 * -wieldable
 * -wearable
 * -container
 * -getable
 * -dropable
 * -visible
 * -owner
 * -creation
 * -capacity
 * -isopenable
 * -keyid
 * -containtype
 * }
 * class mm_methods {
 * -name
 * -src
 * -owner
 * -creation
 * }
 * class mm_boardmessages {
 * -boardid
 * -name
 * -posttime
 * -message
 * -removed
 * }
 * class mm_guilds {
 * -name
 * -title
 * -daysguilddeath
 * -maxguilddeath
 * -minguildmembers
 * -minguildlevel
 * -guilddescription
 * -guildurl
 * -bossname
 * -active
 * -creation
 * -owner
 * -logonmessage
 * }
 * class mm_guildranks {
 * -title
 * -guildlevel
 * -guildname
 * -accept_access
 * -reject_access
 * -settings_access
 * -logonmessage_access
 * }
 * class mm_boards {
 * -id
 * -name
 * -description
 * -owner
 * -creation
 * }
 * class mm_oldlog {
 * -creation
 * -name
 * -message
 * }
 * class mm_log {
 * -creation
 * -name
 * -message
 * }
 * class mm_help {
 * }
 * class mm_ignore {
 * -fromperson
 * -toperson
 * }
 * class mm_answers {
 * -name
 * -question
 * -answer
 * }
 * class mm_errormessages {
 * }
 * class mm_bantable {
 * -address
 * -days
 * -IP
 * -name
 * -deputy
 * -date
 * -reason
 * }
 * class mm_unbantable {
 * -name
 * }
 * class mm_bannednamestable {
 * -name
 * -deputy
 * -creation
 * -days
 * -reason
 * }
 * class mm_sillynamestable {
 * -name
 * }
 * class mm_mailtable {
 * -id
 * -name
 * -toname
 * -subject
 * -whensent
 * -haveread
 * -newmail
 * -body
 * -deleted
 * -item_id
 * }
 * class mm_macro {
 * -id
 * -name
 * -macroname
 * -contents
 * }
 * class mm_usertable {
 * -name
 * -password
 * -god
 * -alignment
 * -movementstats
 * -notes
 * }
 * class mm_commandlog {
 * -id
 * -stamp
 * -name
 * -command
 * }
 * class mm_area {
 * -area
 * -description
 * -shortdesc
 * -owner
 * -creation
 * }
 * class mm_itemitemtable {
 * -id
 * -containerid
 * }
 * class mm_rooms {
 * -id
 * -west
 * -east
 * -north
 * -south
 * -up
 * -down
 * -contents
 * -owner
 * -creation
 * -area
 * -title
 * -picture
 * }
 * class mm_roomitemtable {
 * -id
 * -room
 * -search
 * }
 * class mm_charitemtable {
 * -id
 * -belongsto
 * -wearing
 * }
 * class mm_shopkeeperitems {
 * -id
 * -max
 * -charname
 * }
 * class mm_itemtable {
 * -id
 * -itemid
 * }
 * class mm_events {
 * -eventid
 * -name
 * -month
 * -dayofmonth
 * -hour
 * -minute
 * -dayofweek
 * -callable
 * -method_name
 * -room
 * -owner
 * -creation
 * }
 * class mm_itemattributes {
 * -name
 * -value
 * -value_type
 * -id
 * }
 * class mm_charattributes {
 * -name
 * -value
 * -value_type
 * -charname
 * }
 * class mm_roomattributes {
 * -name
 * -value
 * -value_type
 * -id
 * }
 * class mm_commands {
 * -id
 * -callable
 * -command
 * -method_name
 * -room
 * -owner
 * -creation
 * }
 * class mm_admin {
 * -name
 * -passwd
 * -ip
 * -created
 * -validuntil
 * -email
 * }
 * @enduml
 */
package mmud.database.entities.game;
