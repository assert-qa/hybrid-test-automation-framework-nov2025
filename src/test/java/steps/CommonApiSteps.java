package steps;

import api.assertions.AuthAssertions;
import api.assertions.BookingAssertions;
import api.assertions.EventAssertions;
import api.client.ApiClient;
import api.context.ApiTestContext;
import helpers.ApiReportHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import managers.ConfigManager;
import managers.EndpointManager;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;

public class CommonApiSteps {
    private final ApiTestContext context = new ApiTestContext();
    private final AuthAssertions authAssertions = new AuthAssertions();
    private final EventAssertions eventAssertions = new EventAssertions();
    private final BookingAssertions bookingAssertions = new BookingAssertions();

    @Given("I set {string} API endpoint")
    public void i_set_login_api_endpoint(String endpointName) {
        String endPoint = EndpointManager.getEndpoint(endpointName);
        ApiReportHelper.attachEndpointEvidence(endpointName, endPoint);
        Assertions.assertThat(endPoint).as(endpointName + " API endpoint").isNotBlank().startsWith("/");
    }

    @And("I set request headers")
    public void i_set_request_headers() {
        ApiReportHelper.attachRequestHeadersEvidence();
        Assertions.assertThat(ApiClient.requestSpec()).as("API request specification with JSON headers").isNotNull();
    }

    @And("I prepare API base configuration")
    public void i_prepare_login_api_base_configuration() {
        ApiReportHelper.attachBaseConfigurationEvidence();
        Assertions.assertThat(ConfigManager.getProperty("API_BASE_URL")).as("API base URL").isNotBlank().startsWith("http");
    }

    @And("the login request body should match login request schema")
    public void the_login_request_body_should_match_login_request_schema() {
        ApiReportHelper.attachRequestSchemaEvidence("login-request.schema.json", context.getRequestPayload());
        authAssertions.assertLoginRequestSchema(context.getRequestPayload());
    }

    @When("I send {string} request to {string} API")
    public void i_send_request_to_api(String method, String endpointName) {
        String httpMethod = method.trim().toUpperCase();
        String endpoint = EndpointManager.getEndpoint(endpointName);
        Map<String, Object> requestPayload = context.getRequestPayload() == null ? Map.of() : context.getRequestPayload();
        ApiReportHelper.attachRequestEvidence(
                httpMethod, endpointName, endpoint, context.getToken(), requestPayload
        );

        Response response = switch(httpMethod){
            case "POST" -> requestWithPathParams(endpoint)
                    .body(requestPayload)
                    .post(endpoint);
            case "GET" -> requestWithPathParams(endpoint)
                    .queryParams(requestPayload)
                    .get(endpoint);
            case "PUT" -> requestWithPathParams(endpoint)
                    .body(requestPayload)
                    .put(endpoint);
            case "PATCH" -> requestWithPathParams(endpoint)
                    .body(requestPayload)
                    .patch(endpoint);
            case "DELETE" -> requestWithPathParams(endpoint)
                    .delete(endpoint);
            default -> throw new IllegalArgumentException("Unsupported HTTP Method: " + method);
        };

        context.setResponse(response);
        ApiReportHelper.attachResponseEvidence(httpMethod, endpointName, response);
    }

    @And("the {string} API response should match {string} schema")
    public void the_api_response_should_match_schema(String apiName, String schemaType) {
        ApiReportHelper.attachResponseSchemaEvidence(apiName, schemaType, context.getResponse());
        if (eventAssertions.supportsApi(apiName)) {
            eventAssertions.assertEventApiResponseSchema(apiName, schemaType, context.getResponse());
            return;
        }

        if (bookingAssertions.supportsApi(apiName)) {
            bookingAssertions.assertBookingApiResponseSchema(apiName, schemaType, context.getResponse());
            return;
        }

        if (authAssertions.supportsApi(apiName)) {
            authAssertions.assertAuthApiResponseSchema(
                    apiName, schemaType, context.getResponse(), getExpectedEmail()
            );
            if ("success".equalsIgnoreCase(schemaType)) {
                context.setToken(context.getResponse().jsonPath().getString("token"));
                context.setUserId(context.getResponse().jsonPath().getInt("user.id"));
            }
            return;
        }
        throw new IllegalArgumentException("Unsupported API response schema: " + apiName + " " + schemaType);
    }

    @Then("the API response status should be {int}")
    public void theLoginApiResponse_status_should_be(int statusCode) {
        ApiReportHelper.attachStatusEvidence(statusCode, context.getResponse());
        authAssertions.assertStatusCode(context.getResponse(), statusCode);
    }

    @And("the {string} API response error should be {string}")
    public void the_api_response_error_should_be(String apiName, String expectedError) {
        ApiReportHelper.attachErrorEvidence(apiName, expectedError, context.getResponse());
        Assertions.assertThat(context.getResponse().jsonPath().getString("error")).as(apiName + " API response error").isEqualTo(expectedError);
    }

    @And("the {string} API response message should be {string}")
    public void the_api_response_message_should_be(String apiName, String expectedMessage) {
        Assertions.assertThat(context.getResponse().jsonPath().getString("message")).as(apiName + " API response message").isEqualTo(expectedMessage);
    }

    @And("the {string} API response details should contain")
    public void the_api_response_details_should_contain(String apiName, DataTable dataTable) {
        List<Map<String, String>> expectedDetails = dataTable.asMaps(String.class, String.class);
        List<Map<String, Object>> actualDetails = context.getResponse().jsonPath().getList("details");

        Assertions.assertThat(actualDetails).as(apiName + " API response details").isNotNull();

        ApiReportHelper.attachDetailsEvidence(apiName, expectedDetails, actualDetails);

        for (Map<String, String> expectedDetail : expectedDetails) {
            Assertions.assertThat(actualDetails).as(apiName + " API response details should contain " + expectedDetail).anySatisfy(actualDetail -> Assertions.assertThat(actualDetail)
                            .containsEntry("field", expectedDetail.get("field")).containsEntry("message", expectedDetail.get("message")));
        }
    }

    @And("the {string} API response details should be empty")
    public void the_api_response_details_should_be_empty(String apiName) {
        List<Map<String, Object>> actualDetails = context.getResponse().jsonPath().getList("details");
        ApiReportHelper.attachEmptyDetailsEvidence(apiName, actualDetails);
        Assertions.assertThat(actualDetails).as(apiName + " API response details").isNotNull().isEmpty();
    }

    // method helper
    private String getExpectedEmail() {
        Map<String, Object> requestPayload = context.getRequestPayload();
        return requestPayload == null ? null : (String) requestPayload.get("email");
    }

    private RequestSpecification requestWithPathParams(String endpoint) {
        RequestSpecification request = ApiClient.request(context.getToken());
        if (endpoint.contains("{id}")) {
            Integer id = endpoint.startsWith("/bookings/")
                    ? context.getBookingId()
                    : context.getEventId();
            Assertions.assertThat(id).as("ID path parameter").isNotNull();

            request.pathParam("id", id);
        }
        if (endpoint.contains("{ref}")) {
            Assertions.assertThat(context.getBookingReferenceCode()).as("Booking reference path parameter").isNotBlank();
            request.pathParam("ref", context.getBookingReferenceCode());
        }
        return request;
    }
}
