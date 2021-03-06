package net.madicorp.smartinvestplus.service.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * User: sennen
 * Date: 01/07/2016
 * Time: 00:39
 */
public class MustacheServiceTest {
    private MustacheService subject = new MustacheService();

    @Test
    public void should_return_mongo_indices() throws Exception {
        // GIVEN
        ListStringMustacheTemplate<String> mongoIndicesTemplate = subject.compileList("mongo_indices");
        List<String> fields = Arrays.asList("field1", "field2");

        // WHEN
        String mongoIndices = mongoIndicesTemplate.render(fields);

        // THEN
        Assertions.assertThat(mongoIndices).isEqualToIgnoringWhitespace("{\n\tfield1:1,\n\tfield2:1\n}");
    }

}
