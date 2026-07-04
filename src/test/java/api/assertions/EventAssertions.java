package api.assertions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.jsoup.Connection;

import java.io.File;

public class EventAssertions {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final File LIST_EVENTS_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/events/list-event-response-200.schema.json");
    private static final File LIST_EVENTS_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/events/list-event-response-500.schema.json");

    public void assertListEventsResponseSchema200(Response response) {
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(LIST_EVENTS_RESPONSE_200_SCHEMA));
    }
}
