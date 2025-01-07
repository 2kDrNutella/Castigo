package de.drnutella.castigo.commands;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.exceptions.PunishActionFaildException;
import de.drnutella.castigo.manager.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class UnPunishCommand extends Command {

    // /unban [Name]
    // / 0    1
    // /      0

    final PunishRegion punishRegion;

    public UnPunishCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        switch (name) {
            case "unban" -> punishRegion = PunishRegion.NETWORK;
            case "unmute" -> punishRegion = PunishRegion.CHAT;
            default -> punishRegion = PunishRegion.OTHER;
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(getPermission())) {
            if (args.length == 1) {
                Castigo.getUserDatabaseManager().getUUIDFromUserName(args[0], uuidFeedback -> {
                    UUID userUUID;

                    if (uuidFeedback == null) {
                        try {
                            throw new PunishActionFaildException(PunishFeedback.USER_NOT_EXSIST);
                        } catch (PunishActionFaildException exception) {
                            commandSender.sendMessage(exception.getMessage());
                            return;
                        }
                    }

                    userUUID = uuidFeedback;

                    Castigo.getPunishDatabaseManager().unpunishPlayer(userUUID, punishRegion, punishFeedback -> {
                        commandSender.sendMessage(punishFeedback.reason());

                        UserManager.punishInfoContainerCache.remove(userUUID); // remove cached Ban Infos

                    });
                });
            }
        }
    }
}
