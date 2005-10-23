#
# MakeFile -- Maarten's Mud Makefile for compiling the java server
#
# javac -source 1.4 -classpath
# *.java

HOMEDIR = /home/karchan/mmud/server
INSTALLDIR = $(HOMEDIR)/jclasses
SOURCEDIR = $(HOMEDIR)
JAVADOCDIR = /var/www/html/javadoc
JAVADIR = /usr/java/j2sdk1.4.2_09/bin

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

CC = $(JAVADIR)/javac
JAVA = $(JAVADIR)/java
JAVADOC = $(JAVADIR)/javadoc
JAVAJAR = $(JAVADIR)/jar

# CC = javac
# the -g option is to compile debugging info into the object files
#
# COPTS = -g -m486 // option for Linux versions running gcc < 3.0
CLASSPATH =  mysql-connector-java-3.1.7-bin.jar:Simkin.jar:runme.jar
CLASSPATH2 =  mysql-connector-java-3.1.7-bin.jar:Simkin.jar
COPTS = -source 1.4 -classpath $(CLASSPATH2) -d $(INSTALLDIR) -sourcepath $(SOURCEDIR)

TARGETS = compile javadoc release

compile: mmud/*.java
	$(CC) $(COPTS) mmud/*.java mmud/items/*.java mmud/commands/*.java \
	mmud/rooms/*.java mmud/characters/*.java mmud/database/*.java \
	mmud/boards/*.java mmud/races/*.java
#    mmud/communication/*.java mmud/configuration/*.java

javadoc: compile
	$(JAVADOC) -source 1.4 -private -d \
	$(JAVADOCDIR) \
	mmud mmud.items mmud.commands mmud.rooms mmud.characters mmud.database \
	mmud.boards mmud.races

clean:
	rm -rf jclasses/* *~ mmud/*~ mmud/items/*~ mmud/commands/*~ \
	mmud/rooms/*~ mmud/characters/*~ mmud/database/*~ \
	mmud/boards/*~ mmud/races/*~ \
	mmud/communication/*~ mmud/configuration/*~

dist-clean:
	rm -rf html/*

all: $(TARGETS)

jar:
	cd jclasses;$(JAVAJAR) -cmvf ../MANIFEST.MF ../runme.jar -C . .

html:
	$(JAVA) -jar j2h.jar -d /home/httpd/html/javasrc/ -js . -jd /home/httpd/html/javadoc

run: jar
	$(JAVA) -cp $(CLASSPATH) mmud.Main

release: clean compile jar javadoc
	echo "Release done."
