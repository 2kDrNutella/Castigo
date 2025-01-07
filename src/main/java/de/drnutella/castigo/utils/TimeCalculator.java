package de.drnutella.castigo.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeCalculator {

    public static String convertMillisToReadableTime(long millis) {
        // Berechnungen
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        // Restzeit berechnen
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        // Dynamischen Zeit-String aufbauen
        StringBuilder timeString = new StringBuilder();
        if (days > 0) {
            timeString.append(days).append(" Tage, ");
        }
        if (hours > 0) {
            timeString.append(hours).append(" Stunden, ");
        }
        if (minutes > 0) {
            timeString.append(minutes).append(" Minuten, ");
        }
        if (seconds > 0 || timeString.length() == 0) {
            timeString.append(seconds).append(" Sekunden");
        } else {
            // Entferne letztes Komma und Leerzeichen
            timeString.setLength(timeString.length() - 2);
        }

        return timeString.toString();
    }

    public static long fromTimeStringToLong(int days, int hours, int minutes, int seconds) {
        long new_millis;
        long new_hours;
        long new_minutes;
        long new_seconds;

        new_hours = days * 24 + hours;
        new_minutes = new_hours * 60 + minutes;
        new_seconds = new_minutes * 60 + seconds;
        new_millis = new_seconds * 1000;

        return (new_millis);
    }

    public static long parseTimeFromFormat(String timeString) {
        String[] parts = timeString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        long time = 0;
        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            String unit = parts[i + 1];

            switch (unit) {
                case "s":
                    time += value * 1000L;
                    break;
                case "m":
                    time += value * 60L * 1000L;
                    break;
                case "h":
                    time += value * 60L * 60L * 1000L;
                    break;
                case "d":
                    time += value * 24L * 60L * 60L * 1000L;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid time unit: " + unit);
            }
        }

        return time;
    }
    public static String getDateStringFromLong(Long time, String format){
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDate = date.format(formatter);
        return formattedDate.toString();
    }
}
