package de.drnutella.castigo.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.utils.JSONFileBuilder;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQL {

    static final JSONFileBuilder CONFIG_BUILDER = Castigo.getMysqlJSONHandler();
    static final Castigo PROXY_CORE_INSTANCE = Castigo.getInstance();

    static final String HOST = CONFIG_BUILDER.getStringFromFile("Host");
    static final String PORT = CONFIG_BUILDER.getStringFromFile("Port");
    static final String DATABASE = CONFIG_BUILDER.getStringFromFile("Database");
    static final String USERNAME = CONFIG_BUILDER.getStringFromFile("Username");
    static final String PASSWORD = CONFIG_BUILDER.getStringFromFile("Password");

    static final HikariDataSource HIKARI_DATA_SOURCE;

    //   - Erstellt die HikariConfig und den HikariPool

    static {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE);
        hikariConfig.setUsername(USERNAME);
        hikariConfig.setPassword(PASSWORD);
        hikariConfig.setIdleTimeout(600000); // 10 Minuten, bis eine inaktive Verbindung geschlossen wird
        hikariConfig.setMaxLifetime(1800000); // 30 Minuten maximale Lebensdauer einer Verbindung
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(250);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HIKARI_DATA_SOURCE = new HikariDataSource(hikariConfig);
        PROXY_CORE_INSTANCE.getLogger().info("§aMySQL-Connection-Pool wurde eingerichtet!");
    }

    public Connection getConnection() {
        try {
            return HIKARI_DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            PROXY_CORE_INSTANCE.getLogger().warning("§cMySQL-Verbindung konnte nicht abgerufen werden!");
            e.printStackTrace();
            return null;
        }
    }

    public void closePool() {
        if (!HIKARI_DATA_SOURCE.isClosed()) {
            HIKARI_DATA_SOURCE.close();
            PROXY_CORE_INSTANCE.getLogger().info("§cMySQL-Connection-Pool wurde geschlossen!");
        }
    }
}
