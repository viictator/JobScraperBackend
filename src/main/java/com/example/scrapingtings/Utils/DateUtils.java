package com.example.scrapingtings.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;

public class DateUtils {
    public static String toDaysAgo(String dateStr) {
        dateStr = dateStr.replace('/', '-');
        if (dateStr == null) return "N/A";
        dateStr = dateStr.trim();
        if (dateStr.toLowerCase().contains("ago")) return dateStr;

        try {
            // Accept dashes as date separator
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            LocalDate now = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(date, now);

            if (daysBetween < 0) return "In the future";
            if (daysBetween == 0) return "Today";
            if (daysBetween == 1) return "1 day ago";
            return daysBetween + " days ago";

        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse date: " + dateStr);
            return dateStr;  // fallback to original string if parse fails
        }
    }

}
