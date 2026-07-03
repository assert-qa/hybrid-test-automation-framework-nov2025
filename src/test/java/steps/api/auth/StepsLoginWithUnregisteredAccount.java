package steps.api.auth;

import api.context.ApiTestContext;
import api.payloads.AuthPayloads;
import io.cucumber.java.en.Given;
import reports.AllureManager;
import utils.LogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class StepsLoginWithUnregisteredAccount {
    private final ApiTestContext context = new ApiTestContext();

    @Given("I prepare unregistered login API payload")
    public void i_prepare_unregistered_login_api_payload() {
        context.setRequestPayload(AuthPayloads.unregisteredLoginPayload());
        String payloadLog = "Prepared unregistered login payload: " + maskedPayload();
        LogUtils.info(payloadLog);
        AllureManager.attachText("Unregistered Login Payload", payloadLog);
    }

    private String maskedPayload() {
        Map<String, Object> payload = new LinkedHashMap<>(context.getRequestPayload());
        if (payload.containsKey("password")) {
            payload.put("password", "******");
        }
        return payload.toString();
    }
}
