#!/bin/sh

cd /karchan/mud/sql   

. ./mysql_constants

# First drop the database mudtest and recreate it to make sure that it is empty
${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s <<END_OF_DATA
drop database mudtest;
create database mudtest;

END_OF_DATA

# Dump all data from database "mud" to database "mudtest"
${MYSQL_DUMP} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} ${MYSQL_DB} | \
${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_TESTDB}

