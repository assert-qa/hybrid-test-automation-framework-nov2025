package steps.ui;

import hooks.TestContext;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import pages.MyBookingPage;

public class StepsEmptyBookingState {
    private TestContext testContext;
    private MyBookingPage myBookingPage;

    public StepsEmptyBookingState(TestContext testContext) {
        this.testContext = testContext;
        this.myBookingPage = new MyBookingPage();
    }

    public StepsEmptyBookingState() {
        this(new TestContext());
    }

    @Then("I should see {string} booking message")
    public void i_should_see_message(String expectedMessage) {
        WebUI.verifyEquals(myBookingPage.bookingEmptyStateLabel(), expectedMessage);
    }
}
