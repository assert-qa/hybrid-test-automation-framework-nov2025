package api.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.Map;

public class EventAssertions {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final File LIST_EVENTS_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/events/list-event-response-200.schema.json");

    private static final File CREATE_NEW_EVENT_SCHEMA =
            new File("src/test/java/api/schemas/events/create-new-event-request.schema.json");

    private static final File CREATE_NEW_EVENT_RESPONSE_201_SCHEMA =
            new File("src/test/java/api/schemas/events/create-new-event-response-201.schema.json");

    private static final File CREATE_NEW_EVENT_RESPONSE_400_SCHEMA =
            new File("src/test/java/api/schemas/events/create-new-event-response-400.schema.json");

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

    private void assertResponseMatchesSchema(Response response, File schema) {
        Assertions.assertThat(schema).exists();
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
    }

    public void assertListEventsResponseSchema200(Response response) {
        assertResponseMatchesSchema(response, LIST_EVENTS_RESPONSE_200_SCHEMA);
    }

    public void assertCreateNewEventRequestSchema(Map<String, Object> payload) {
        Assertions.assertThat(CREATE_NEW_EVENT_SCHEMA).exists();
        Assertions.assertThat(JsonSchemaValidator.matchesJsonSchema(CREATE_NEW_EVENT_SCHEMA).matches(toJson(payload)))
                .as("Create new event request payload should match schema")
                .isTrue();
    }

    public void assertUpdateEventRequestSchema(Map<String, Object> payload) {
        Assertions.assertThat(UPDATE_EVENT_REQUEST_SCHEMA).exists();
        Assertions.assertThat(JsonSchemaValidator.matchesJsonSchema(UPDATE_EVENT_REQUEST_SCHEMA).matches(toJson(payload)))
                .as("Update event request payload should match schema")
                .isTrue();
    }

    public boolean supportsApi(String apiName) {
        String normalizedApiName = normalize(apiName);
        return normalizedApiName.contains("event");
    }

    public void assertEventApiResponseSchema(String apiName, String schemaType, Response response) {
        String normalizedApiName = normalize(apiName);
        String normalizedSchemaType = normalize(schemaType);
        int statusCode = response.statusCode();

        if ("events".equals(normalizedApiName) || "list events".equals(normalizedApiName)) {
            assertListEventsResponseSchema200(response);
            return;
        }

        if (normalizedApiName.contains("create")) {
            switch (statusCode) {
                case 201 -> assertResponseMatchesSchema(response, CREATE_NEW_EVENT_RESPONSE_201_SCHEMA);
                case 400 -> assertResponseMatchesSchema(response, CREATE_NEW_EVENT_RESPONSE_400_SCHEMA);
                default -> throw unsupportedEventSchema(apiName, schemaType, statusCode);
            }
            return;
        }

        if (normalizedApiName.contains("get")) {
            switch (statusCode) {
                case 200 -> assertResponseMatchesSchema(response, GET_EVENT_BY_ID_RESPONSE_200_SCHEMA);
                case 404 -> assertResponseMatchesSchema(response, GET_EVENT_BY_ID_RESPONSE_404_SCHEMA);
                case 500 -> assertResponseMatchesSchema(response, GET_EVENT_BY_ID_RESPONSE_500_SCHEMA);
                default -> throw unsupportedEventSchema(apiName, schemaType, statusCode);
            }
            return;
        }

        if (normalizedApiName.contains("update")) {
            switch (statusCode) {
                case 200 -> assertResponseMatchesSchema(response, UPDATE_EVENT_RESPONSE_200_SCHEMA);
                case 400 -> assertResponseMatchesSchema(response, UPDATE_EVENT_RESPONSE_400_SCHEMA);
                case 404 -> assertResponseMatchesSchema(response, UPDATE_EVENT_RESPONSE_404_SCHEMA);
                default -> throw unsupportedEventSchema(apiName, schemaType, statusCode);
            }
            return;
        }

        if (normalizedApiName.contains("delete")) {
            switch (statusCode) {
                case 200 -> assertResponseMatchesSchema(response, DELETE_EVENT_RESPONSE_200_SCHEMA);
                case 404 -> assertResponseMatchesSchema(response, DELETE_EVENT_RESPONSE_404_SCHEMA);
                case 500 -> assertResponseMatchesSchema(response, DELETE_EVENT_RESPONSE_500_SCHEMA);
                default -> throw unsupportedEventSchema(apiName, schemaType, statusCode);
            }
            return;
        }

        if ("success".equals(normalizedSchemaType) && statusCode == 200) {
            assertListEventsResponseSchema200(response);
            return;
        }

        throw unsupportedEventSchema(apiName, schemaType, statusCode);
    }

    // helper method
    private IllegalArgumentException unsupportedEventSchema(String apiName, String schemaType, int statusCode) {
        return new IllegalArgumentException(
                "Unsupported event API response schema: " + apiName + " " + schemaType + " " + statusCode
        );
    }

    private String normalize(String value) {
        return value.trim().replace("-", " ").replace("_", " ").toLowerCase();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize event payload for schema validation", e);
        }
    }
}
