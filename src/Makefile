#	@cp mud $(INSTALLDIR)mud2.cgi
#
# MakeFile -- Maarten's Mud Makefile for compiling the binaries
#

# Directory for installing the mud-cgi-binaries 
# 	(enter.cgi, newchar.cgi, mudrelogin.cgi, mud.cgi)

# INSTALLDIR = /home/karchan/.html/cgi-bin/
INSTALLDIR = /home/httpd/cgi-bin/
#
# Options that can be used to the make command
#
# make						(normal compilation of all files)
# make install		(installation og binaries into the correct dir)
# make backup			(makes backup of the binaries from *.cgi to *_backup.cgi)
# make restore		(restores the backup that was created, if the backup was present)

#------------------------------------------------------------------
# It is not necessary to modify anything below this line,
# unless you know what you are doing.
#

# cc -I/usr/include/mysql/ -L/usr/lib/mysql/ prog.c -o prog -lm
# 			-lmysqlclient
#
# $(OPTIONS) $(LIBDIR) $(INCLUDES)

CC = gcc
COPTS = -O2 -m486
INCLUDES = -I../include -idirafter /usr/include/mysql/ 

EXEDIR = /home/httpd/cgi-bin
LIBDIR = -L/usr/local/lib/mysql/ 
OPTIONS = -lm -lmysqlclient 

TARGETS = mud events mudrelogin newchar enter addendumframe leftframe logonframe \
nph-addendum nph-logonframe nph-leftframe nph-javascriptframe rolls2 \
wholist charactersheet charactersheets editcharactersheet submitcharactersheet clearcharacters

all: $(TARGETS)
	strip $(TARGETS)

cgic.o: cgic.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c cgic.c

mud.o: mud.c mud-lib2.o mud-lib3.o parser.o mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mud.c 

events.o: events.c mud-lib2.o mud-lib3.o parser.o mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c events.c 

newchar.o: newchar.c mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c newchar.c 

enter.o: enter.c mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c enter.c 

rolls2: rolls.o typedefs.o userlib.o rolls.c
	$(CC) $(COPTS) -o rolls2 typedefs.o userlib.o rolls.o $(LIBDIR) $(OPTIONS)

rolls.o: rolls.c userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c rolls.c 

mudrelogin.o: mudrelogin.c mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mudrelogin.c 

mud-lib.o: mud-lib.c typedefs.o userlib.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mud-lib.c 

guild.o: guild.c mud-lib.o typedefs.o userlib.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c guild.c 

mud-lib2.o: mud-lib2.c guild.o mud-lib.o typedefs.o userlib.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mud-lib2.c 

mud-lib3.o: mud-lib3.c parser.o mud-lib2.o guild.o mud-lib.o typedefs.o userlib.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mud-lib3.c

parser.o: parser.c mud-lib2.o guild.o mud-lib.o typedefs.o userlib.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c parser.c

userlib.o: userlib.c typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c userlib.c 

typedefs.o: typedefs.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c typedefs.c 

mudclient.o: cgic.o mudclient.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mudclient.c

mudserver.o: mudserver.c mud-lib2.o parser.o mud-lib3.o mud-lib.o userlib.o typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c mudserver.c 

#--- Binary executables are compiled below...

mudclient: cgic.o mudclient.o
	$(CC) $(COPTS) -o mudclient cgic.o mudclient.o

enter: enter.o mud-lib.o typedefs.o userlib.o cgic.o enter.c
	$(CC) $(COPTS) -o enter cgic.o typedefs.o userlib.o mud-lib.o enter.o $(LIBDIR) $(OPTIONS)

mudrelogin: mudrelogin.o mud-lib.o typedefs.o userlib.o cgic.o mudrelogin.c
	$(CC) $(COPTS) -o mudrelogin cgic.o typedefs.o userlib.o mud-lib.o mudrelogin.o $(LIBDIR) $(INCLUDES) $(OPTIONS)

newchar: newchar.o mud-lib.o typedefs.o userlib.o cgic.o newchar.c 
	$(CC) $(COPTS) -o newchar cgic.o typedefs.o userlib.o mud-lib.o newchar.o  $(LIBDIR) $(OPTIONS)

mud: mud.o typedefs.o userlib.o mud-lib.o mud-lib2.o mud-lib3.o parser.o guild.c cgic.o mud.c enter newchar 
	$(CC) $(COPTS) -o mud cgic.o typedefs.o userlib.o mud-lib.o guild.o mud-lib2.o mud-lib3.o parser.o mud.o $(LIBDIR) $(OPTIONS) 

events: events.o typedefs.o userlib.o mud-lib.o mud-lib2.o mud-lib3.o parser.o guild.c cgic.o events.c
	$(CC) $(COPTS) -o events cgic.o typedefs.o userlib.o mud-lib.o guild.o mud-lib2.o mud-lib3.o parser.o events.o $(LIBDIR) $(OPTIONS) 

mudserver: mudserver.o typedefs.o userlib.o mud-lib.o mud-lib2.o mud-lib3.o parser.o guild.c cgic.o mudserver.c enter newchar 
	$(CC) $(COPTS) -o mudserver cgic.o typedefs.o userlib.o mud-lib.o guild.o mud-lib2.o mud-lib3.o parser.o mudserver.o $(LIBDIR) $(OPTIONS) 

#--- Compilation of frame-binaries

addendumframe.o: addendumframe.c 
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c addendumframe.c  -o addendumframe.o

addendumframe: addendumframe.o cgic.o
	$(CC) $(COPTS) -o addendumframe cgic.o addendumframe.o $(LIBDIR) $(OPTIONS) $(INCLUDES)

leftframe.o: leftframe.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c leftframe.c -o leftframe.o

leftframe: leftframe.o cgic.o typedefs.o
	$(CC) $(COPTS) -o leftframe cgic.o typedefs.o leftframe.o $(LIBDIR) $(OPTIONS)

logonframe.o: cgic.o logonframe.c typedefs.o
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c logonframe.c -o logonframe.o

logonframe: logonframe.o cgic.o logonframe.c typedefs.o
	$(CC) $(COPTS) -o logonframe cgic.o typedefs.o logonframe.o $(LIBDIR) $(OPTIONS)

#--- Compilation of javascript/serverpush/frame-binaries

nph-addendum.o: cgic.o typedefs.o nph-addendum.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c nph-addendum.c -o nph-addendum.o 

nph-addendum: nph-addendum.o cgic.o typedefs.o
	$(CC) $(COPTS) -o nph-addendum cgic.o typedefs.o nph-addendum.o $(LIBDIR) $(OPTIONS)

nph-logonframe.o: nph-logonframe.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c nph-logonframe.c -o nph-logonframe.o

nph-logonframe: nph-logonframe.o cgic.o typedefs.o
	$(CC) $(COPTS) -o nph-logonframe cgic.o typedefs.o nph-logonframe.o $(LIBDIR) $(OPTIONS)

nph-leftframe.o: nph-leftframe.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c nph-leftframe.c -o nph-leftframe.o

nph-leftframe: nph-leftframe.o cgic.o typedefs.o
	$(CC) $(COPTS) -o nph-leftframe cgic.o typedefs.o nph-leftframe.o $(LIBDIR) $(OPTIONS)

nph-javascriptframe.o: nph-javascriptframe.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c nph-javascriptframe.c -o nph-javascriptframe.o

nph-javascriptframe: nph-javascriptframe.o cgic.o typedefs.o
	$(CC) $(COPTS) -o nph-javascriptframe cgic.o typedefs.o nph-javascriptframe.o $(LIBDIR) $(OPTIONS)

#--- Compilation and installing of small website binaries like the wholist

wholist.o: wholist.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c wholist.c 

wholist: wholist.o
	$(CC) $(COPTS) -o wholist wholist.o $(INCLUDES) $(LIBDIR) $(OPTIONS)

charactersheet.o: charactersheet.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c charactersheet.c 

charactersheet: charactersheet.o cgic.o
	$(CC) $(COPTS) -o charactersheet cgic.o charactersheet.o $(INCLUDES) $(LIBDIR) $(OPTIONS)

charactersheets.o: charactersheets.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c charactersheets.c 

charactersheets: charactersheets.o cgic.o
	$(CC) $(COPTS) -o charactersheets cgic.o charactersheets.o $(INCLUDES) $(LIBDIR) $(OPTIONS)

editcharactersheet.o: editcharactersheet.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c editcharactersheet.c 

editcharactersheet: editcharactersheet.o cgic.o
	$(CC) $(COPTS) -o editcharactersheet cgic.o editcharactersheet.o $(INCLUDES) $(LIBDIR) $(OPTIONS)

submitcharactersheet.o: submitcharactersheet.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c submitcharactersheet.c 

submitcharactersheet: submitcharactersheet.o cgic.o
	$(CC) $(COPTS) -o submitcharactersheet cgic.o submitcharactersheet.o $(INCLUDES) $(LIBDIR) $(OPTIONS)

clearcharacters.o: clearcharacters.c
	$(CC) $(COPTS) $(INCLUDES) $(LIBDIR) -c clearcharacters.c 

clearcharacters: clearcharacters.o clearcharacters.c
	$(CC) $(COPTS) -o clearcharacters clearcharacters.o $(LIBDIR) $(OPTIONS)

#--- Clean up the stuff and install as well

clean:
	rm -f *.o *.backup *~ $(TARGETS)

backup: all
	@echo "Backing up Mud Binaries..."
	@echo "  Backing up Central Mud CGI..."
	@cp $(INSTALLDIR)mud.cgi $(INSTALLDIR)mud_backup.cgi
	@cp $(INSTALLDIR)mudrelogin.cgi $(INSTALLDIR)mudrelogin_backup.cgi
	@cp $(INSTALLDIR)enter.cgi $(INSTALLDIR)enter_backup.cgi
	@cp $(INSTALLDIR)newchar.cgi $(INSTALLDIR)newchar_backup.cgi
	@echo "  Backing up Frame CGI..."
	@cp $(INSTALLDIR)leftframe.cgi $(INSTALLDIR)leftframe_backup.cgi
	@cp $(INSTALLDIR)logonframe.cgi $(INSTALLDIR)logonframe_backup.cgi
	@echo "  Backing up Frame/Serverpush CGI..."
	@cp $(INSTALLDIR)nph-addendum.cgi $(INSTALLDIR)nph-addendum_backup.cgi
	@cp $(INSTALLDIR)nph-logonframe.cgi $(INSTALLDIR)nph-logonframe_backup.cgi
	@cp $(INSTALLDIR)nph-leftframe.cgi $(INSTALLDIR)nph-leftframe_backup.cgi
	@cp $(INSTALLDIR)nph-javascriptframe.cgi $(INSTALLDIR)nph-javascriptframe_backup.cgi
	@echo "Backup done."

restore: all
	@echo "Restoring backup of Mud Binaries..."
	@echo "  Restoring backup Central Mud CGI..."
	@cp $(INSTALLDIR)mud_backup.cgi $(INSTALLDIR)mud.cgi 
	@cp $(INSTALLDIR)mudrelogin_backup.cgi $(INSTALLDIR)mudrelogin.cgi 
	@cp $(INSTALLDIR)enter_backup.cgi $(INSTALLDIR)enter.cgi 
	@cp $(INSTALLDIR)newchar_backup.cgi $(INSTALLDIR)newchar.cgi 
	@echo "  Restoring backup Frame CGI..."
	@cp $(INSTALLDIR)leftframe_backup.cgi $(INSTALLDIR)leftframe.cgi 
	@cp $(INSTALLDIR)logonframe_backup.cgi $(INSTALLDIR)logonframe.cgi 
	@echo "  Restoring backup Frame/Serverpush CGI..."
	@cp $(INSTALLDIR)nph-addendum_backup.cgi $(INSTALLDIR)nph-addendum.cgi 
	@cp $(INSTALLDIR)nph-logonframe_backup.cgi $(INSTALLDIR)nph-logonframe.cgi 
	@cp $(INSTALLDIR)nph-leftframe_backup.cgi $(INSTALLDIR)nph-leftframe.cgi 
	@cp $(INSTALLDIR)nph-javascriptframe_backup.cgi $(INSTALLDIR)nph-javascriptframe.cgi 
	@echo "Setting UIDs for Mud Binaries..."
	@chmod o=x $(INSTALLDIR)*
	@chmod u=rwx $(INSTALLDIR)*
	@chmod g= $(INSTALLDIR)*
	@chmod a+s $(INSTALLDIR)*
	@echo "Backup restored."

install: all
	@echo "Installing Mud Binaries..."
	@echo "  Installing Central Mud CGI..."
	@cp mud $(INSTALLDIR)mud.cgi
	@cp mudrelogin $(INSTALLDIR)mudrelogin.cgi
	@cp enter $(INSTALLDIR)enter.cgi
	@cp newchar $(INSTALLDIR)newchar.cgi
	@echo "  Installing Frame CGI..."
	@cp leftframe $(INSTALLDIR)leftframe.cgi
	@cp logonframe $(INSTALLDIR)logonframe.cgi
	@echo "  Installing Frame/Serverpush CGI..."
	@cp nph-addendum $(INSTALLDIR)nph-addendum.cgi
	@cp nph-logonframe $(INSTALLDIR)nph-logonframe.cgi
	@cp nph-leftframe $(INSTALLDIR)nph-leftframe.cgi
	@cp nph-javascriptframe $(INSTALLDIR)nph-javascriptframe.cgi
	@echo "Setting UIDs for Mud Binaries..."
	@chmod o=x $(INSTALLDIR)*
	@chmod u=rwx $(INSTALLDIR)*
	@chmod g= $(INSTALLDIR)*
	@chmod a+s $(INSTALLDIR)*
	@echo "Installation done."

install_scripts: all
	@echo "Installing Mud Script Binaries..."
	@echo "  (these are small scripts that add some functionality to the website)"
	@cp wholist $(INSTALLDIR)wholist.cgi
	@cp charactersheet $(INSTALLDIR)charactersheet.cgi
	@cp charactersheets $(INSTALLDIR)charactersheets.cgi
	@cp editcharactersheet $(INSTALLDIR)editcharactersheet.cgi
	@cp submitcharactersheet $(INSTALLDIR)submitcharactersheet.cgi
	@echo "Setting UIDs for Mud Script Binaries..."
	@chmod o=x $(INSTALLDIR)*
	@chmod u=rwx $(INSTALLDIR)*
	@chmod g= $(INSTALLDIR)*
	@chmod a+s $(INSTALLDIR)*
	@echo "Installation of Single Mud Scripts done."

