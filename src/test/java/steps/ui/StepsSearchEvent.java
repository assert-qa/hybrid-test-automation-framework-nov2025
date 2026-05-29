package steps.ui;

import hooks.TestContext;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.EventPage;

import java.util.List;

public class StepsSearchEvent {
    private TestContext testContext;
    private EventPage eventPage;

    public StepsSearchEvent(TestContext testContext) {
        this.testContext = testContext;
        this.eventPage = new EventPage();
    }

    public StepsSearchEvent() {
        this(new TestContext());
    }

    @Then("I should see event related to {string}")
    public void i_should_see_event_related_to(String keyword) {
        List<WebElement> eventList = eventPage.getEvents();
        for (WebElement event : eventList) {
            String actualEventName = event.findElement(By.tagName("h3")).getText();

            if (actualEventName.equalsIgnoreCase(keyword)) {
                WebUI.verifyEquals(actualEventName, keyword);
                return;
            }
        }
    }
}
