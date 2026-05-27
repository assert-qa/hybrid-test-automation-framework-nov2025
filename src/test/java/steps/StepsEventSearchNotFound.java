package steps;

import hooks.TestContext;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import pages.EventPage;

public class StepsEventSearchNotFound {
    private TestContext testContext;
    private EventPage eventPage;

    public StepsEventSearchNotFound(TestContext testContext) {
        this.testContext = testContext;
        this.eventPage = new EventPage();
    }

    public StepsEventSearchNotFound() {
        this(new TestContext());
    }

    @Then("I should see {string} message")
    public void i_should_see_message(String expectedMessage) {
        WebUI.verifyEquals(eventPage.searchNotFound(), expectedMessage);
    }

}
