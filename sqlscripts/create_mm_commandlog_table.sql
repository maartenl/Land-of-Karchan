#!/bin/sh

. ./mysql_constants

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA
#
# this table is always forgotten in backups, as it tends to fill up quickly and is just
# logging.
# NOTE: FOr some reason this script doesn't work, but copy-pasting it in a mysql session does. No clue why.

DROP TABLE IF EXISTS `mm_commandlog`;

CREATE TABLE `mm_commandlog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(20) NOT NULL,
  `command` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7915438 DEFAULT CHARSET=latin1;
                   
END_OF_DATA

