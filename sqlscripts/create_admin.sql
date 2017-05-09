#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} -p -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done for new admins
#

insert into mm_admin
(name, passwd, created, validuntil, email)
values("Meredin", sha1("48ybab68nab6a4klkwedc"), now(), now(), "jdland01@yahoo.com");

END_OF_DATA

