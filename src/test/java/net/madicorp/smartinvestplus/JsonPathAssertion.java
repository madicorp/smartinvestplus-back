package net.madicorp.smartinvestplus;

import org.assertj.core.api.AbstractAssert;
import org.hamcrest.Matchers;
import org.springframework.test.util.JsonPathExpectationsHelper;

import java.text.ParseException;

/**
 * User: sennen
 * Date: 03/07/2016
 * Time: 23:39
 */
public class JsonPathAssertion extends AbstractAssert<JsonPathAssertion, String> {

    public JsonPathAssertion(String actual) {
        super(actual, JsonPathAssertion.class);
    }

    public static JsonPathAssertion assertThat(String actual) {
        return new JsonPathAssertion(actual);
    }

    public <T> JsonPathAssertion contains(String jsonPath, T expectedValue) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(actual, expectedValue);
        return this;
    }

    public JsonPathAssertion hasSize(String jsonPath, int expectedSize) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(actual, Matchers.hasSize(expectedSize));
        return this;
    }
}
