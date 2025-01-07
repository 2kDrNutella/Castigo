package de.drnutella.castigo.objects;

public class PunishInfoContainer {

    PunishInfo lastChatPunish;
    PunishInfo lastNetworkPunish;

    public PunishInfoContainer(PunishInfo lastChatPunish, PunishInfo lastNetworkPunish) {
        this.lastChatPunish = lastChatPunish;
        this.lastNetworkPunish = lastNetworkPunish;
    }

    public PunishInfo lastChatPunish() {
        return lastChatPunish;
    }

    public PunishInfo lastNetworkPunish() {
        return lastNetworkPunish;
    }
}
