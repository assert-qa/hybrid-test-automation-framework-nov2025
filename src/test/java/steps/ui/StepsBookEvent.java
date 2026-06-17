package steps.ui;

import factory.BookingDataFactory;
import hooks.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import keywords.WebUI;
import org.openqa.selenium.WebElement;
import pages.EventPage;
import pages.MyBookingPage;
import pages.dto.EventBookDetailDataObject;
import pages.dto.SelectedEventDataObject;
import utils.LogUtils;

import java.util.List;
import java.util.Random;

public class StepsBookEvent {
    private TestContext testContext;
    private MyBookingPage myBookingPage;
    private EventPage eventPage;
    private EventBookDetailDataObject bookingData;

    public StepsBookEvent(TestContext testContext) {
        this.testContext = testContext;
        this.myBookingPage = new MyBookingPage();
        this.eventPage = new EventPage();
        this.bookingData = new EventBookDetailDataObject();
    }

    public StepsBookEvent() {
        this(new TestContext());
    }

    @And("I click on any available event card")
    public void i_click_on_any_available_event_card() {
       SelectedEventDataObject selectedEvent = eventPage.clickAnyAvailableEventAndGetData();
       testContext.setSelectedEventName(selectedEvent.getEventName());
       myBookingPage.waitForBookingFormDisplayed();
    }

    @And("I enter booking information")
    public void i_enter_booking_information() {
        Random genTicketNum = new Random();
        // non-eligible is when booking more than 1 ticket
        bookingData = BookingDataFactory.createBooking(genTicketNum.nextInt(9) + 2);
        testContext.setBookingData(bookingData);
        myBookingPage.fillBookingInformation(bookingData);
    }

    @When("I view my bookings")
    public void i_view_my_bookings(){
        myBookingPage.clickViewMyBookingsButton();
    }

    @Then("I should be redirected to the my bookings page")
    public void i_should_be_redirected_to_my_bookings_page() {
        String selectedEventName = testContext.getSelectedEventName();
        if (selectedEventName == null) {
            throw new IllegalStateException("Selected event name is not available.");
        }

        WebUI.verifyTextVisible("My Bookings");
        WebUI.verifyTextVisible(selectedEventName);

        String currentUrl = myBookingPage.getCurrentURL();
        WebUI.verifyTrue(currentUrl.contains("/bookings"),
                "Expected URL to contain /bookings, but current URL is: " + currentUrl);
    }

    @And("my booked events should be displayed")
    public void my_booked_events_should_be_displayed() {
        List<WebElement> bookingList = myBookingPage.getBookingList();
        WebUI.verifyTrue(!bookingList.isEmpty(), "No booked events are displayed");
        LogUtils.info("Total booked events: " +  bookingList.size());
    }
}
