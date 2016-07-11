package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:13
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        String dateLiteral = "ISODATE(\"" + value.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME) + "\")";
        gen.writeBinary(dateLiteral.getBytes("UTF-8"));
    }
}
