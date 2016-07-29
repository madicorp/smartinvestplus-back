package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import de.undercouch.bson4jackson.BsonParser;
import de.undercouch.bson4jackson.deserializers.BsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:13
 */
public class LocalDateDeserializer extends BsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(BsonParser bsonParser,
                                 DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Date date = (Date) bsonParser.getEmbeddedObject();
        // TODO rajouter information timezone quand elle sera dispo
        // TODO utiliser GregorianCalendar au lieu de Date à ce moment
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
