package steps.api.auth;

import api.assertions.AuthAssertions;
import api.context.ApiTestContext;
import api.payloads.AuthPayloads;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class StepsSuccessLogin {
    private final ApiTestContext context = new ApiTestContext();
    private final AuthAssertions authAssertions = new AuthAssertions();

    @Given("I prepare valid login API payload")
    public void i_prepare_valid_login_api_payload() {
        context.setRequestPayload(AuthPayloads.validLoginPayload());
    }

    @And("the login API response should match error schema")
    public void the_login_api_response_should_match_error_schema() {
        authAssertions.assertUnauthorizedLoginResponse(context.getResponse());
    }
}
