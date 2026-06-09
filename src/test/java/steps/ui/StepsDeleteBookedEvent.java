package steps.ui;

import hooks.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import pages.MyBookingPage;
import utils.LogUtils;

public class StepsDeleteBookedEvent {
    private final TestContext testContext;
    private final MyBookingPage myBookingPage;

    public StepsDeleteBookedEvent(TestContext testContext) {
        this.testContext = testContext;
        this.myBookingPage = new MyBookingPage();
    }

    public StepsDeleteBookedEvent() {
        this(new TestContext());
    }

    @Given("I note the booked event name")
    public void i_note_the_booked_event_name() {
        String bookedEventName = testContext.getSelectedEventName();

        if (bookedEventName == null || bookedEventName.isBlank()) {
            bookedEventName = myBookingPage.getFirstBookedEventName();
        }

        testContext.setNotedBookedEventName(bookedEventName);
        LogUtils.info("Noted booked event name: " + bookedEventName);
    }

    @And("I confirm booking deletion")
    public void i_confirm_booking_deletion() {
        myBookingPage.confirmBookingDeletion();
    }
}