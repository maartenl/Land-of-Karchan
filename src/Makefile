#
# MakeFile -- Maarten's Mud Makefile for compiling the binaries
#
# gcc mmserver.c `/usr/bin/xml-config --cflags` `/usr/bin/xml-config --libs` -I/usr/include/mysql/ -I../include/ -lm -l$(MYSQLLIB) -lefence 

# Directory for installing the mud-cgi-binaries 
# ---------------------------------------------
# 	(enter.cgi, newchar.cgi, mudrelogin.cgi, mud.cgi)

# INSTALLDIR = /home/karchan/.html/cgi-bin/
INSTALLDIR = /home/httpd/cgi-bin/

# MYSQL CLIENT LIBRARY
# 
# MYSQLLIB = mysqlclient
# (do NOT use the value above! It is not thread safe)
MYSQLLIB = mysqlclient_r

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
# Option for debugging
# --------------------
# the DEBUG can be set to an integer value.
# for instance: '-DDEBUG=1' and consequently recompiling the mud source code
# will yield a binary with level 1 debug messages added.
#
# the MEMMAN can be set to indicate that memory checking should be active
# when the program is terminated, a list is dumped on standard output
# on which variables are still available in memory and have not cleared
# properly.
DEBUG = 
#-DMEMMAN
# -DDEBUG


#
#------------------------------------------------------------------
# It is not necessary to modify anything below this line,
# unless you know what you are doing.
#

# cc -I/usr/include/mysql/ -L/usr/lib/mysql/ prog.c -o prog -lm
# 			-l$(MYSQLLIB)
#
# $(OPTIONS) $(LIBDIR) $(INCLUDES)

CC = gcc
# the -g option is to compile debugging info into the object files
#
# COPTS = -g -m486 // option for Linux versions running gcc < 3.0
COPTS = -O2 -mcpu=i486 -Wall

# this header is for RPM installations of mysql
#
# INCLUDES = -I../include -idirafter /usr/include/mysql/ 
# this header is for Source installations of mysql
#
INCLUDES = -I../include -I/usr/include/mysql/ 
XMLINCLUDES = `/usr/bin/xml2-config --cflags`

EXEDIR = /home/httpd/cgi-bin
# this header is for RPM installations of mysql
#
# LIBDIR = -L/usr/lib/mysql/ -lm -l$(MYSQLLIB)
# this header is for Source installations of mysql
#
# the -lefence option is strictly for debugging purposes
# it enables ElectricFence which is used to detect
# general memory allocation errors (malloc)
#
LIBDIR = -L/usr/lib/mysql/ -lm -l$(MYSQLLIB) -lz -lpthread
# you can use xml-config, however, when two xml libs exists, it will
# pick the oldest one
XMLLIBDIR = `/usr/bin/xml2-config --libs`

TARGETS = mmserver mud events newchar enter addendumframe leftframe logonframe \
nph-addendum nph-logonframe nph-leftframe nph-javascriptframe \
 charactersheet editcharactersheet submitcharactersheet clearcharacters

all: $(TARGETS)
	strip $(TARGETS)

cgi-util.o: cgi-util.c
	$(CC) $(COPTS) $(INCLUDES) -c -o cgi-util.o cgi-util.c

mudmain.o: mudmain.c
	$(CC) $(COPTS) $(INCLUDES) -c mudmain.c $(DEBUG)

mudlogon.o: mudlogon.c
	$(CC) $(COPTS) $(INCLUDES) -c mudlogon.c $(DEBUG)

mudnewchar.o: mudnewchar.c
	$(CC) $(COPTS) $(INCLUDES) -c mudnewchar.c $(DEBUG)

mud.o: mud.c
	$(CC) $(COPTS) $(INCLUDES) $(XMLINCLUDES) -c mud.c $(DEBUG)

events.o: events.c
	$(CC) $(COPTS) $(INCLUDES) -c events.c $(DEBUG)

newchar.o: newchar.c
	$(CC) $(COPTS) $(INCLUDES) $(XMLINCLUDES) -c newchar.c $(DEBUG)

enter.o: enter.c
	$(CC) $(COPTS) $(INCLUDES) $(XMLINCLUDES) -c enter.c $(DEBUG)

mmserver.o: mmserver.c
	$(CC) $(COPTS) $(INCLUDES) $(XMLINCLUDES) -c mmserver.c $(DEBUG)

# has become obsolete
# mudrelogin.o: mudrelogin.c
#	$(CC) $(COPTS) $(INCLUDES) -c mudrelogin.c $(DEBUG)

mud-lib.o: mud-lib.c
	$(CC) $(COPTS) $(INCLUDES) -c mud-lib.c $(DEBUG)

guild.o: guild.c
	$(CC) $(COPTS) $(INCLUDES) -c guild.c $(DEBUG)

mud-lib2.o: mud-lib2.c
	$(CC) $(COPTS) $(INCLUDES) -c mud-lib2.c $(DEBUG)

mud-lib3.o: mud-lib3.c
	$(CC) $(COPTS) $(INCLUDES) -c mud-lib3.c $(DEBUG)

parser.o: parser.c 
	$(CC) $(COPTS) $(INCLUDES) -c parser.c $(DEBUG)

userlib.o: userlib.c
	$(CC) $(COPTS) $(INCLUDES) -c userlib.c $(DEBUG)

typedefs.o: typedefs.c 
	$(CC) $(COPTS) $(INCLUDES) $(XMLINCLUDES) -c typedefs.c $(DEBUG)

#--- Binary executables are compiled below...

enter: enter.o cgi-util.o 
	$(CC) $(COPTS) -o enter cgi-util.o enter.o $(LIBDIR) $(XMLLIBDIR) 

# has become obsolete
#mudrelogin: mudrelogin.o mud-lib.o typedefs.o userlib.o cgi-util.o mudrelogin.c
#	$(CC) $(COPTS) -o mudrelogin cgi-util.o typedefs.o userlib.o mud-lib.o mudrelogin.o $(LIBDIR)

newchar: newchar.o cgi-util.o newchar.c
	$(CC) $(COPTS) -o newchar cgi-util.o newchar.o $(LIBDIR) $(XMLLIBDIR) 

mud: mud.o cgi-util.o
	$(CC) $(COPTS) -o mud cgi-util.o mud.o $(LIBDIR) $(XMLLIBDIR) 

mmserver: mmserver.o mudmain.o mudlogon.o mudnewchar.o typedefs.o userlib.o mud-lib.o mud-lib2.o mud-lib3.o parser.o guild.o 
	$(CC) $(COPTS) -o mmserver mmserver.o mudmain.o mudlogon.o mudnewchar.o typedefs.o userlib.o mud-lib.o guild.o mud-lib2.o mud-lib3.o parser.o $(LIBDIR) $(XMLLIBDIR)

events: events.o typedefs.o userlib.o mud-lib.o mud-lib2.o mud-lib3.o parser.o guild.c cgi-util.o 
	$(CC) $(COPTS) -o events cgi-util.o typedefs.o userlib.o mud-lib.o guild.o mud-lib2.o mud-lib3.o parser.o events.o $(LIBDIR) $(XMLLIBDIR)

#--- Compilation of frame-binaries

addendumframe.o: addendumframe.c 
	$(CC) $(COPTS) $(INCLUDES) -c addendumframe.c

addendumframe: addendumframe.o cgi-util.o
	$(CC) $(COPTS) -o addendumframe cgi-util.o addendumframe.o $(LIBDIR) 

leftframe.o: leftframe.c
	$(CC) $(COPTS) $(INCLUDES) -c leftframe.c

leftframe: leftframe.o cgi-util.o
	$(CC) $(COPTS) -o leftframe cgi-util.o leftframe.o $(LIBDIR)

logonframe.o: logonframe.c
	$(CC) $(COPTS) $(INCLUDES) -c logonframe.c

logonframe: logonframe.o cgi-util.o 
	$(CC) $(COPTS) -o logonframe cgi-util.o logonframe.o $(LIBDIR)

#--- Compilation of javascript/serverpush/frame-binaries

nph-addendum.o: nph-addendum.c
	$(CC) $(COPTS) $(INCLUDES) -c nph-addendum.c

nph-addendum: nph-addendum.o cgi-util.o typedefs.o
	$(CC) $(COPTS) -o nph-addendum cgi-util.o typedefs.o nph-addendum.o $(LIBDIR) $(XMLLIBDIR)

nph-logonframe.o: nph-logonframe.c
	$(CC) $(COPTS) $(INCLUDES) -c nph-logonframe.c

nph-logonframe: nph-logonframe.o cgi-util.o
	$(CC) $(COPTS) -o nph-logonframe cgi-util.o nph-logonframe.o $(LIBDIR)

nph-leftframe.o: nph-leftframe.c
	$(CC) $(COPTS) $(INCLUDES) -c nph-leftframe.c

nph-leftframe: nph-leftframe.o cgi-util.o 
	$(CC) $(COPTS) -o nph-leftframe cgi-util.o nph-leftframe.o $(LIBDIR)

nph-javascriptframe.o: nph-javascriptframe.c
	$(CC) $(COPTS) $(INCLUDES) -c nph-javascriptframe.c

nph-javascriptframe: nph-javascriptframe.o cgi-util.o 
	$(CC) $(COPTS) -o nph-javascriptframe cgi-util.o nph-javascriptframe.o $(LIBDIR)

#--- Compilation and installing of small website binaries

charactersheet.o: charactersheet.c
	$(CC) $(COPTS) $(INCLUDES) -c charactersheet.c 

charactersheet: charactersheet.o cgi-util.o typedefs.o
	$(CC) $(COPTS) -o charactersheet cgi-util.o charactersheet.o typedefs.o $(LIBDIR) $(XMLLIBDIR)

editcharactersheet.o: editcharactersheet.c
	$(CC) $(COPTS) $(INCLUDES) -c editcharactersheet.c 

editcharactersheet: editcharactersheet.o cgi-util.o typedefs.o
	$(CC) $(COPTS) -o editcharactersheet cgi-util.o editcharactersheet.o typedefs.o $(LIBDIR) $(XMLLIBDIR)

submitcharactersheet.o: submitcharactersheet.c
	$(CC) $(COPTS) $(INCLUDES) -c submitcharactersheet.c 

submitcharactersheet: submitcharactersheet.o cgi-util.o typedefs.o 
	$(CC) $(COPTS) -o submitcharactersheet cgi-util.o submitcharactersheet.o typedefs.o $(LIBDIR) $(XMLLIBDIR)

clearcharacters.o: clearcharacters.c
	$(CC) $(COPTS) $(INCLUDES) -c clearcharacters.c 

clearcharacters: clearcharacters.o typedefs.o
	$(CC) $(COPTS) -o clearcharacters clearcharacters.o typedefs.o $(LIBDIR) $(XMLLIBDIR)

#--- Clean up the stuff and install as well

clean:
	rm -f *.o *.backup *~ $(TARGETS)

backup: all
	@echo "Backing up Mud Binaries..."
	@echo "  Backing up Central Mud CGI..."
	@cp $(INSTALLDIR)mud.cgi $(INSTALLDIR)mud_backup.cgi
#	@cp $(INSTALLDIR)mudrelogin.cgi $(INSTALLDIR)mudrelogin_backup.cgi
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
#	@cp $(INSTALLDIR)mudrelogin_backup.cgi $(INSTALLDIR)mudrelogin.cgi 
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
#	@cp mudrelogin $(INSTALLDIR)mudrelogin.cgi
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
	@cp charactersheet $(INSTALLDIR)charactersheet.cgi
	@cp editcharactersheet $(INSTALLDIR)editcharactersheet.cgi
	@cp submitcharactersheet $(INSTALLDIR)submitcharactersheet.cgi
	@echo "Setting UIDs for Mud Script Binaries..."
	@chmod o=x $(INSTALLDIR)*
	@chmod u=rwx $(INSTALLDIR)*
	@chmod g= $(INSTALLDIR)*
	@chmod a+s $(INSTALLDIR)*
	@echo "Installation of Single Mud Scripts done."

