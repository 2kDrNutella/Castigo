package de.drnutella.castigo.commands;

import de.drnutella.castigo.data.CacheManager;
import de.drnutella.castigo.data.api.implementation.PunishService;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.exceptions.PunishActionFaildException;
import de.drnutella.proxycore.data.implementation.UserBasicInformationService;
import de.drnutella.proxycore.objects.CustomProxyPlayer;
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
                UserBasicInformationService.getUUIDFromUserName(args[0], uuidFeedback ->{
                    if (uuidFeedback == null) {
                        try {
                            throw new PunishActionFaildException(PunishFeedback.USER_NOT_EXSIST);
                        } catch (PunishActionFaildException exception) {
                            commandSender.sendMessage(exception.getMessage());
                            return;
                        }
                    }

                    PunishService.unpunishPlayer(uuidFeedback, punishRegion, punishFeedback -> {
                        commandSender.sendMessage(punishFeedback.reason());

                        CacheManager.punishInfoContainerCache.remove(uuidFeedback); // remove cached Ban Infos

                    });
                });
            }
        }
    }
}
