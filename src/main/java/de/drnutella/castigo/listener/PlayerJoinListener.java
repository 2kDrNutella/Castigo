package de.drnutella.castigo.listener;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.manager.UserManager;
import de.drnutella.castigo.objects.PunishInfo;
import de.drnutella.castigo.utils.TimeCalculator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void postLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        Castigo.getUserDatabaseManager().refreshPlayerOrCreateIt(player, success -> {
            if (!success) {
                event.getPlayer().disconnect("§cConnection Lost");
            }
        });

        UserManager.uuidCache.put(player.getName(), player.getUniqueId());

        //Load Punish Info Container from Database (cached)
        Castigo.getPunishDatabaseManager().loadPunishInfoContainer(player.getUniqueId(), feedback -> {
            if (feedback.lastNetworkPunish() != null) { //null if no network punish found
                final PunishInfo lastNetworkPunish = feedback.lastNetworkPunish();
                if (lastNetworkPunish.isActive()) {
                    if(lastNetworkPunish.isPerma()){
                        event.getPlayer().disconnect(
                                "§cDu wurdest §4PERMANENT §cvom Netzwerk gebannt\n" +
                                        "§3Grund§7: §c" + lastNetworkPunish.reason() + "\n\n" +
                                        "§3Verbleibende Zeit§7: §4PERMANENT \n\n" +
                                        "§aDu kannst unter §awww.hierKannIhreWerbungStehen.net/go/ea §aeinen Entbannungsantrag\n" +
                                        "§aerstellen"
                        );
                    }else {

                        final long remainingTimeMillis = lastNetworkPunish.punishedUntil() - System.currentTimeMillis();
                        final long originalTimeMillis = lastNetworkPunish.punishedUntil() - lastNetworkPunish.punishedFrom() + 1;

                        event.getPlayer().disconnect(
                                "§cDu wurdest für §e" + TimeCalculator.convertMillisToReadableTime(originalTimeMillis) + " §cvom Netzwerk gebannt\n" +
                                        "§3Grund§7: §c" + lastNetworkPunish.reason() + "\n\n" +
                                        "§3Verbleibende Zeit§7: §e" + TimeCalculator.convertMillisToReadableTime(remainingTimeMillis) + "\n\n" +
                                        "§aDu kannst unter §awww.hierKannIhreWerbungStehen.net/go/ea §aeinen Entbannungsantrag\n" +
                                        "§aerstellen"
                        );
                    }
                }
            }
        });
    }
}
