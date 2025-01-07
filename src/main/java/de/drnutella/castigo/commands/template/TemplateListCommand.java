package de.drnutella.castigo.commands.template;

import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.utils.TemplateConverter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class TemplateListCommand extends Command {

    final PunishRegion punishRegion;

    public TemplateListCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        switch (name) {
            case "bantemplates" -> punishRegion = PunishRegion.NETWORK;
            case "mutetemplates" -> punishRegion = PunishRegion.CHAT;
            default -> punishRegion = PunishRegion.OTHER;
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(getPermission())) {
            switch (punishRegion){
                case NETWORK -> {
                    commandSender.sendMessage("§aBan Templates§7:");
                    TemplateConverter.getTemplateList(PunishRegion.NETWORK).forEach(template -> {
                        commandSender.sendMessage("§b§l" + template);
                    });
                }
                case CHAT -> {
                    commandSender.sendMessage("§aMute Templates§7:");
                    TemplateConverter.getTemplateList(PunishRegion.CHAT).forEach(template -> {
                        commandSender.sendMessage("§b§l" + template);
                    });
                }
            }
        }
    }
}
