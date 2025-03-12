package de.drnutella.castigo.data.api.dataAdapter;

import de.drnutella.castigo.data.CacheManager;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.enums.PunishType;
import de.drnutella.castigo.objects.Punish;
import de.drnutella.castigo.objects.PunishInfo;
import de.drnutella.castigo.objects.PunishInfoContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class PunishDataAdapter extends DataAdapter {

    static ResultSet resultSet;

    /*
        Database returns feedback with Coulmn 'feedback':
        - ALLREADY PUNISHED
        - SUCCESS
     */
    protected static void punishPlayerSQL(Punish punish, Consumer<PunishFeedback> callback) {
        executorService.submit(() -> {
            PunishFeedback feedback;
            try (final Connection connection = mysql.getConnection()) {
                final PreparedStatement preparedStatement = connection.prepareStatement("call punish(?,?,?,?,?,?,?,?,?);");

                preparedStatement.setString(1, punish.targetUUID().toString());
                preparedStatement.setString(2, punish.staff().toString());
                preparedStatement.setString(3, punish.punishRegion().name());
                preparedStatement.setString(4, punish.punishType().name());
                preparedStatement.setString(5, punish.reason());

                if (punish.isPerma()) {
                    preparedStatement.setInt(6, 1);
                } else {
                    preparedStatement.setInt(6, 0);
                }

                preparedStatement.setLong(7, punish.punishedFrom());
                preparedStatement.setLong(8, punish.punishedUntil());

                if (punish.isUnbanned()) {
                    preparedStatement.setInt(9, 1);
                } else {
                    preparedStatement.setInt(9, 0);
                }

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    if (resultSet.getString("feedback").equals("ALLREADY PUNISHED")) {
                        feedback = PunishFeedback.USER_IS_ALLREADY_BANNED;
                    } else {
                        feedback = PunishFeedback.SUCCESS_PUNISHED;
                    }

                } else {
                    feedback = PunishFeedback.DATABASE_ISSUE;
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException ignored) {
                feedback = PunishFeedback.DATABASE_ISSUE;
            }
            callback.accept(feedback);
        });
    }

    /*
        Database returns feedback with Coulmn 'feedback':
        - NOT PUNISHED
        - SUCCESS
     */
    protected static void unpunishPlayerSQL(UUID uuid, PunishRegion punishRegion, Consumer<PunishFeedback> callback) {
        executorService.submit(() -> {
            PunishFeedback feedback;
            try (final Connection connection = mysql.getConnection()) {

                final PreparedStatement preparedStatement = connection.prepareStatement("call unpunishPlayerMapped(?,?);");

                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, punishRegion.name());

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    if (resultSet.getString("feedback").equals("NOT PUNISHED")) {
                        feedback = PunishFeedback.USER_NOT_PUNISHED;
                    } else {
                        feedback = PunishFeedback.SUCCESS_UNPUNISHED;
                    }

                } else {
                    feedback = PunishFeedback.DATABASE_ISSUE;
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException ignored) {
                feedback = PunishFeedback.DATABASE_ISSUE;
            }
            callback.accept(feedback);
        });
    }

    /*
        Database returns Amount with Coulmn 'amount':
     */
    protected static void getReasonCountSQL(UUID uuid, String template, PunishRegion region, PunishType punishType, Consumer<Integer> callback) {
        executorService.submit(() -> {
            int result;
            try (final Connection connection = mysql.getConnection()) {
                final PreparedStatement preparedStatement =
                        connection.prepareStatement(
                                "SELECT count(t.punishId) as 'amount' FROM castigo.punishment t " +
                                        "WHERE t.uuid = ? " +
                                        "AND t.region = ? " +
                                        "AND t.`type` = ? " +
                                        "AND t.reason = ?" +
                                        "AND t.isUnbanned = 0" +
                                        ";");

                preparedStatement.setString(1, String.valueOf(uuid));
                preparedStatement.setString(2, region.name());
                preparedStatement.setString(3, punishType.name());
                preparedStatement.setString(4, template);


                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    result = resultSet.getInt("amount");
                } else {
                    result = -1;
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException ignored) {
                result = -1;
            }
            callback.accept(result);
        });
    }

    /*
     */
    protected static void loadPunishInfoContainerSQL(UUID uuid, Consumer<PunishInfoContainer> callback) {
        executorService.submit(() -> {
            PunishInfoContainer feedback = null;
            try (final Connection connection = mysql.getConnection()) {

                final PreparedStatement preparedStatement = connection.prepareStatement("call getLatestPunishs(?);");

                preparedStatement.setString(1, uuid.toString());

                resultSet = preparedStatement.executeQuery();

                PunishInfo lastChatPunish = null;
                PunishInfo lastNetworkPunish = null;

                boolean isPerma;
                boolean isUnbanned;

                while (resultSet.next()) {
                    if (resultSet.getString("region").equals("CHAT")) {

                        isPerma = (resultSet.getInt("isPerma") == 1);
                        isUnbanned = (resultSet.getInt("isUnbanned") == 1);

                        lastChatPunish = new PunishInfo(
                                UUID.fromString(resultSet.getString("uuid")),
                                PunishRegion.CHAT,
                                resultSet.getString("reason"),
                                isPerma,
                                resultSet.getLong("punishedFrom"),
                                resultSet.getLong("punishedUntil"),
                                isUnbanned
                        );
                    } else { //must be network
                        isPerma = (resultSet.getInt("isPerma") == 1);
                        isUnbanned = (resultSet.getInt("isUnbanned") == 1);

                        lastNetworkPunish = new PunishInfo(
                                UUID.fromString(resultSet.getString("uuid")),
                                PunishRegion.NETWORK,
                                resultSet.getString("reason"),
                                isPerma,
                                resultSet.getLong("punishedFrom"),
                                resultSet.getLong("punishedUntil"),
                                isUnbanned
                        );
                    }
                    resultSet.close();
                    feedback = new PunishInfoContainer(lastChatPunish, lastNetworkPunish);
                }

            } catch (SQLException ignored) {
                feedback = null;
            }
            callback.accept(feedback);
        });
    }
}
