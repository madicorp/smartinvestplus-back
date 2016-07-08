package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:13
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.isExpectedStartObjectToken()) {
            JsonToken token = parser.nextToken();
            String tokenTextValue = parser.getText();
            if (token != JsonToken.FIELD_NAME || !"$date" .equals(tokenTextValue)) {
                String errorMsg =
                    String.format("Expected $date field, received '%s' token and '%s' value for token",
                                  token, tokenTextValue);
                throw context.wrongTokenException(parser, JsonToken.FIELD_NAME, errorMsg);
            }
            String parsedDate = parser.nextTextValue();
            token = parser.nextToken();
            if (JsonToken.END_OBJECT != token) {
                String errorMsg =
                    String.format("Expected $date field to be the only one, but received '%s' token",
                                  token);
                throw context.wrongTokenException(parser, JsonToken.END_OBJECT, errorMsg);
            }
            return LocalDate.parse(parsedDate, DateTimeFormatter.ISO_DATE_TIME);
        }
        throw context.wrongTokenException(parser, JsonToken.START_OBJECT, "Expected array or string.");
    }
}
