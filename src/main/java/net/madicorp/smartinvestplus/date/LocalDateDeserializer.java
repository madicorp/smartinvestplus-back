package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:13
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private final Pattern isoDateLiteralPattern = Pattern.compile("ISODATE\\(\"(?<date>.*)\"\\)");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        byte[] binFormattedIsoDate = parser.getBinaryValue();
        String formattedIsoDate = new String(binFormattedIsoDate, "UTF-8").replace("\u0000", "");
        Matcher matcher = isoDateLiteralPattern.matcher(formattedIsoDate);
        if (matcher.find()) {
            // This part is for the mongo driver
            String formattedDate = matcher.group("date");
            try {
                return LocalDate.parse(formattedDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw context.weirdStringException(formattedDate, LocalDate.class, "Expected ISO-8601 formatted date.");
            }
        } else {
            // This part is for the API which may pass an ISO date directly
            try {
                return LocalDate.parse(formattedIsoDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw context
                    .weirdStringException(formattedIsoDate, LocalDate.class, "Expected ISO-8601 formatted date.");
            }
        }
    }
}
