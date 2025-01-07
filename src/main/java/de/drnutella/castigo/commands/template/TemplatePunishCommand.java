package de.drnutella.castigo.commands.template;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.enums.PunishType;
import de.drnutella.castigo.events.PunishExecutedEvent;
import de.drnutella.castigo.exceptions.PunishActionFaildException;
import de.drnutella.castigo.objects.Punish;
import de.drnutella.castigo.utils.TemplateConverter;
import de.drnutella.castigo.utils.TimeCalculator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.UUID;

public class TemplatePunishCommand extends Command {

    // /ban [Name] [Template]
    // / 0    1        2
    // /      0        1

    final PunishRegion punishRegion;

    public TemplatePunishCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        switch (name) {
            case "ban" -> punishRegion = PunishRegion.NETWORK;
            case "mute" -> punishRegion = PunishRegion.CHAT;
            default -> punishRegion = PunishRegion.OTHER;
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission(getPermission())) {
            if (args.length == 2) {

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

                    UUID staffUUID;
                    final String template = args[1].toUpperCase();

                    if (commandSender instanceof ProxiedPlayer proxiedPlayer) {
                        staffUUID = proxiedPlayer.getUniqueId();
                    } else {
                        staffUUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
                    }

                    //lädt die Anzahl der Bestrafungen für ein Template aus der Datenbank
                    Castigo.getPunishDatabaseManager().getReasonCount(userUUID, template, punishRegion, PunishType.TemplatePunish, result -> {
                        long banTime;
                        ArrayList<String> durationArray;

                        try {
                            //liest die Templates aus der Template Config aus
                            //Schmeißt Exception wenn das Template nicht gefunden wurde
                            durationArray = TemplateConverter.getBanDurationAsList(template, punishRegion);
                        } catch (PunishActionFaildException exception) {
                            commandSender.sendMessage(exception.getMessage());
                            return;
                        }

                        boolean isPerma = false;

                        //result = die Anzahl der Bestrafungen für ein Template
                        if (result > durationArray.size()) {
                            if(durationArray.get((durationArray.size() - 1)).equals("perma")){
                                isPerma = true;
                                banTime = 0L; //für eine valide BanTime
                            }else {
                                banTime = TimeCalculator.parseTimeFromFormat(durationArray.get((durationArray.size() - 1)));
                            }
                        } else {
                            if (result > 0) {
                                result = result - 1;
                                if(durationArray.get(result).equals("perma")){
                                    isPerma = true;
                                    banTime = 0L; //für eine valide BanTime
                                }else {
                                    banTime = TimeCalculator.parseTimeFromFormat(durationArray.get(result));
                                }

                            } else {
                                if(durationArray.get(0).equals("perma")){
                                    isPerma = true;
                                    banTime = 0L; //für eine valide BanTime
                                }else {
                                    banTime = TimeCalculator.parseTimeFromFormat(durationArray.get(0));
                                }
                            }
                        }
                        //Addiert die BanTime zu den aktuellen Millis um den finalen Endbannungs Milisekunden Timestamp zubekommen
                        banTime = banTime + System.currentTimeMillis();

                        Castigo.getInstance().getProxy().getPluginManager().callEvent(new PunishExecutedEvent(
                                new Punish(
                                        userUUID,
                                        staffUUID,
                                        punishRegion,
                                        PunishType.TemplatePunish,
                                        template,
                                        isPerma,
                                        System.currentTimeMillis(),
                                        banTime,
                                        false
                                ),
                                commandSender
                        ));
                    });
                });
            } else {
                sendSyntax(commandSender);
            }
        }
    }

    public void sendSyntax(CommandSender sender) {
        if(punishRegion == PunishRegion.NETWORK) {
            sender.sendMessage("§cBitte benutze §7/§eban §7[§bName§7] §7[§bTemplate§7]");
            sender.sendMessage("§7- §cVerfügbare Templates siehst du bei §7/§ebantemplates");
        }else if(punishRegion == PunishRegion.CHAT){
            sender.sendMessage("§cBitte benutze §7/§emute§7[§bName§7] §7[§bTemplate§7]");
            sender.sendMessage("§7- §cVerfügbare Templates siehst du bei §7/§emutetemplates");
        }
    }
}
