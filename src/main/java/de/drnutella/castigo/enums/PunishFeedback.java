package de.drnutella.castigo.enums;

public enum PunishFeedback {
    SUCCESS_PUNISHED("§aErfolgreich Bestraft!"),
    SUCCESS_UNPUNISHED("§aErfolgreich die Bestrafung aufgehoben!"),
    DATABASE_ISSUE("§cEtwas ist schiefgelaufen!"),
    USER_IS_ALLREADY_BANNED("§cDer angegebene User ist bereits bestraft!"),
    TEMPLATE_NOT_FOUND("§cDas angegebene Template wurde nicht gefunden! /bantemplates oder /mutetemplates"),
    USER_NOT_PUNISHED("§cDer angegebene User ist §c§nnicht§c bestraft!"),
    NOT_IMPLEMENTED("§cDiese Funktion ist noch nicht funktionsfähig!"),
    USER_NOT_EXSIST("§cDer angegebene User war noch nie Online!");

    private final String reason;

    PunishFeedback(String reason) {
        this.reason = reason;
    }

    public String reason() {
        return reason;
    }
}
