package de.drnutella.castigo.events;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.manager.UserManager;
import de.drnutella.castigo.objects.Punish;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PunishExecutedEvent extends Event {

    public PunishExecutedEvent(Punish punish, CommandSender punishExecutor) {
        Castigo.getPunishDatabaseManager().punishPlayer(punish, punishFeedback -> {
            punishExecutor.sendMessage(punishFeedback.reason());
            UserManager.punishInfoContainerCache.remove(punish.targetUUID()); // remove cached Ban Infos


            if(punish.punishRegion() == PunishRegion.NETWORK){
                final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(punish.targetUUID());
                if(player != null){
                    player.disconnect("§cDu wurdest gebannt!");
                }
            }
        });
    }
}