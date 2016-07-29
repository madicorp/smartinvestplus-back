package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.databind.SerializerProvider;
import de.undercouch.bson4jackson.BsonGenerator;
import de.undercouch.bson4jackson.serializers.BsonSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:13
 */
public class LocalDateSerializer extends BsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, BsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        // TODO rajouter information timezone quand elle sera dispo
        // TODO utiliser GregorianCalendar au lieu de Date à ce moment
        gen.writeDateTime(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}
