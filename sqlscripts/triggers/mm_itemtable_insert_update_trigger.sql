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
DROP TRIGGER IF EXISTS item_trigger_before_insert;
DROP TRIGGER IF EXISTS item_trigger_before_update;

#
# create the trigger
#

CREATE TRIGGER item_trigger_before_insert BEFORE INSERT ON mm_itemtable
FOR EACH ROW
BEGIN
#if NEW.belongsto = NULL then
#    if NEW.room IS NULL then
#         if NEW.containerid IS NULL then
            signal sqlstate '02234' -- set message_text = 'Cannot put items into the void.';
#         end if;
#    end if;
#end if;
END;

#
# commit transaction
#
COMMIT;

END_OF_DATA
