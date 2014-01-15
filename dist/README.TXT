Download the plugin into your plugins folders
create a folder "McLogin" with there in a file called "mclogin.conf".
The content of this conf file should be:

DatabaseAddres=IPOFMYSQLSERVER
#The ip address of the mysql
DatabasePort=PORTOFMYSQLSERVER
#Port of the database Standard = 3306
DatabaseName=DATABASENAME
#Name of the database
DatabaseUser=USERNAME
#Name of the user that can connect to the database
DatabasePassword=PASSWORD
#Password of the uses that can connect to the database
LoginPosX=0
LoginPosY=0
LoginPosZ=0
#Mid area where players spawn to login
LoginArea=3
#Area arround the previous position where players could spawn (in blocks)
MaxTryAgain=4
#times that a player can try to login (+1)
WorldName=world
#The name of the world
DefaultSpawn=X|Y|Z
DefaultWorld=world

Execute the following SQL and replace the fields DATBASENAME to the name you want your database to be called:

CREATE DATABASE  IF NOT EXISTS `DATBASENAME` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `DATBASENAME`;
DROP TABLE IF EXISTS `tblplayers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblplayers` (
  `PlayerID` int(11) NOT NULL AUTO_INCREMENT,
  `PlayerMCName` varchar(100) DEFAULT NULL,
  `PlayerLoginKey` varchar(10) DEFAULT 'Default',
  `PlayerLastLocation` varchar(30) DEFAULT NULL,
  `PlayerLoginAttempt` int(11) DEFAULT '3',
  `PlayerLocked` int(11) DEFAULT '0',
  `PlayerWorld` varchar(100) DEFAULT NULL,
  `PlayerLoggedIn` int(1) DEFAULT '0',
  `PlayerGamemode` int(1) DEFAULT '0',
  PRIMARY KEY (`PlayerID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

