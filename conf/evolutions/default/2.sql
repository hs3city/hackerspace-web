# --- !Ups

create table `event` (`id` VARCHAR(255) NOT NULL PRIMARY KEY, `title` VARCHAR(255) NOT NULL, `host` VARCHAR(255) NOT NULL, `description` VARCHAR(1024) NOT NULL, `startTime` DATETIME NOT NULL, `endTime` DATETIME);
create table `event_participants` (`eventID` VARCHAR(255) NOT NULL , `userID` VARCHAR(255) NOT NULL, `status` INTEGER NOT NULL, PRIMARY KEY (eventID, userID));

# --- !Downs

drop table `event`;
drop table `event_participants`;