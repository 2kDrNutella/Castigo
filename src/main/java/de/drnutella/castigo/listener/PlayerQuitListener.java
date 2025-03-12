package de.drnutella.castigo.listener;

import de.drnutella.castigo.data.CacheManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void playerQuit(PlayerDisconnectEvent event){
        final ProxiedPlayer player = event.getPlayer();
        CacheManager.uuidCache.remove(player.getName());
    }

}
