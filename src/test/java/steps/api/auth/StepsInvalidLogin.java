package steps.api.auth;

import api.context.ApiTestContext;
import api.payloads.AuthPayloads;
import io.cucumber.java.en.Given;
import reports.AllureManager;
import utils.LogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class StepsInvalidLogin {
    private final ApiTestContext context = new ApiTestContext();

    @Given("I prepare invalid login API payload")
    public void i_prepare_invalid_login_api_payload() {
        context.setRequestPayload(AuthPayloads.invalidLoginPayload());
        String payloadLog = "Prepared invalid login payload: " + maskedPayload();
        LogUtils.info(payloadLog);
        AllureManager.attachText("Invalid Login Payload", payloadLog);
    }

    private String maskedPayload() {
        Map<String, Object> payload = new LinkedHashMap<>(context.getRequestPayload());
        payload.put("password", "****");
        return payload.toString();
    }
}
