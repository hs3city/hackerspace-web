# --- !Ups

create table `event_comments` (`id` INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, `eventID` VARCHAR (255) NOT NULL, `userID` VARCHAR (255) NOT NULL, `commentText` VARCHAR (255) NOT NULL, `datetime` DATETIME NOT NULL);

# --- !Downs

drop table `event_comments`;