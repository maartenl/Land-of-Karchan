#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# insert a new area.
#

insert into mm_area
(area, shortdesc, description, owner, creation)
values("Saparta",
"city of Saparta",
"Saparta is a nation that lies west of Karchan.  
The main capital from which the nation takes its name can be found across 
the broad, often dangerous, Adaran Bay.  In our current day, all major 
trade and pedestrian traffic to and from Saparta is by boat and barge.  
The Sapartan harbor is one of the largest and busiest harbors in the world, 
accommodating over one hundred ships each week.  Though the harbor area is 
a extremely diverse place due to the ships and people that flock to it from 
countless other lands, nations, and continents the rest of the city retains 
the imposing strength and ancient culture of its ancestors.  Saparta is 
considered to be the place where the first, true government was formed 
and is the first place for democracy to have been documented.  Traveling 
through the city, one might find the Grand Palace, the National Gardens,
and the Congress Hall as well as the Grand Sapartan Library and many 
other places.",
"Crissy",
now());

END_OF_DATA

