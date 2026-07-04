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

    private static final File CREATE_NEW_EVENT_SCHEMA =
            new File("src/test/java/api/schemas/events/create-new-event-request.schema.json");

    private static final File GET_EVENT_BY_ID_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/events/get-event-response-200.schema.json");

    private static final File GET_EVENT_BY_ID_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/events/get-event-response-404.schema.json");

    private static final File GET_EVENT_BY_ID_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/events/get-event-response-500.schema.json");

    private static final File UPDATE_EVENT_REQUEST_SCHEMA =
            new File("src/test/java/api/schemas/events/update-event-request.schema.json");

    private static final File UPDATE_EVENT_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/events/update-event-response-200.schema.json");

    private static final File UPDATE_EVENT_RESPONSE_400_SCHEMA =
            new File("src/test/java/api/schemas/events/update-event-response-400.schema.json");

    private static final File UPDATE_EVENT_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/events/update-event-response-404.schema.json");

    private static final File DELETE_EVENT_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/events/delete-event-response-200.schema.json");

    private static final File DELETE_EVENT_RESPONSE_404_SCHEMA =
            new File("src/test/java/api/schemas/events/delete-event-response-404.schema.json");

    private static final File DELETE_EVENT_RESPONSE_500_SCHEMA =
            new File("src/test/java/api/schemas/events/delete-event-response-500.schema.json");

    public void assertListEventsResponseSchema200(Response response) {
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(LIST_EVENTS_RESPONSE_200_SCHEMA));
    }


}
