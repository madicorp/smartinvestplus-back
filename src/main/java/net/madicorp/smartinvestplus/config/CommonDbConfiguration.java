package net.madicorp.smartinvestplus.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.DB;
import de.undercouch.bson4jackson.BsonModule;
import net.madicorp.smartinvestplus.domain.util.JSR310DateConverters;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sennen
 * Date: 06/07/2016
 * Time: 11:33
 */
@Configuration
public class CommonDbConfiguration {
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(JSR310DateConverters.DateToZonedDateTimeConverter.INSTANCE);
        converterList.add(JSR310DateConverters.ZonedDateTimeToDateConverter.INSTANCE);
        converterList.add(JSR310DateConverters.DateToLocalDateConverter.INSTANCE);
        converterList.add(JSR310DateConverters.LocalDateToDateConverter.INSTANCE);
        converterList.add(JSR310DateConverters.DateToLocalDateTimeConverter.INSTANCE);
        converterList.add(JSR310DateConverters.LocalDateTimeToDateConverter.INSTANCE);
        return new CustomConversions(converterList);
    }

    Jongo jongo(DB db) {
        return new Jongo(db, jongoMapper());
    }

    public static Mapper jongoMapper() {
        return new JacksonMapper.Builder()
            .registerModule(new BsonModule())
            .registerModule(new JavaTimeModule())
            .setVisibilityChecker(new VisibilityChecker.Std(
                JsonAutoDetect.Visibility.PUBLIC_ONLY).withFieldVisibility(JsonAutoDetect.Visibility.NONE))
            .build();
    }
}
