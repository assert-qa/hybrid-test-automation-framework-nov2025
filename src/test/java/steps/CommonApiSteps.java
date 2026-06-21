package steps;

import api.assertions.AuthAssertions;
import api.client.ApiClient;
import api.context.ApiTestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import managers.ConfigManager;
import managers.EndpointManager;
import org.assertj.core.api.Assertions;
import reports.AllureManager;
import utils.LogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommonApiSteps {
    private final ApiTestContext context = new ApiTestContext();
    private final AuthAssertions authAssertions = new AuthAssertions();

    @Given("I set login API endpoint")
    public void i_set_login_api_endpoint() {
        Assertions.assertThat(EndpointManager.getEndpoint("login"))
                .as("Login API endpoint")
                .isNotBlank()
                .startsWith("/");
    }

    @And("I set request headers")
    public void i_set_request_headers() {
        Assertions.assertThat(ApiClient.requestSpec())
                .as("API request specification with JSON headers")
                .isNotNull();
    }

    @And("I prepare login API base configuration")
    public void i_prepare_login_api_base_configuration() {
        Assertions.assertThat(ConfigManager.getProperty("API_BASE_URL"))
                .as("API base URL")
                .isNotBlank()
                .startsWith("http");
    }

    @And("the login request body should match login request schema")
    public void the_login_request_body_should_match_login_request_schema() {
        authAssertions.assertLoginRequestSchema(context.getRequestPayload());
    }

    @When("I send {string} request to {string} API")
    public void i_send_request_to_api(String method, String endpointName) {
        String httpMethod = method.trim().toUpperCase();
        String endpoint = EndpointManager.getEndpoint(endpointName);
        logAndAttachApiRequest(httpMethod, endpointName, endpoint);

        Response response = switch(httpMethod){
            case "POST" -> ApiClient.request(context.getToken())
                    .body(context.getRequestPayload())
                    .post(endpoint);
            case "GET" -> ApiClient.request(context.getToken())
                    .get(endpoint);
            case "PUT" -> ApiClient.request(context.getToken())
                    .body(context.getRequestPayload())
                    .put(endpoint);
            case "PATCH" -> ApiClient.request(context.getToken())
                    .body(context.getRequestPayload())
                    .patch(endpoint);
            case "DELETE" -> ApiClient.request(context.getToken())
                    .delete(endpoint);
            default -> throw new IllegalArgumentException("Unsupported HTTP Method: " + method);
        };

        context.setResponse(response);
        logAndAttachApiResponse(httpMethod, endpointName, response);
    }

    @And("the login API response should match success schema")
    public void the_login_api_response_should_match_success_schema() {
        authAssertions.assertSuccessfulLoginResponse(
                context.getResponse(),
                (String) context.getRequestPayload().get("email")
        );
        context.setToken(context.getResponse().jsonPath().getString("token"));
        context.setUserId(context.getResponse().jsonPath().getInt("user.id"));
    }

    @Then("the API response status should be {int}")
    public void theLoginApiResponse_status_should_be(int statusCode) {
        authAssertions.assertStatusCode(context.getResponse(), statusCode);
    }

    private void logAndAttachApiRequest(String method, String endpointName, String endpoint) {
        String requestLog = "API REQUEST"
                + System.lineSeparator() + "Method: " + method
                + System.lineSeparator() + "Endpoint Name: " + endpointName
                + System.lineSeparator() + "Endpoint: " + endpoint
                + System.lineSeparator() + "Token Available: " + (context.getToken() != null && !context.getToken().isBlank())
                + System.lineSeparator() + "Payload: " + formatPayload();

        LogUtils.info(requestLog);
        AllureManager.attachText("API Request - " + method + " " + endpointName, requestLog);
    }

    private void logAndAttachApiResponse(String method, String endpointName, Response response) {
        String responseLog = "API RESPONSE"
                + System.lineSeparator() + "Request: " + method + " " + endpointName
                + System.lineSeparator() + "Status Code: " + response.statusCode()
                + System.lineSeparator() + "Status Line: " + response.statusLine()
                + System.lineSeparator() + "Headers: " + response.getHeaders()
                + System.lineSeparator() + "Body: " + maskSensitiveValues(response.asPrettyString());

        LogUtils.info(responseLog);
        AllureManager.attachText("API Response - " + method + " " + endpointName, responseLog);
    }

    private String formatPayload() {
        if (context.getRequestPayload() == null) {
            return "<empty>";
        }

        Map<String, Object> maskedPayload = new LinkedHashMap<>(context.getRequestPayload());
        if (maskedPayload.containsKey("password")) {
            maskedPayload.put("password", "******");
        }
        if (maskedPayload.containsKey("token")) {
            maskedPayload.put("token", "******");
        }
        return maskedPayload.toString();
    }

    private String maskSensitiveValues(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("(?i)(\"(?:password|token)\"\\s*:\\s*\")([^\"]*)(\")", "$1****$3");
    }
}
