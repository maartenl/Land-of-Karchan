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
CLASSPATH = ../../mysql-connector-java-3.0.6-stable/mysql-connector-java-3.0.6-stable-bin.jar:../../jakarta-oro-2.0.7/jakarta-oro-2.0.7.jar:jclasses
COPTS = -source 1.4 -classpath $(CLASSPATH) -d jclasses

TARGETS = compile

compile: *.java
	$(CC) $(COPTS) *.java

javadoc:
	cd html; javadoc $(COPTS) ../*.java

clean:
	rm jclasses/* *~

all: $(TARGETS)

jars: $(TARGETS)
	cd jclasses;jar -cvf ../runme.jar -C . .

run:
	/usr/java/j2sdk1.4.1_02/bin/java -cp $(CLASSPATH) Main
