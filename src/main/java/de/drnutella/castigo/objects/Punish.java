package de.drnutella.castigo.objects;

import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.enums.PunishType;

import java.util.UUID;

public class Punish extends PunishInfo {

    UUID staff;
    PunishType punishType;

    public Punish(UUID target, UUID staff, PunishRegion punishRegion, PunishType punishType, String reason, Boolean isPerma, Long punishedFrom, Long punishedUntil, Boolean isUnbanned) {
        super(target, punishRegion, reason, isPerma, punishedFrom, punishedUntil, isUnbanned);
        this.punishType = punishType;
        this.staff = staff;
    }
    public PunishType punishType() {
        return punishType;
    }

    public UUID staff() {
        return staff;
    }
}
