# --- !Ups

create table `event` (`id` VARCHAR(255) NOT NULL PRIMARY KEY, `title` VARCHAR(255) NOT NULL, `host` VARCHAR(255), `description` VARCHAR(1024), `startTime` DATETIME NOT NULL, `endTime` DATETIME NOT NULL);
create table `event_participants` (`id` INT PRIMARY KEY AUTO_INCREMENT,  `eventID` VARCHAR(255) NOT NULL , `userID` VARCHAR(255) NOT NULL, `status` VARCHAR(255) NOT NULL);

# --- !Downs

drop table `event`;
drop table `event_participants`;