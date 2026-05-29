package steps.ui;

import hooks.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.EventPage;

import java.util.List;

public class StepsFilterEventsByCategory {
    private TestContext testContext;
    private EventPage eventPage;

    public StepsFilterEventsByCategory(TestContext testContext) {
        this.testContext = testContext;
        this.eventPage = new EventPage();
    }

    public StepsFilterEventsByCategory() {
        this(new TestContext());
    }

    @When("I select {string} from category dropdown")
    public void i_select_from_category_dropdown(String category) {
        eventPage.selectEventCategory(category);
    }

    @Then("I should see only {string} events displayed")
    public void i_should_see_only_events_displayed(String expectedCategory) {
        List<WebElement> eventList = eventPage.getEvents();
        for (WebElement event : eventList) {
            String actualCategory = event.findElement(By.cssSelector("span.inline-flex")).getText().trim();

            WebUI.verifyEquals(actualCategory, expectedCategory);
        }
    }
}
