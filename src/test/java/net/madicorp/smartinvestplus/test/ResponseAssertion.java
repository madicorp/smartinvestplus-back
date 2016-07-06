package net.madicorp.smartinvestplus.test;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.springframework.test.util.JsonPathExpectationsHelper;

import javax.ws.rs.core.Response;
import java.text.ParseException;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 22:28
 */
public class ResponseAssertion extends AbstractAssert<ResponseAssertion, Response> {
    private String actualJsonPayload;

    private ResponseAssertion(Response actual) {
        super(actual, ResponseAssertion.class);
    }

    public static ResponseAssertion assertThat(Response actual) {
        return new ResponseAssertion(actual);
    }

    public ResponseAssertion success() {
        isNotNull();
        return statusEquals(200);
    }

    public ResponseAssertion notFound() {
        isNotNull();
        return statusEquals(404);
    }

    public ResponseAssertion statusEquals(int statusCode) {
        isNotNull();
        Assertions.assertThat(actual.getStatus()).isEqualTo(statusCode);
        return this;
    }

    public <T> ResponseAssertion payloadIsEqualTo(String expectedRawPayload) throws ParseException {
        isNotNull();
        Assertions.assertThat(getPayload()).isEqualTo(expectedRawPayload);
        return this;
    }

    public <T> ResponseAssertion contains(String jsonPath, T expectedValue) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(getPayload(), expectedValue);
        return this;
    }

    public ResponseAssertion hasSize(String jsonPath, int expectedSize) throws ParseException {
        isNotNull();
        JsonPathExpectationsHelper jsonPathExpectationsHelper = new JsonPathExpectationsHelper(jsonPath);
        jsonPathExpectationsHelper.assertValue(getPayload(), Matchers.hasSize(expectedSize));
        return this;
    }

    private String getPayload() {
        if (actualJsonPayload == null) {
            actualJsonPayload = actual.readEntity(String.class);
        }
        return actualJsonPayload;
    }
}