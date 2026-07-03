package steps.api.auth;

import api.assertions.AuthAssertions;
import api.context.ApiTestContext;
import api.payloads.AuthPayloads;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class StepsRegisterNewUser {
    private final ApiTestContext context = new ApiTestContext();
    private final AuthAssertions authAssertions = new AuthAssertions();

    @Given("I prepare register API payload")
    public void i_prepare_register_api_payload(){
        context.setRequestPayload(AuthPayloads.registerPayload());
    }

    @Given("I prepare register API payload with invalid email format")
    public void i_prepare_register_api_payload_with_invalid_email_format() {
        context.setRequestPayload(AuthPayloads.registerPayloadWithInvalidEmailFormat());
    }

    @Given("I prepare register API payload with password too short")
    public void i_prepare_register_api_payload_with_password_too_short() {
        context.setRequestPayload(AuthPayloads.registerPayloadWithPasswordTooShort());
    }

    @Given("I prepare register API payload with invalid email format and password too short")
    public void i_prepare_register_api_payload_with_invalid_email_format_and_password_too_short() {
        context.setRequestPayload(AuthPayloads.registerPayloadWithInvalidEmailFormatAndPasswordTooShort());
    }

    @Given("I prepare register API payload with registered email")
    public void i_prepare_register_api_payload_with_registered_email() {
        context.setRequestPayload(AuthPayloads.registerPayloadWithRegisteredEmail());
    }

    @And("the register request body should match register request schema")
    public void the_register_request_body_should_match_register_request_schema() {
        authAssertions.assertRegisterRequestSchema(context.getRequestPayload());
    }
}
