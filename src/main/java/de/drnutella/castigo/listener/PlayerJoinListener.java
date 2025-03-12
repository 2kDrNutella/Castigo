package de.drnutella.castigo.listener;

import de.drnutella.castigo.data.CacheManager;
import de.drnutella.castigo.data.api.implementation.PunishService;
import de.drnutella.castigo.objects.PunishInfo;
import de.drnutella.proxycore.utils.TimeCalculator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void postLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        CacheManager.uuidCache.put(player.getName(), player.getUniqueId());

        //Load Punish Info Container from Database (cached)
        PunishService.loadPunishInfoContainer(player.getUniqueId(), feedback -> {
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
                        event.getPlayer().disconnect(
                                "§cDu wurdest für §e" + TimeCalculator.convertMillisToReadableTime(lastNetworkPunish.originalTimeMillis()) + " §cvom Netzwerk gebannt\n" +
                                        "§3Grund§7: §c" + lastNetworkPunish.reason() + "\n\n" +
                                        "§3Verbleibende Zeit§7: §e" + TimeCalculator.convertMillisToReadableTime(lastNetworkPunish.remainingTimeMillis()) + "\n\n" +
                                        "§aDu kannst unter §awww.hierKannIhreWerbungStehen.net/go/ea §aeinen Entbannungsantrag\n" +
                                        "§aerstellen"
                        );
                    }
                }
            }
        });
    }
}
