#!/bin/sh

. ./mysql_constants

# used for wrapping a select of all users not logged on for more than 6 months
# and deleting them

./select_oldcharacters.sql | xargs -n 3 ./clear_oldcharacters.sql
