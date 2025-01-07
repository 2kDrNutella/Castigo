package de.drnutella.castigo.manager;

import de.drnutella.castigo.objects.PunishInfoContainer;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    public static final HashMap<String, UUID> uuidCache = new HashMap<>(); //only reset if User quit
    public static final HashMap<UUID, PunishInfoContainer> punishInfoContainerCache = new HashMap<>(); //only reset on Server restart! Updated from Netty Channel

}
