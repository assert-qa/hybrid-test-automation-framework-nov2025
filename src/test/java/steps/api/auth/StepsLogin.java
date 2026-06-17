package steps.api.auth;

import api.client.ApiClient;
import api.endpoints.AuthEndPoints;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import managers.ConfigManager;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StepsLogin {
    private Map<String, Object> payload;
    private Response response;

    @Given("I prepare valid login API payload")
    public void i_prepare_valid_login_api_payload() {
        payload = new HashMap<>();
        payload.put("email", ConfigManager.getValidLoginEmail());
        payload.put("password", ConfigManager.getValidLoginPassword());
    }

    @When("I send POST request to login API")
    public void i_send_POST_request_to_login_api() {
        response = ApiClient.request()
                .body(payload)
                .post(AuthEndPoints.LOGIN);
    }

    @Then("the login API response status should be {int}")
    public void theLoginApiResponse_status_should_be(int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    @Then("the login API response should contain access token")
    public void  theLoginApiResponse_should_contain_access_token() {
        String token = response.jsonPath().getString("token");
        assertThat(token).isNotBlank();
    }
}
