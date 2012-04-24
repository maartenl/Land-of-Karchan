#!/bin/sh

. ../mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# begin transaction
#
START TRANSACTION;

#
# drop trigger, if it exists
#
DROP TRIGGER IF EXISTS password_trigger;

#
# create the trigger
#

CREATE TRIGGER password_trigger BEFORE INSERT ON mm_usertable
FOR EACH ROW
    SET NEW.password = sha1(NEW.password);

#
# commit transaction
#
COMMIT;

END_OF_DATA
