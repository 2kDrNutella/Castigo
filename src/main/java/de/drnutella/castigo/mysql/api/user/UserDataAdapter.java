package de.drnutella.castigo.mysql.api.user;

import de.drnutella.castigo.manager.UserManager;
import de.drnutella.castigo.mysql.MySQL;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class UserDataAdapter {

    private final ExecutorService executorService;
    private final MySQL mysql;
    private ResultSet resultSet;

    public UserDataAdapter(ExecutorService executorService, MySQL mySQL){
        this.executorService = executorService;
        this.mysql = mySQL;
    }
    /*
        No Database Feedback Only Error with SQLException
     */
    public void refreshPlayerOrCreateIt(ProxiedPlayer proxiedPlayer, Consumer<Boolean> callback) {
        executorService.submit(() -> {
            boolean result;
            try {
                PreparedStatement preparedStatement = mysql.getConnection().prepareStatement("call checkUserUpToDate(?, ?)");

                preparedStatement.setString(1, proxiedPlayer.getUniqueId().toString());
                preparedStatement.setString(2, proxiedPlayer.getName());

                preparedStatement.executeUpdate();
                preparedStatement.close();

                result =  true;
            } catch (SQLException ignored) {
                result = false;
            }
            callback.accept(result);
        });
    }

    /*
        Database returns UUID with Coulmn 'uuid':
     */
    public void getUUIDFromUserName(String username, Consumer<UUID> callback){
        if(UserManager.uuidCache.containsKey(username)){
            callback.accept(UserManager.uuidCache.get(username));
        }else {
            executorService.submit(() -> {
                UUID result;
                try {
                    PreparedStatement preparedStatement =
                            mysql.getConnection().prepareStatement("SELECT t.uuid FROM castigo.user t WHERE LOWER(t.username) = LOWER(?);");

                    preparedStatement.setString(1, username);
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        result = UUID.fromString(resultSet.getString("uuid"));
                    }else {
                        resultSet.close();
                        result = null;
                    }

                    preparedStatement.close();

                } catch (SQLException exception) {
                    result = null;
                }
                callback.accept(result);
            });
        }
    }
}
