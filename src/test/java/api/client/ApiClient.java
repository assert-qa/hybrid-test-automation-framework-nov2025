package api.client;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import managers.ConfigManager;

public class ApiClient {
    public static RequestSpecification request() {
        return RestAssured
                .given()
                .baseUri(ConfigManager.getProperty("API_BASE_URL"))
                .contentType("application/json")
                .accept("application/json");
    }
}
