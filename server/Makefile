#
# MakeFile -- Maarten's Mud Makefile for compiling the binaries
#
# javac -source 1.4 -classpath
# ../../mysql-connector-java-3.0.6-stable/mysql-connector-java-3.0.6-stable-bin.jar:../../jakarta-oro-2.0.7/jakarta-oro-2.0.7.jar:.
# *.java

# Directory for installing the mud-cgi-binaries 
# ---------------------------------------------
# 	(enter.cgi, newchar.cgi, mudrelogin.cgi, mud.cgi)

# INSTALLDIR = /home/karchan/.html/cgi-bin/
INSTALLDIR = /home/httpd/cgi-bin/

#
# Options that can be used to the make command
# --------------------------------------------
#
# make						(normal compilation of all files)
# make install		(installation og binaries into the correct dir)
# make backup			(makes backup of the binaries from *.cgi to *_backup.cgi)
# make restore		(restores the backup that was created, if the backup was present)
# make install_scripts    (created the scripts used on the web and installs them)

#
#------------------------------------------------------------------
# It is not necessary to modify anything below this line,
# unless you know what you are doing.
#

CC = /usr/java/j2sdk1.4.1_02/bin/javac

# CC = javac
# the -g option is to compile debugging info into the object files
#
# COPTS = -g -m486 // option for Linux versions running gcc < 3.0
CLASSPATH = ../../mysql-connector-java-3.0.6-stable/mysql-connector-java-3.0.6-stable-bin.jar:../../jakarta-oro-2.0.7/jakarta-oro-2.0.7.jar:.
COPTS = -source 1.4 -classpath $(CLASSPATH)

TARGETS = Character.class CharacterException.class Characters.class \
Command.class Constants.class Database.class Echo.class Inventory.class \
Item.class ItemException.class Main.class MudException.class MudSocket.class \
Room.class RoomException.class Rooms.class User.class BowCommand.class \
NormalCommand.class MeCommand.class BogusCommand.class EmotionCommand.class \
EmotionToCommand.class QuitCommand.class SleepCommand.class \
AwakenCommand.class AskCommand.class ClearCommand.class DateCommand.class \
TimeCommand.class SouthCommand.class NorthCommand.class WestCommand.class \
EastCommand.class UpCommand.class DownCommand.class GoCommand.class \
HelpCommand.class TellCommand.class SayCommand.class ShoutCommand.class  \
WhisperCommand.class BigTalkCommand.class CurtseyCommand.class \
EyebrowCommand.class WhimpyCommand.class WhoCommand.class \
ListMailCommand.class ReadMailCommand.class DeleteMailCommand.class \
MailCommand.class SendMailCommand.class MailException.class \
PkillCommand.class ItemDef.class ItemDefs.class InventoryCommand.class \
DrinkCommand.class EatCommand.class DropCommand.class GetCommand.class \
GiveCommand.class ReadCommand.class LookCommand.class BuyCommand.class

Character.class: Character.java
	$(CC) $(COPTS)  Character.java

CharacterException.class: CharacterException.java
	$(CC) $(COPTS)  CharacterException.java

MailException.class: MailException.java
	$(CC) $(COPTS)  MailException.java

Characters.class: Characters.java
	$(CC) $(COPTS)  Characters.java

Command.class: Command.java
	$(CC) $(COPTS)  Command.java

NormalCommand.class: NormalCommand.java
	$(CC) $(COPTS)  NormalCommand.java

BowCommand.class: BowCommand.java
	$(CC) $(COPTS)  BowCommand.java

EmotionCommand.class: EmotionCommand.java
	$(CC) $(COPTS)  EmotionCommand.java

EmotionToCommand.class: EmotionToCommand.java
	$(CC) $(COPTS)  EmotionToCommand.java

MeCommand.class: MeCommand.java
	$(CC) $(COPTS)  MeCommand.java

QuitCommand.class: QuitCommand.java
	$(CC) $(COPTS) QuitCommand.java

SleepCommand.class: SleepCommand.java
	$(CC) $(COPTS) SleepCommand.java

AwakenCommand.class: AwakenCommand.java
	$(CC) $(COPTS) AwakenCommand.java

AskCommand.class: AskCommand.java
	$(CC) $(COPTS) AskCommand.java

SayCommand.class: SayCommand.java
	$(CC) $(COPTS) SayCommand.java

TellCommand.class: TellCommand.java
	$(CC) $(COPTS) TellCommand.java

ShoutCommand.class: ShoutCommand.java
	$(CC) $(COPTS) ShoutCommand.java

WhisperCommand.class: WhisperCommand.java
	$(CC) $(COPTS) WhisperCommand.java

ClearCommand.class: ClearCommand.java
	$(CC) $(COPTS) ClearCommand.java

DateCommand.class: DateCommand.java
	$(CC) $(COPTS) DateCommand.java

TimeCommand.class: TimeCommand.java
	$(CC) $(COPTS) TimeCommand.java

SouthCommand.class: SouthCommand.java
	$(CC) $(COPTS) SouthCommand.java

NorthCommand.class: NorthCommand.java
	$(CC) $(COPTS) NorthCommand.java

WestCommand.class: WestCommand.java
	$(CC) $(COPTS) WestCommand.java

EastCommand.class: EastCommand.java
	$(CC) $(COPTS) EastCommand.java

UpCommand.class: UpCommand.java
	$(CC) $(COPTS) UpCommand.java

DownCommand.class: DownCommand.java
	$(CC) $(COPTS) DownCommand.java

GoCommand.class: GoCommand.java
	$(CC) $(COPTS) GoCommand.java

HelpCommand.class: HelpCommand.java
	$(CC) $(COPTS) HelpCommand.java

BigTalkCommand.class: BigTalkCommand.java
	$(CC) $(COPTS) BigTalkCommand.java

CurtseyCommand.class: CurtseyCommand.java
	$(CC) $(COPTS) CurtseyCommand.java

EyebrowCommand.class: EyebrowCommand.java
	$(CC) $(COPTS) EyebrowCommand.java

WhimpyCommand.class: WhimpyCommand.java
	$(CC) $(COPTS) WhimpyCommand.java

WhoCommand.class: WhoCommand.java
	$(CC) $(COPTS) WhoCommand.java

ListMailCommand.class: ListMailCommand.java
	$(CC) $(COPTS) ListMailCommand.java

ReadMailCommand.class: ReadMailCommand.java
	$(CC) $(COPTS) ReadMailCommand.java

DeleteMailCommand.class: DeleteMailCommand.java
	$(CC) $(COPTS) DeleteMailCommand.java

MailCommand.class: MailCommand.java
	$(CC) $(COPTS) MailCommand.java

SendMailCommand.class: SendMailCommand.java
	$(CC) $(COPTS) SendMailCommand.java

PkillCommand.class: PkillCommand.java
	$(CC) $(COPTS)  PkillCommand.java

InventoryCommand.class: InventoryCommand.java
	$(CC) $(COPTS)  InventoryCommand.java

DrinkCommand.class: DrinkCommand.java
	$(CC) $(COPTS)  DrinkCommand.java

EatCommand.class: EatCommand.java
	$(CC) $(COPTS)  EatCommand.java

GetCommand.class: GetCommand.java
	$(CC) $(COPTS)  GetCommand.java

DropCommand.class: DropCommand.java
	$(CC) $(COPTS)  DropCommand.java

GiveCommand.class: GiveCommand.java
	$(CC) $(COPTS)  GiveCommand.java

ReadCommand.class: ReadCommand.java
	$(CC) $(COPTS)  ReadCommand.java

LookCommand.class: LookCommand.java
	$(CC) $(COPTS)  LookCommand.java

BuyCommand.class: BuyCommand.java
	$(CC) $(COPTS)  BuyCommand.java

BogusCommand.class: BogusCommand.java
	$(CC) $(COPTS)  BogusCommand.java

Constants.class: Constants.java
	$(CC) $(COPTS)  Constants.java

Database.class: Database.java
	$(CC) $(COPTS)  Database.java

Echo.class: Echo.java
	$(CC) $(COPTS)  Echo.java

Inventory.class: Inventory.java
	$(CC) $(COPTS)  Inventory.java

Item.class: Item.java
	$(CC) $(COPTS)  Item.java

ItemDef.class: ItemDef.java
	$(CC) $(COPTS)  ItemDef.java

ItemDefs.class: ItemDefs.java
	$(CC) $(COPTS)  ItemDefs.java

ItemException.class: ItemException.java
	$(CC) $(COPTS)  ItemException.java

Main.class: Main.java
	$(CC) $(COPTS)  Main.java

MudException.class: MudException.java
	$(CC) $(COPTS)  MudException.java

MudSocket.class: MudSocket.java
	$(CC) $(COPTS)  MudSocket.java

Room.class: Room.java
	$(CC) $(COPTS)  Room.java

RoomException.class: RoomException.java
	$(CC) $(COPTS)  RoomException.java

Rooms.class: Rooms.java
	$(CC) $(COPTS)  Rooms.java

User.class: User.java
	$(CC) $(COPTS)  User.java

javadoc:
	cd html; javadoc $(COPTS) ../*.java

clean:
	rm *.class *~

all: $(TARGETS)

