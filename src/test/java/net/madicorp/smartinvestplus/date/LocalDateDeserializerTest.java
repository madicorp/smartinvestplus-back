package net.madicorp.smartinvestplus.date;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 10:34
 */
public class LocalDateDeserializerTest {
    private LocalDateDeserializer subject = new LocalDateDeserializer();

    @Test
    public void should_deserialize_bson_date_time_to_local_date() throws Exception {
        // GIVEN
        String july_8_2016_json = "{\"$date\":\"2016-07-08T00:00:00\"}";
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        LocalDate actual = objectMapper.readValue(july_8_2016_json, LocalDate.class);

        // THEN
        Assertions.assertThat(actual).isEqualTo(LocalDate.of(2016, Month.JULY, 8));
    }

    @Test(expected = JsonMappingException.class)
    public void should_reject_wrong_object_format() throws Exception {
        // GIVEN
        String july_8_2016_json = "{}";
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        objectMapper.readValue(july_8_2016_json, LocalDate.class);

        // THEN
        // should throw exception explaining that it expected $date field
    }

    @Test(expected = JsonMappingException.class)
    public void should_reject_object_if_another_field_is_provided() throws Exception {
        // GIVEN
        String july_8_2016_json = "{\"$date\":\"2016-07-08T00:00:00\", \"dummy\":\"dummy\"";
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        objectMapper.readValue(july_8_2016_json, LocalDate.class);

        // THEN
        // should throw exception explaining that it expected closed object
    }

    @Test
    public void should_deserialize_null_json_to_null_java() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = objectMapper(subject);

        // WHEN
        LocalDate actual = objectMapper.readValue("null", LocalDate.class);

        // THEN
        Assertions.assertThat(actual).isNull();
    }

    private static ObjectMapper objectMapper(LocalDateDeserializer subject) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule localDateModule = new SimpleModule();
        localDateModule.addDeserializer(LocalDate.class, subject);
        objectMapper.registerModule(localDateModule);
        return objectMapper;
    }

}
