package de.drnutella.castigo.exceptions;

import de.drnutella.castigo.enums.PunishFeedback;

public class PunishActionFaildException extends Exception{

    public PunishActionFaildException(PunishFeedback punishFeedback) {
        super(punishFeedback.reason());
    }
}
