package de.drnutella.castigo.objects;

import de.drnutella.castigo.enums.PunishRegion;

import java.util.UUID;

public class PunishInfo {

    UUID target;
    String reason;
    PunishRegion punishRegion;
    Long punishedFrom, punishedUntil;
    Boolean isPerma, isUnbanned;

    public PunishInfo(UUID target, PunishRegion punishRegion, String reason, Boolean isPerma, Long punishedFrom, Long punishedUntil, Boolean isUnbanned) {
        this.isPerma = isPerma;
        this.isUnbanned = isUnbanned;
        this.punishedFrom = punishedFrom;
        this.punishedUntil = punishedUntil;
        this.punishRegion = punishRegion;
        this.reason = reason;
        this.target = target;
    }

    public Boolean isPerma() {
        return isPerma;
    }

    public Boolean isUnbanned() {
        return isUnbanned;
    }

    public Long punishedFrom() {
        return punishedFrom;
    }

    public Long punishedUntil() {
        return punishedUntil;
    }

    public PunishRegion punishRegion() {
        return punishRegion;
    }

    public String reason() {
        return reason;
    }

    public UUID targetUUID() {
        return target;
    }

    public Long remainingTimeMillis() {
        return punishedUntil - System.currentTimeMillis();
    }

    public Long originalTimeMillis() {
        return punishedUntil() - punishedFrom() + 1;
    }

    public Boolean isActive(){
        if(!isUnbanned){
            if(!isPerma){
                return System.currentTimeMillis() < punishedUntil;
            }else {
                return true;
            }
        }else {
            return false;
        }
    }
}
