package net.madicorp.smartinvestplus.test;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import javax.ws.rs.core.Response;
import java.text.ParseException;

/**
 * User: sennen
 * Date: 05/07/2016
 * Time: 22:28
 */
public class ResponseAssertion extends AbstractAssert<ResponseAssertion, Response> {
    private String actualJsonPayload;
    private JsonAssertion jsonAssertion;

    private ResponseAssertion(Response actual) {
        super(actual, ResponseAssertion.class);
    }

    public static ResponseAssertion assertThat(Response actual) {
        return new ResponseAssertion(actual);
    }

    public ResponseAssertion ok() {
        isNotNull();
        return statusEquals(200);
    }

    public ResponseAssertion notFound() {
        isNotNull();
        return statusEquals(404);
    }

    public ResponseAssertion created() {
        isNotNull();
        return statusEquals(201);
    }

    public ResponseAssertion badRequest() {
        isNotNull();
        return statusEquals(400);
    }

    public ResponseAssertion noContent() {
        statusEquals(204);
        return this;
    }

    private ResponseAssertion statusEquals(int statusCode) {
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
        getJsonAssertion().contains(jsonPath, expectedValue);
        return this;
    }

    public ResponseAssertion hasSize(String jsonPath, int expectedSize) throws ParseException {
        getJsonAssertion().hasSize(jsonPath, expectedSize);
        return this;
    }

    public ResponseAssertion location(String path) {
        Assertions.assertThat(actual.getLocation()).hasPath(path).hasScheme("http");
        return this;
    }

    private JsonAssertion getJsonAssertion() {
        if (jsonAssertion == null) {
            jsonAssertion = JsonAssertion.assertThat(getPayload());
        }
        return jsonAssertion;
    }

    private String getPayload() {
        if (actualJsonPayload == null) {
            actualJsonPayload = actual.readEntity(String.class);
        }
        return actualJsonPayload;
    }
}
