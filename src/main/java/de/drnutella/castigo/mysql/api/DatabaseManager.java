package de.drnutella.castigo.mysql.api;


import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.mysql.MySQL;
import de.drnutella.castigo.utils.JSONFileBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private final MySQL mySQL = Castigo.getMySQL();
    private final JSONFileBuilder mySQLJSONHandler = Castigo.getMysqlJSONHandler();
    private final Boolean areTablesCreated = mySQLJSONHandler.getBooleanFromFile("Tables-Created");

    public void createDefaultTables() {
        if (!areTablesCreated) {
            createUserTable();
            createPunishmentTable();
            mySQLJSONHandler.setBooleanToFile("Tables-Created", true);
        }
    }

    private void createUserTable() {
        try {
            PreparedStatement preparedStatement = mySQL.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXSIST `castigo`.`user` (" +
                    "  `uuid` CHAR(36) NOT NULL," +
                    "  `username` VARCHAR(16) NOT NULL," +
                    "  `lastLogin` TIMESTAMP NOT NULL," +
                    "  PRIMARY KEY (`uuid`));"
            );

            preparedStatement.executeUpdate();
            preparedStatement.close();
            Castigo.getInstance().getLogger().info("[Castigo] `castigo`.`user` Table erfolgreich erstellt!");

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Castigo.getInstance().getLogger().info("[Castigo] FEHLER BEI `castigo`.`user` Table ERSTELLUNG!");

        }
    }

    private void createPunishmentTable() {
        try {
            PreparedStatement preparedStatement = mySQL.getConnection().prepareStatement(
                    "CREATE TABLE `castigo`.`punishment` (" +
                    "  `punishId` INT NOT NULL AUTO_INCREMENT," +
                    "  `uuid` CHAR(36) NOT NULL," +
                    "  `staffUUID` CHAR(36) NOT NULL," +
                    "  `region` VARCHAR(16) NOT NULL," +
                    "  `type` VARCHAR(16) NOT NULL," +
                    "  `reason` LONGTEXT NOT NULL," +
                    "  `isPerma` TINYINT NOT NULL DEFAULT 0," +
                    "  `punishedFrom` BIGINT NOT NULL," +
                    "  `punishedUntil` BIGINT NOT NULL," +
                    "  `isUnbanned` TINYINT NOT NULL DEFAULT 0," +
                    "  PRIMARY KEY (`punishId`)" +
                            ");");

            preparedStatement.executeUpdate();
            preparedStatement.close();
            Castigo.getInstance().getLogger().info("[Castigo] `castigo`.`punishment` Table erfolgreich erstellt!");

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            Castigo.getInstance().getLogger().info("[Castigo] FEHLER BEI `castigo`.`punishment` Table ERSTELLUNG!");

        }
    }
}
