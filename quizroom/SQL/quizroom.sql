DROP DATABASE if exists Data;
CREATE DATABASE Data;
USE Data;

CREATE TABLE User (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(256) NOT NULL,
    salt VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    image VARCHAR(25),
    imageData mediumblob
);

CREATE TABLE Friend (
	user1 varchar(50) not null,
	user2 varchar(50) not null,
	FOREIGN KEY fk1(user1) REFERENCES User(username),
	FOREIGN KEY fk2(user2) REFERENCES User(username)
);

CREATE TABLE Quiz (
	quizID INT(11),
	quizname varchar(100) PRIMARY KEY not null,
	creator varchar(50) not null,
	difficulty varchar(50) not null,
	topic varchar(50) not null,
	structure varchar(50) not null,
    timer int(11) not null,
	FOREIGN KEY fk1(creator) REFERENCES User(username)
);

CREATE TABLE QuizQuestion (
	quizQuestionID INT(11) PRIMARY KEY AUTO_INCREMENT,
	quizname varchar(100) not null,
	question varchar(1000) not null,
	correctchoice varchar(1000) not null,
	FOREIGN KEY fk1(quizname) REFERENCES Quiz(quizname)	
);

CREATE TABLE QuizChoice (
	quizQuestionID INT(11) not null,
	choice varchar(1000) not null,
	FOREIGN KEY fk1(quizQuestionID) REFERENCES QuizQuestion(quizQuestionID)	
);