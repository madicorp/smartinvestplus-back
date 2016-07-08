package net.madicorp.smartinvestplus.test;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.springframework.test.util.JsonPathExpectationsHelper;

import java.text.ParseException;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 10:19
 */
public class JsonAssertion extends AbstractAssert<JsonAssertion, String> {
    private JsonAssertion(String actual) {
        super(actual, JsonAssertion.class);
    }

    public static JsonAssertion assertThat(String actual) {
        return new JsonAssertion(actual);
    }

    public <T> JsonAssertion contains(String jsonPath, T expectedValue) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(actual, expectedValue);
        return this;
    }

    public JsonAssertion hasSize(String jsonPath, int expectedSize) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(actual, Matchers.hasSize(expectedSize));
        return this;
    }

    public JsonAssertion isNullValue() throws ParseException {
        Assertions.assertThat(actual).isEqualTo("null");
        return this;
    }
}
