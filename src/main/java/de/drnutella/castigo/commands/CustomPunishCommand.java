package de.drnutella.castigo.commands;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.enums.PunishType;
import de.drnutella.castigo.events.PunishExecutedEvent;
import de.drnutella.castigo.exceptions.PunishActionFaildException;
import de.drnutella.castigo.objects.Punish;
import de.drnutella.proxycore.data.implementation.UserBasicInformationService;
import de.drnutella.proxycore.objects.CustomProxyPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CustomPunishCommand extends Command {

    // /customban [Name] [Zeit] [Grund]
    // /    0        1     2       3
    // /             0     1       2

    final PunishRegion punishRegion;

    public CustomPunishCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        switch (name) {
            case "customban" -> punishRegion = PunishRegion.NETWORK;
            case "custommute" -> punishRegion = PunishRegion.CHAT;
            default -> punishRegion = PunishRegion.OTHER;
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(getPermission())) {
            if (args.length < 2) {
                commandSender.sendMessage("§cBenutzung: §7/§ecustomban §7[§bSpieler§7] §7[§bZeit§7] §7[§bGrund§7]");
                return;
            }
            // Zeitangabe analysieren
            int timeArgsEndIndex = 1;
            long banTime;
            boolean isPerma;

            if (args[1].equalsIgnoreCase("perma")) {
                banTime = 0;
                isPerma = true;
            } else {
                isPerma = false;
                StringBuilder timeBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    String part = args[i];
                    if (part.matches("\\d+[dhms]") || part.matches("\\d+min")) {
                        timeBuilder.append(part).append(" ");
                        timeArgsEndIndex = i;
                    } else {
                        break;
                    }
                }

                String timeInput = timeBuilder.toString().trim();
                if (timeInput.isEmpty()) {
                    commandSender.sendMessage("§cUngültiges Zeitformat! Nutze z.B. 12h 10min 10s.");
                    return;
                }

                banTime = parseTimeInput(timeInput);
                if (banTime <= 0) {
                    commandSender.sendMessage("§cUngültiges Zeitformat! Nutze z.B. 12h 10min 10s.");
                    return;
                }
            }

            String reason;
            if (args.length > timeArgsEndIndex + 1) {
                StringBuilder reasonBuilder = new StringBuilder();
                for (int i = timeArgsEndIndex + 1; i < args.length; i++) {
                    reasonBuilder.append(args[i]).append(" ");
                }
                reason = reasonBuilder.toString().trim();
            } else {
                reason = "";
            }

            UserBasicInformationService.getUUIDFromUserName(args[0], uuidFeedback ->{
                if (uuidFeedback == null) {
                    try {
                        throw new PunishActionFaildException(PunishFeedback.USER_NOT_EXSIST);
                    } catch (PunishActionFaildException exception) {
                        commandSender.sendMessage(exception.getMessage());
                        return;
                    }
                }

                UUID staffUUID;

                if (commandSender instanceof ProxiedPlayer proxiedPlayer) {
                    staffUUID = proxiedPlayer.getUniqueId();
                } else {
                    staffUUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
                }

                Castigo.getInstance().getProxy().getPluginManager().callEvent(new PunishExecutedEvent(
                        new Punish(
                                uuidFeedback,
                                staffUUID,
                                punishRegion,
                                PunishType.CustomPunish,
                                reason,
                                isPerma,
                                System.currentTimeMillis(),
                                System.currentTimeMillis() + banTime,
                                false
                        ),
                        commandSender
                ));
            });
        }
    }

    private long parseTimeInput(String timeInput) {
        long totalMillis = 0;

        String[] parts = timeInput.split(" ");
        for (String part : parts) {
            String number = part.replaceAll("[^0-9]", "");
            String unit = part.replaceAll("[0-9]", "").toLowerCase();

            if (number.isEmpty() || unit.isEmpty()) {
                return -1; // Ungültiges Format
            }

            int value = Integer.parseInt(number);
            switch (unit) {
                case "d":
                case "day":
                case "days":
                    totalMillis += TimeUnit.DAYS.toMillis(value);
                    break;
                case "h":
                case "hour":
                case "hours":
                    totalMillis += TimeUnit.HOURS.toMillis(value);
                    break;
                case "min":
                case "minute":
                case "minutes":
                    totalMillis += TimeUnit.MINUTES.toMillis(value);
                    break;
                case "s":
                case "sec":
                case "seconds":
                    totalMillis += TimeUnit.SECONDS.toMillis(value);
                    break;
                default:
                    return -1; // Ungültige Einheit
            }
        }
        return totalMillis;
    }
}
