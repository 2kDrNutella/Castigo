package de.drnutella.castigo.data;

import de.drnutella.castigo.objects.PunishInfoContainer;

import java.util.HashMap;
import java.util.UUID;

public class CacheManager {

    public static final HashMap<String, UUID> uuidCache = new HashMap<>(); //only reset if User quit
    public static final HashMap<UUID, PunishInfoContainer> punishInfoContainerCache = new HashMap<>(); //only reset on Server restart! Updated from Netty Channel

}
