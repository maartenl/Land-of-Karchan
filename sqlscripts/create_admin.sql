#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# This has to be done for new admins
#

insert into mm_admin
(name, passwd, ip, created, validuntil, email)
values("Aurican", sha1("48ybab68nab6a4klkwedc"), now(), now(), "r.zwarycz@gmail.com");

END_OF_DATA

