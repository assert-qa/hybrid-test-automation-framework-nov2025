package api.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.Map;

public class AuthAssertions {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final File LOGIN_REQUEST_SCHEMA =
            new File("src/test/java/api/schemas/auth/login-request.schema.json");
    private static final File REGISTER_REQUEST_SCHEMA =
            new File("src/test/java/api/schemas/auth/register-request.schema.json");
    private static final File REGISTER_RESPONSE_201_SCHEMA =
            new File("src/test/java/api/schemas/auth/register-response-201.schema.json");
    private static final File REGISTER_RESPONSE_400_SCHEMA =
            new File("src/test/java/api/schemas/auth/register-response-400.schema.json");
    private static final File LOGIN_RESPONSE_200_SCHEMA =
            new File("src/test/java/api/schemas/auth/login-response-200.schema.json");
    private static final File AUTH_RESPONSE_401_SCHEMA =
            new File("src/test/java/api/schemas/auth/get-auth-response-401.schema.json");
    private static final File LOGIN_RESPONSE_400_SCHEMA =
            new File("src/test/java/api/schemas/auth/login-response-400.schema.json");


    public void assertLoginRequestSchema(Map<String, Object> payload) {
        Assertions.assertThat(LOGIN_REQUEST_SCHEMA).exists();
        Assertions.assertThat(JsonSchemaValidator.matchesJsonSchema(LOGIN_REQUEST_SCHEMA).matches(toJson(payload)))
                .as("Login request payload should match schema")
                .isTrue();
    }

    public void assertRegisterRequestSchema(Map<String, Object> payload) {
        Assertions.assertThat(REGISTER_REQUEST_SCHEMA).exists();
        Assertions.assertThat(JsonSchemaValidator.matchesJsonSchema(REGISTER_REQUEST_SCHEMA).matches(toJson(payload)))
                .as("Register request payload should match schema")
                .isTrue();
    }

    public void assertLoginRequestDoesNotMatchSchema(Map<String, Object> payload) {
        Assertions.assertThat(LOGIN_REQUEST_SCHEMA).exists();
        Assertions.assertThat(JsonSchemaValidator.matchesJsonSchema(LOGIN_REQUEST_SCHEMA).matches(toJson(payload)))
                .as("Login request payload should not match schema")
                .isFalse();
    }

    public void assertStatusCode(Response response, int expectedStatusCode) {
        Assertions.assertThat(response)
                .as("API response should not be null")
                .isNotNull();
        Assertions.assertThat(response.statusCode())
                .as("API response status code")
                .isEqualTo(expectedStatusCode);
    }

    public void assertSuccessfulLoginResponse(Response response, String expectedEmail) {
        assertLoginResponseSchema200(response);
        assertAccessToken(response);
        assertUserObject(response, expectedEmail);
        Assertions.assertThat(response.jsonPath().getBoolean("success")).isTrue();
    }

    public void assertSuccessfulRegisterResponse(Response response, String expectedEmail) {
        assertRegisterResponseSchema201(response);
        assertAccessToken(response);
        assertUserObject(response, expectedEmail);
        Assertions.assertThat(response.jsonPath().getBoolean("success")).isTrue();
    }

    public void assertUnauthorizedLoginResponse(Response response) {
        assertResponseSchema(response, response.statusCode());
        Assertions.assertThat(response.jsonPath().getBoolean("success")).isFalse();
        Assertions.assertThat(response.jsonPath().getString("error")).isNotBlank();
    }

    public void assertAuthApiResponseSchema(String apiName, String schemaType, Response response, String expectedEmail) {
        String normalizedApiName = apiName.trim().toLowerCase();
        String normalizedSchemaType = schemaType.trim().toLowerCase();

        switch (normalizedApiName + ":" + normalizedSchemaType) {
            case "login:success" -> assertSuccessfulLoginResponse(response, expectedEmail);
            case "login:error" -> assertLoginErrorResponse(response);
            case "register:success" -> assertSuccessfulRegisterResponse(response, expectedEmail);
            case "register:error" -> assertRegisterErrorResponse(response);
            default -> throw new IllegalArgumentException(
                    "Unsupported auth API response schema: " + apiName + " " + schemaType
            );
        }
    }

    public void assertLoginResponseSchema200(Response response) {
        assertResponseMatchesSchema(response, LOGIN_RESPONSE_200_SCHEMA);
    }

    public void assertRegisterResponseSchema201(Response response) {
        assertResponseMatchesSchema(response, REGISTER_RESPONSE_201_SCHEMA);
    }

    public void assertAuthResponseSchema401(Response response) {
        assertResponseMatchesSchema(response, AUTH_RESPONSE_401_SCHEMA);
    }

    public void assertLoginResponseSchema400(Response response) {
        assertResponseMatchesSchema(response, LOGIN_RESPONSE_400_SCHEMA);
    }

    public void assertRegisterResponseSchema400(Response response) {
        assertResponseMatchesSchema(response, REGISTER_RESPONSE_400_SCHEMA);
    }

    public void assertResponseSchema(Response response, int statusCode) {
        switch (statusCode) {
            case 200 -> assertLoginResponseSchema200(response);
            case 400 -> assertLoginResponseSchema400(response);
            default -> throw new IllegalArgumentException("Unsupported auth response schema for status code: " + statusCode);
        }
    }

    public void assertLoginErrorResponse(Response response) {
        assertLoginResponseSchema400(response);
        assertErrorResponse(response);
    }

    public void assertRegisterErrorResponse(Response response) {
        assertRegisterResponseSchema400(response);
        assertErrorResponse(response);
    }


    // method helper

    public void assertUserObject(Response response, String expectedEmail) {
        Assertions.assertThat(response.jsonPath().getMap("user"))
                .as("User object")
                .isNotNull()
                .containsKey("id")
                .containsKey("email");
        Assertions.assertThat(response.jsonPath().getInt("user.id"))
                .as("User id")
                .isPositive();
        Assertions.assertThat(response.jsonPath().getString("user.email"))
                .as("User email")
                .isEqualTo(expectedEmail);
    }

    public void assertAccessToken(Response response) {
        Assertions.assertThat(response.jsonPath().getString("token"))
                .as("Access token")
                .isNotBlank()
                .contains(".");
    }

    private void assertErrorResponse(Response response) {
        Assertions.assertThat(response.jsonPath().getBoolean("success")).isFalse();
        Assertions.assertThat(response.jsonPath().getString("error")).isNotBlank();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize login payload for schema validation", e);
        }
    }

    private void assertResponseMatchesSchema(Response response, File schema) {
        Assertions.assertThat(schema).exists();
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
    }
}
