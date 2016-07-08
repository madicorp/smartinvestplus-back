package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.madicorp.smartinvestplus.test.JsonAssertion;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 09:50
 */
public class LocalDateSerializerTest {
    private LocalDateSerializer subject = new LocalDateSerializer();

    @Test
    public void should_serialize_local_date_to_bson_date_time() throws Exception {
        // GIVEN
        LocalDate july_8_2016 = LocalDate.of(2016, Month.JULY, 8);
        String expectedDateValue = july_8_2016.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME);
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        String actual = serialize(july_8_2016, objectMapper);

        // THEN
        JsonAssertion.assertThat(actual).contains("$.$date", expectedDateValue);
    }

    @Test
    public void should_serialize_null_value_as_null() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        String actual = serialize(null, objectMapper);

        // THEN
        JsonAssertion.assertThat(actual).isNullValue();
    }

    private static ObjectMapper objectMapper(LocalDateSerializer subject) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule localDateModule = new SimpleModule();
        localDateModule.addSerializer(LocalDate.class, subject);
        objectMapper.registerModule(localDateModule);
        return objectMapper;
    }

    private String serialize(LocalDate july_8_2016, ObjectMapper objectMapper) throws IOException {
        StringWriter actualContainer = new StringWriter();
        objectMapper.writeValue(actualContainer, july_8_2016);
        return actualContainer.toString();
    }

}
