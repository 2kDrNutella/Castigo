DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkUserUpToDate`(
	IN userUUID CHAR(36),
	IN newUsername VARCHAR(16)
)
BEGIN
	DECLARE userCount INT;
	DECLARE currentUsername VARCHAR(16);
 	 
	SELECT COUNT(*) INTO userCount FROM user u WHERE u.uuid = userUUID;
    
	IF userCount > 0 THEN
   	 
    	SELECT username INTO currentUsername FROM user u WHERE u.uuid = userUUID;
    	UPDATE user SET lastlogin = now() WHERE uuid = userUUID;

    	IF currentUsername <> newUsername THEN
       	 
        	UPDATE user u SET u.username = newUsername WHERE u.uuid = userUUID;
    	END IF;
	ELSE
   	 
    	INSERT INTO user (uuid, username, lastlogin) VALUES (userUUID, newUsername, now());
	END IF;
   	 
END //
CREATE DEFINER=`root`@`localhost` PROCEDURE `getLatestPunishs`(
	IN uuid char(36)
)
BEGIN
	DECLARE punishCount INT;
	DECLARE lastChatPunishID INT;
	DECLARE lastNetworkPunishID INT;
	SELECT COUNT(t.punishid) INTO punishCount FROM punishment t WHERE t.uuid = uuid;

	IF punishCount > 0 THEN
    	SELECT MAX(t1.punishs) INTO lastChatPunishID FROM (SELECT t.punishid as 'punishs' FROM punishment t WHERE t.uuid = uuid AND t.region = 'CHAT') as t1;
    	SELECT MAX(t1.punishs) INTO lastNetworkPunishID FROM (SELECT t.punishid as 'punishs' FROM punishment t WHERE t.uuid = uuid AND t.region = 'NETWORK') as t1;
   	 
    	SELECT * FROM punishment t WHERE t.uuid = uuid AND t.punishid = lastChatPunishID OR t.punishid = lastNetworkPunishID;
	END IF;
END;
CREATE DEFINER=`root`@`localhost` PROCEDURE `isPlayerPunished`(
	IN uuid char(36),
	IN region varchar(16),
	OUT feedback varchar(32)
)
BEGIN
	DECLARE punishCount INT;
	DECLARE lastPunishID INT;
	DECLARE lastBanTime BIGINT;
	DECLARE currentMillis BIGINT;
	DECLARE isPerma TINYINT;
	DECLARE isUnbanned TINYINT;
	SELECT COUNT(t.punishid) INTO punishCount FROM punishment t WHERE t.uuid = uuid AND t.region = region;
    
	IF punishCount > 0 THEN
    	SELECT ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) INTO currentMillis;
    	SELECT MAX(t1.punishs) INTO lastPunishID FROM (SELECT t.punishid as 'punishs' FROM punishment t WHERE t.uuid = uuid AND t.region = region) as t1;
    	SELECT t.isPerma INTO isPerma FROM punishment t WHERE t.punishId = lastPunishID;
    	SELECT t.isUnbanned INTO isUnbanned FROM punishment t WHERE t.punishId = lastPunishID;
   	 
    	IF isUnbanned = 0 THEN
        	IF isPerma = 1 THEN
            	SET feedback = 'IS PUNISHED';
        	ELSE
            	SELECT t.punishedUntil INTO lastBanTime FROM punishment t WHERE t.punishId = lastPunishID;
   	 
            	IF lastBanTime > currentMillis THEN
                	SET feedback = 'IS PUNISHED';
            	ELSE
                	SET feedback = 'NOT PUNISHED';
            	END IF;
        	END IF;
    	ELSE
        	SET feedback = 'NOT PUNISHED';
    	END IF;
	ELSE
    	SET feedback = 'NOT PUNISHED';
	END IF;
END
CREATE DEFINER=`root`@`localhost` PROCEDURE `punish`(
	IN uuid char(36),
	IN staffUUID char(36),
	IN region varchar(16),
	IN `type` varchar(16),
	IN reason longtext,
	IN isPerma tinyint,
	IN punishedFrom bigint,
	IN punishedUntil bigint,
	IN isUnbanned tinyint
)
BEGIN
	DECLARE isPunished VARCHAR(32);
	CALL isPlayerPunished(uuid, region, isPunished);

	IF isPunished = "NOT PUNISHED" THEN
    	INSERT INTO castigo.punishment
        	(uuid, staffUUID, region, `type`, reason, isPerma, punishedFrom, punishedUntil, isUnbanned)
        	VALUES
        	(uuid, staffUUID, region, `type`, reason, isPerma, punishedFrom, punishedUntil, isUnbanned);    
    	SELECT 'SUCCESS' AS 'feedback';
	ELSE
    	SELECT 'ALLREADY PUNISHED' AS 'feedback';
	END IF;
END
CREATE DEFINER=`root`@`localhost` PROCEDURE `unpunishPlayer`(    
	IN uuid char(36),
	IN region varchar(16),
	OUT feedback varchar(32)
)
BEGIN
	DECLARE punishCount INT;
	DECLARE lastPunishID INT;
	DECLARE lastBanTime BIGINT;
	DECLARE currentMillis BIGINT;
	DECLARE isPerma TINYINT;
	DECLARE isUnbanned TINYINT;
	SELECT COUNT(t.punishid) INTO punishCount FROM punishment t WHERE t.uuid = uuid AND t.region = region;
    
	IF punishCount > 0 THEN
    	SELECT ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000) INTO currentMillis;
    	SELECT MAX(t1.punishs) INTO lastPunishID FROM (SELECT t.punishid as 'punishs' FROM punishment t WHERE t.uuid = uuid AND t.region = region) as t1;
    	SELECT t.isPerma INTO isPerma FROM punishment t WHERE t.punishId = lastPunishID;
    	SELECT t.isUnbanned INTO isUnbanned FROM punishment t WHERE t.punishId = lastPunishID;
   	 
    	IF isUnbanned = 0 THEN
        	SELECT t.punishedUntil INTO lastBanTime FROM punishment t WHERE t.punishId = lastPunishID;
       	 
            	IF lastBanTime > currentMillis OR isPerma = 1 THEN
                	UPDATE punishment t SET t.isUnbanned = 1 WHERE t.uuid = uuid AND t.region = region AND t.punishId = lastPunishID;
                	SET feedback = 'SUCCESS';
            	ELSE
                	SET feedback = 'NOT PUNISHED';
            	END IF;
    	ELSE
        	SET feedback = 'NOT PUNISHED';
    	END IF;
	ELSE
    	SET feedback = 'NOT PUNISHED';
	END IF;
END
CREATE DEFINER=`root`@`localhost` PROCEDURE `unpunishPlayerMapped`(
	IN uuid char(36),
	IN region varchar(16)
)
BEGIN
	DECLARE feedbackCall VARCHAR(32);
	CALL unpunishPlayer(uuid, region, feedbackCall);
	SELECT feedbackCall as 'feedback';
END
