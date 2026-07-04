package steps.api.events;

import api.context.ApiTestContext;
import helpers.ApiReportHelper;
import io.cucumber.java.en.Given;

import java.util.Map;

public class StepsGetEventById {
    private final ApiTestContext context = new ApiTestContext();

    @Given("I set event id path parameter to {int}")
    public void i_set_event_id_path_parameter_to(int eventId) {
        context.setEventId(eventId);
        ApiReportHelper.attachQueryParamsEvidence("Event ID Path Parameter", Map.of("id", eventId));
    }
}
