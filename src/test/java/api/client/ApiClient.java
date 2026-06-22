package api.client;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.Header;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import managers.ConfigManager;
import utils.LogUtils;

import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.LogConfig.logConfig;

public class ApiClient {
    private static final String DEFAULT_TIMEOUT_SECONDS = "20";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HIDDEN_VALUE = "HIDDEN";

    private ApiClient() {
        // Utility class
    }

    public static RequestSpecification request() {
        return RestAssured.given().spec(requestSpec());
    }

    public static RequestSpecification request(String token) {
        RequestSpecification request = request();
        if (token != null && !token.isBlank()) {
            request.auth().oauth2(token);
        }
        return request;
    }

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getProperty("API_BASE_URL"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(apiConfig())
                .addFilter(logOnFailure())
                .build();
    }

    public static ResponseSpecification responseSpec() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .addResponseSpecification(responseSpec())
                .expectStatusCode(statusCode)
                .build();
    }

    private static RestAssuredConfig apiConfig() {
        int timeoutInMilliseconds = getApiTimeoutInMilliseconds();
        return RestAssuredConfig.config()
                .httpClient(httpClientConfig()
                        .setParam("http.connection.timeout", timeoutInMilliseconds)
                        .setParam("http.socket.timeout", timeoutInMilliseconds)
                        .setParam("http.connection-manager.timeout", (long) timeoutInMilliseconds))
                .logConfig(logConfig()
                        .blacklistDefaultSensitiveHeaders()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL));
    }

    private static int getApiTimeoutInMilliseconds() {
        String timeout = ConfigManager.getProperty("API_TIMEOUT", DEFAULT_TIMEOUT_SECONDS).trim();
        return Integer.parseInt(timeout) * 1000;
    }

    private static Filter logOnFailure() {
        return (FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec,
                FilterContext context) -> {
            Response response = context.next(requestSpec, responseSpec);
            if (response.statusCode() >= 500) {
                logFailedRequest(requestSpec);
                logFailedResponse(response);
            }
            return response;
        };
    }

    private static void logFailedRequest(FilterableRequestSpecification requestSpec) {
        StringBuilder logMessage = new StringBuilder()
                .append("========== API REQUEST FAILED ==========")
                .append(System.lineSeparator())
                .append("Method: ").append(requestSpec.getMethod())
                .append(System.lineSeparator())
                .append("URI: ").append(requestSpec.getURI())
                .append(System.lineSeparator())
                .append("Headers: ").append(formatHeaders(requestSpec.getHeaders()));
        Object body = requestSpec.getBody();
        if (body != null) {
            logMessage.append(System.lineSeparator()).append("Body: ").append(maskSensitiveValues(body.toString()));
        }
        LogUtils.error(logMessage.toString());
    }

    private static void logFailedResponse(Response response) {
        String logMessage = "========== API RESPONSE FAILED =========="
                + System.lineSeparator()
                + "Status: " + response.statusLine()
                + System.lineSeparator()
                + "Headers: " + response.getHeaders()
                + System.lineSeparator()
                + "Body: " + maskSensitiveValues(response.asPrettyString());
        LogUtils.error(logMessage);
    }

    private static String formatHeaders(Iterable<Header> headers) {
        StringBuilder formattedHeaders = new StringBuilder("[");
        for (Header header : headers) {
            if (formattedHeaders.length() > 1) {
                formattedHeaders.append(", ");
            }
            formattedHeaders.append(header.getName()).append("=");
            if (AUTHORIZATION_HEADER.equalsIgnoreCase(header.getName())) {
                formattedHeaders.append(HIDDEN_VALUE);
            } else {
                formattedHeaders.append(header.getValue());
            }
        }
        return formattedHeaders.append("]").toString();
    }

    private static String maskSensitiveValues(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("(?i)(\"(?:password|token)\"\\s*:\\s*\")([^\"]*)(\")", "$1****$3");
    }
}
