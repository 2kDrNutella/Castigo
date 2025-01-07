package de.drnutella.castigo.listener;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.manager.UserManager;
import de.drnutella.castigo.objects.PunishInfo;
import de.drnutella.castigo.utils.TimeCalculator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat(ChatEvent event){
        if(event.getSender() instanceof ProxiedPlayer player){
            if(!event.getMessage().contains("/")) {
                Castigo.getPunishDatabaseManager().loadPunishInfoContainer(player.getUniqueId(), feedback -> {
                    if (feedback.lastChatPunish() != null) { //null if no network punish found
                        final PunishInfo lastChatPunish = feedback.lastChatPunish();
                        if (lastChatPunish.isActive()) {
                            event.setMessage(null);
                            event.setCancelled(true);

                            if (lastChatPunish.isPerma()) {
                                player.sendMessage("§cDu wurdest §4PERMANENT §caus dem Chat gebannt§7!");
                                player.sendMessage("§eMutegrund§7: §c" + lastChatPunish.reason());
                                player.sendMessage("§aVerbleibende Zeit§7: §4PERMANENT");

                            } else {
                                final long remainingTimeMillis = lastChatPunish.punishedUntil() - System.currentTimeMillis();
                                final long originalTimeMillis = lastChatPunish.punishedUntil() - lastChatPunish.punishedFrom();

                                player.sendMessage("§cDu wurdest für §4" + TimeCalculator.convertMillisToReadableTime(originalTimeMillis) + " §caus dem Chat gebannt§7!");
                                player.sendMessage("§eMutegrund§7: §c" + lastChatPunish.reason());
                                player.sendMessage("§aVerbleibende Zeit§7: §e" + TimeCalculator.convertMillisToReadableTime(remainingTimeMillis));
                            }
                        }
                    }
                });
            }
        }
    }
}
