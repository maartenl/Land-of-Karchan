#!/bin/sh

. ./mysql_constants

echo "Starting database setup using filename <$1>"

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# Disable foreign keys, load database, enable foreign keys
#

SET FOREIGN_KEY_CHECKS=0;
SET AUTOCOMMIT=1;
source $1;
SET FOREIGN_KEY_CHECKS=1;

END_OF_DATA

