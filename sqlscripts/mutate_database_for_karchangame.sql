#!/bin/sh

. ./mysql_constants

echo "Running changing script..."

${MYSQL_BIN} -h ${MYSQL_HOST} -u ${MYSQL_USR} --password=${MYSQL_PWD} -s ${MYSQL_DB} <<END_OF_DATA

# === database commands to fix it for "karchangame" in ejb mode ===

drop table IF EXISTS mm_boardmessages_old;

ALTER TABLE mm_usertable CHANGE state currentstate text;
ALTER TABLE mm_usertable CHANGE creation creation_date timestamp not null
default current_timestamp;
ALTER TABLE mm_usertable CHANGE length height varchar(20);

# change the log (mm_log)
drop table if exists mm_log2;

create table mm_log2 (
  id bigint(20) NOT NULL AUTO_INCREMENT primary key,
  creation_date timestamp not null,
  name varchar(20),
  message text not null,
  addendum text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index natural_index on mm_log2 (creation_date, name);


insert into mm_log2
(creation_date, name, message, addendum)
select *
from mm_log
order by creation, name, message;

rename table mm_log to mm_log_old;
rename table mm_log2 to mm_log;

drop table mm_log_old;

ALTER TABLE mm_usertable ADD lastcommand timestamp;
update mm_usertable set lastcommand = null;

#
# this is where the items will be changed
#
ALTER TABLE mm_itemtable
  ADD COLUMN `belongsto` VARCHAR(20) NULL DEFAULT NULL  AFTER `owner` , 
  ADD COLUMN `room` INT(5) NULL DEFAULT NULL AFTER `belongsto`, 
  ADD COLUMN `containerid` INT(11) NULL DEFAULT NULL AFTER `room` , 
  ADD CONSTRAINT `fk_mm_itemtable_1`
  FOREIGN KEY (`belongsto` )
  REFERENCES mm_usertable (`name` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_mm_itemtable_2`
  FOREIGN KEY (`room` )
  REFERENCES mm_rooms (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_mm_itemtable_3`
  FOREIGN KEY (`containerid` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_mm_itemtable_1_idx` (`belongsto` ASC) 
, ADD INDEX `fk_mm_itemtable_2_idx` (`room` ASC) 
, ADD INDEX `fk_mm_itemtable_3_idx` (`containerid` ASC) ;

# now update all the items.
update mm_itemtable, mm_charitemtable
set mm_itemtable.belongsto = mm_charitemtable.belongsto
where mm_charitemtable.id = mm_itemtable.id;

update mm_itemtable, mm_itemitemtable
set mm_itemtable.containerid = mm_itemitemtable.containerid
where mm_itemitemtable.id = mm_itemtable.id;

update mm_itemtable, mm_roomitemtable
set mm_itemtable.room = mm_roomitemtable.room
where mm_roomitemtable.id = mm_itemtable.id;

# now, one would assume that there are no items that have not been
# allocated, but we're going to check anyways.
select *
from mm_itemtable
where belongsto is null
and containerid is null
and room is null;

# if the query above provides results, we need to delete those results.
update mm_itemtable
set belongsto='Karn'
where belongsto is null
and containerid is null
and room is null;

# add fields to mm_usertable to indicate what someone is wearing where
ALTER TABLE mm_usertable
  ADD COLUMN `wieldleft` INT(11) NULL DEFAULT NULL  AFTER `lastcommand` , 
  ADD COLUMN `wieldright` INT(11) NULL DEFAULT NULL  AFTER `wieldleft` , 
  ADD COLUMN `wieldboth` INT(11) NULL DEFAULT NULL  AFTER `wieldright` , 
  ADD COLUMN `wearhead` INT(11) NULL DEFAULT NULL  AFTER `wieldboth` , 
  ADD COLUMN `wearneck` INT(11) NULL DEFAULT NULL  AFTER `wearhead` , 
  ADD COLUMN `weartorso` INT(11) NULL DEFAULT NULL AFTER `wearneck` , 
  ADD COLUMN `weararms` INT(11) NULL DEFAULT NULL  AFTER`weartorso` , 
  ADD COLUMN `wearleftwrist` INT(11) NULL DEFAULT NULL  AFTER`weararms` , 
  ADD COLUMN `wearrightwrist` INT(11) NULL DEFAULT NULL  AFTER`wearleftwrist` , 
  ADD COLUMN `wearleftfinger` INT(11) NULL DEFAULT NULL AFTER `wearrightwrist` , 
  ADD COLUMN `wearrightfinger` INT(11) NULL DEFAULT NULL  AFTER `wearleftfinger` , 
  ADD COLUMN `wearfeet` INT(11) NULL DEFAULT NULL  AFTER `wearrightfinger` , 
  ADD COLUMN `wearhands` INT(11) NULL DEFAULT NULL  AFTER `wearfeet` , 
  ADD COLUMN `wearfloatingnearby` INT(11) NULL DEFAULT NULL  AFTER `wearhands` , 
  ADD COLUMN `wearwaist` INT(11) NULL DEFAULT NULL  AFTER `wearfloatingnearby` , 
  ADD COLUMN `wearlegs` INT(11)NULL DEFAULT NULL  AFTER `wearwaist` , 
  ADD COLUMN `weareyes` INT(11) NULL DEFAULT NULL  AFTER `wearlegs` , 
  ADD COLUMN `wearears` INT(11) NULL DEFAULT NULL  AFTER `weareyes` , 
  ADD COLUMN `wearaboutbody` INT(11) NULL DEFAULT NULL  AFTER `wearears` , 
  ADD CONSTRAINT `wieldleft_fk`
  FOREIGN KEY (`wieldleft` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wieldright_fk`
  FOREIGN KEY (`wieldright` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wieldboth_fk`
  FOREIGN KEY (`wieldboth` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearhead_fk`
  FOREIGN KEY (`wearhead` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearneck_fk`
  FOREIGN KEY (`wearneck` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `weartorso_fk`
  FOREIGN KEY (`weartorso` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `weararms_fk`
  FOREIGN KEY (`weararms` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearleftwrist_fk`
  FOREIGN KEY (`wearleftwrist` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearrightwrist_fk`
  FOREIGN KEY (`wearrightwrist` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearleffinger_fk`
  FOREIGN KEY (`wearleftfinger` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearrightfinger_fk`
  FOREIGN KEY (`wearrightfinger` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearfeet_fk`
  FOREIGN KEY (`wearfeet` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearhands_fk`
  FOREIGN KEY (`wearhands` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearfloatingnearby_fk`
  FOREIGN KEY (`wearfloatingnearby` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearwaist_fk`
  FOREIGN KEY (`wearwaist` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearlegs_fk`
  FOREIGN KEY (`wearlegs` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `weareyes_fk`
  FOREIGN KEY (`weareyes` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearears_fk`
  FOREIGN KEY (`wearears` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `wearaboutbody`
  FOREIGN KEY (`wearaboutbody` )
  REFERENCES mm_itemtable (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_mm_usertable_1_idx` (`wieldleft` ASC) 
, ADD INDEX `fk_mm_usertable_2_idx` (`wieldright` ASC) 
, ADD INDEX `fk_mm_usertable_3_idx` (`wieldboth` ASC) 
, ADD INDEX `fk_mm_usertable_4_idx` (`wearhead` ASC) 
, ADD INDEX `fk_mm_usertable_5_idx` (`wearneck` ASC) 
, ADD INDEX `fk_mm_usertable_6_idx` (`weartorso` ASC) 
, ADD INDEX `fk_mm_usertable_7_idx` (`weararms` ASC) 
, ADD INDEX `fk_mm_usertable_8_idx` (`wearleftwrist` ASC) 
, ADD INDEX `fk_mm_usertable_9_idx` (`wearrightwrist` ASC) 
, ADD INDEX `fk_mm_usertable_1_idx1` (`wearleftfinger` ASC) 
, ADD INDEX `fk_mm_usertable_2_idx1` (`wearrightfinger` ASC) 
, ADD INDEX `fk_mm_usertable_3_idx1` (`wearfeet` ASC) 
, ADD INDEX `fk_mm_usertable_4_idx1` (`wearhands` ASC) 
, ADD INDEX `fk_mm_usertable_5_idx1` (`wearfloatingnearby` ASC) 
, ADD INDEX `fk_mm_usertable_6_idx1` (`wearwaist` ASC) 
, ADD INDEX `fk_mm_usertable_1_idx2` (`wearlegs` ASC) 
, ADD INDEX `fk_mm_usertable_1_idx3` (`weareyes` ASC) 
, ADD INDEX `fk_mm_usertable_1_idx4` (`wearears` ASC) 
, ADD INDEX `fk_mm_usertable_1_idx5` (`wearaboutbody` ASC) ;

# dropping the old tables, we cannot rename them or something like that
# because they contain links to items, that therefore cannot be deleted.

drop table mm_charitemtable;
drop table mm_itemitemtable;
drop table mm_roomitemtable;

# don't know why, but the itemid in mm_itemtable did not have a not-null
# constraint.
ALTER TABLE mm_itemtable DROP FOREIGN KEY `mm_itemtable_ibfk_1` ;
ALTER TABLE mm_itemtable CHANGE COLUMN `itemid` `itemid` INT(11)
NOT NULL  , 
  ADD CONSTRAINT `mm_itemtable_ibfk_1`
  FOREIGN KEY (`itemid` )
  REFERENCES mm_items (`id` )
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

# enditems

ALTER TABLE mm_items ADD COLUMN `image` VARCHAR(255) NULL DEFAULT
NULL  AFTER `notes` , ADD COLUMN `title` VARCHAR(255) NULL  AFTER `image` ;

# add a room number to the boards, so a board
# is situated in a room.
ALTER TABLE mm_boards ADD COLUMN `room` INT(5) NOT NULL AFTER
  `creation`;
update mm_boards set room = 1 where id = 2;
update mm_boards set room = 3 where id = 1;
update mm_boards set room = 13 where id = 3;
ALTER TABLE mm_boards
  ADD CONSTRAINT `fk_mm_boards_1`
  FOREIGN KEY (`room` )
  REFERENCES mm_rooms (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_mm_boards_1_idx` (`room` ASC) ;

update mm_events
set month = null where month = -1;
update mm_events
set dayofmonth = null where dayofmonth = -1;
update mm_events
set hour = null where hour = -1;
update mm_events
set minute = null where minute = -1;
update mm_events
set dayofweek = null where dayofweek = -1;

ALTER TABLE mm_events
CHANGE COLUMN `month` `month` INT(11) NULL DEFAULT NULL  , 
CHANGE COLUMN `dayofmonth` `dayofmonth` INT(11) NULL DEFAULT NULL  , 
CHANGE COLUMN `hour` `hour` INT(11) NULL DEFAULT NULL  , 
CHANGE COLUMN `minute` `minute` INT(11) NULL DEFAULT NULL  , 
CHANGE COLUMN `dayofweek` `dayofweek` INT(11) NULL DEFAULT NULL  ;

# deletes events whos persons/users are no longer in the database.
delete from mm_events where name is not null and not exists (select * from
mm_usertable where mm_usertable.name = mm_events.name);

ALTER TABLE mm_events
  ADD CONSTRAINT `fk_mm_events_1`
  FOREIGN KEY (`name` )
  REFERENCES mm_usertable (`name` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION, 
  ADD CONSTRAINT `fk_mm_events_2`
  FOREIGN KEY (`room` )
  REFERENCES mm_rooms (`id` )
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
, ADD INDEX `fk_mm_events_1_idx` (`name` ASC) 
, ADD INDEX `fk_mm_events_2_idx` (`room` ASC) ;

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
                   

START TRANSACTION;
 # drop trigger, if it exists
DROP TRIGGER IF EXISTS password_trigger;
# create the trigger
CREATE TRIGGER password_trigger BEFORE INSERT ON mm_usertable
FOR EACH ROW
    SET NEW.password = sha1(NEW.password);
# commit transaction
COMMIT;


# changed the mm_commands table, it's weird to have a composite key. Like there's ever going to be a strange ID in there...
ALTER TABLE mm_commands DROP PRIMARY KEY;                        
ALTER TABLE mm_commands MODIFY id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT;

ALTER TABLE mm_commands ADD FOREIGN KEY
    mm_commands_room_fk (room) REFERENCES mm_rooms (id) ON DELETE CASCADE;

END_OF_DATA

