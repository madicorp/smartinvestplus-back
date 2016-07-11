package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.jongo.marshall.jackson.bson4jackson.BsonModule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 10:34
 */
public class LocalDateDeserializerTest {
    private LocalDateDeserializer subject = new LocalDateDeserializer();
    private JsonParser parser = mock(JsonParser.class);
    private DeserializationContext ctxt = mock(DeserializationContext.class);

    @Test
    public void should_deserialize_bson_date_time_to_local_date() throws Exception {
        // GIVEN
        byte[] july_8_2016_json_bin = "ISODATE(\"2016-07-08T00:00:00\")".getBytes("UTF-8");
        when(parser.getBinaryValue()).thenReturn(july_8_2016_json_bin);

        // WHEN
        LocalDate actual = subject.deserialize(parser, ctxt);

        // THEN
        Assertions.assertThat(actual).isEqualTo(LocalDate.of(2016, Month.JULY, 8));
    }
}
