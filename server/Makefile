#
# MakeFile -- Maarten's Mud Makefile for compiling the java server
#
# javac -source 1.4 -classpath
# ../../mysql-connector-java-3.0.6-stable/mysql-connector-java-3.0.6-stable-bin.jar:../../jakarta-oro-2.0.7/jakarta-oro-2.0.7.jar:.
# *.java

HOMEDIR = /home/karchan/cvs2/server
INSTALLDIR = $(HOMEDIR)/jclasses
SOURCEDIR = $(HOMEDIR)
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
CLASSPATH = mysql-connector-java-3.0.6-stable-bin.jar:jakarta-oro-2.0.7.jar:jclasses
COPTS = -source 1.4 -classpath $(CLASSPATH) -d $(INSTALLDIR) -sourcepath $(SOURCEDIR)

TARGETS = compile

compile: mmud/*.java
	$(CC) $(COPTS) mmud/*.java mmud/items/*.java mmud/commands/*.java \
    mmud/rooms/*.java mmud/characters/*.java mmud/database/*.java 
#    mmud/communication/*.java mmud/configuration/*.java

javadoc:
	cd html; javadoc $(COPTS) ../mmud/*.java 

clean:
	rm -rf jclasses/* *~ mmud/*~ mmud/items/*~ mmud/commands/*~ \
    mmud/rooms/*~ mmud/characters/*~ mmud/database/*~ \
    mmud/communication/*~ mmud/configuration/*~

all: $(TARGETS)

jar:
	cd jclasses;jar -cmvf ../MANIFEST.MF ../runme.jar -C . .

run:
	/usr/java/j2sdk1.4.1_02/bin/java -cp $(CLASSPATH) -jar runme.jar
