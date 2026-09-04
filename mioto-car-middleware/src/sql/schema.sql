CREATE DATABASE IF NOT EXISTS mioto_final_project
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mioto_final_project;

CREATE TABLE IF NOT EXISTS Users (
  userId       INT          NOT NULL AUTO_INCREMENT,
  phone        VARCHAR(20)  NOT NULL,
  email        VARCHAR(160) NOT NULL DEFAULT '',
  displayName  VARCHAR(120) NOT NULL DEFAULT '',
  status       TINYINT      NOT NULL DEFAULT 1,      
  timeCreated  BIGINT       NOT NULL DEFAULT 0,
  timeUpdated  BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (userId),
  UNIQUE KEY uk_users_phone (phone)      
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS UserPwd (
  userId       INT          NOT NULL,
  pwdHash      VARCHAR(128) NOT NULL,
  salt         VARCHAR(32)  NOT NULL,
  timeUpdated  BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (userId)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Session (
  sessionId    BIGINT       NOT NULL,               
  userId       INT          NOT NULL,
  userAgent    VARCHAR(255) NOT NULL DEFAULT '',
  userIP       VARCHAR(45)  NOT NULL DEFAULT '',
  timeCreated  BIGINT       NOT NULL DEFAULT 0,
  timeExpired  BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (sessionId),
  KEY idx_session_user (userId)          
) ENGINE=InnoDB;

CREATE USER IF NOT EXISTS 'tuanlee'@'localhost' IDENTIFIED BY 'tuanlee@5678';
GRANT SELECT, INSERT, UPDATE, DELETE ON mioto_final_project.* TO 'mioto'@'localhost';
FLUSH PRIVILEGES;