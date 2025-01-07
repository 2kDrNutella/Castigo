package de.drnutella.castigo.utils;

import de.drnutella.castigo.Castigo;
import de.drnutella.castigo.enums.PunishFeedback;
import de.drnutella.castigo.enums.PunishRegion;
import de.drnutella.castigo.exceptions.PunishActionFaildException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TemplateConverter {

    private final static JSONFileBuilder jsonHandler = Castigo.getTemplateJSONHandler();

    public static List<String> getTemplateList(PunishRegion punishRegion) {
        JSONObject templateObject;
        switch (punishRegion) {
            case CHAT -> templateObject = jsonHandler.getCustomSizeArray("MuteTemplates");
            case NETWORK -> templateObject = jsonHandler.getCustomSizeArray("BanTemplates");
            default -> {
                return new ArrayList<>();
            }
        }
        return new ArrayList<>(templateObject.keySet());
    }


    public static ArrayList<String> getBanDurationAsList(String template, PunishRegion punishRegion) throws PunishActionFaildException {

        JSONObject templateObject;
        switch (punishRegion){
            case CHAT -> templateObject = jsonHandler.getCustomSizeArray("MuteTemplates");
            case NETWORK -> templateObject = jsonHandler.getCustomSizeArray("BanTemplates");
            default -> {
                return new ArrayList<>(); //THE FAMOUS 'SHOULD NEVER HAPPEN CASE'
            }
        }

        if(!templateObject.containsKey(template)){
            throw new PunishActionFaildException(PunishFeedback.TEMPLATE_NOT_FOUND);
        }else {

            String durationString = (String) templateObject.get(template);

            ArrayList<String> result = new ArrayList<String>();

            String[] tokens = durationString.split(",");
            for (String token : tokens) {

                String suffix = "d";
                int number = 0;

                token = token.trim();

                if (!token.contains("perma")) {
                    String[] parts = token.split(" ");
                    if (parts[0].contains("d")) {
                        suffix = "d";
                        number = Integer.parseInt(parts[0].replace(suffix, ""));
                    } else if (parts[0].contains("h")) {
                        suffix = "h";
                        number = Integer.parseInt(parts[0].replace(suffix, ""));
                    } else if (parts[0].contains("m")) {
                        suffix = "m";
                        number = Integer.parseInt(parts[0].replace(suffix, ""));
                    } else if (parts[0].contains("s")) {
                        suffix = "s";
                        number = Integer.parseInt(parts[0].replace(suffix, ""));
                    }
                }

                if (token.endsWith(")")) {
                    int count = Integer.parseInt(token.substring(token.indexOf('(') + 1, token.length() - 1));

                    for (int i = 1; i < count + 1; i++) {
                        result.add((i * number) + suffix);
                    }


                } else if (token.endsWith("]")) {
                    int count = Integer.parseInt(token.substring(token.indexOf('[') + 1, token.length() - 1));

                    for (int i = 1; i < count + 1; i++) {
                        result.add(number + suffix);
                    }
                } else if (!token.contains("perma")) {
                    result.add(number + suffix);

                } else {
                    result.add("perma");
                }
            }

            return result;
        }
    }
}
