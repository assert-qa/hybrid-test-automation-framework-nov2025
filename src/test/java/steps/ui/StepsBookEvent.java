package steps.ui;

import factory.BookingDataFactory;
import hooks.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.EventPage;
import pages.MyBookingPage;
import pages.models.EventBookDetailDataObject;
import utils.LogUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepsBookEvent {
    private TestContext testContext;
    private MyBookingPage myBookingPage;
    private EventPage eventPage;
    private EventBookDetailDataObject bookingData;
    private static final String EVENT_NAME = "World Tech Summit"; // > Dilli Diwali Mela, Hollywood Monsoon Night — Los Angeles, World Tech Summit

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
        for (WebElement card : eventPage.getEvents()){
            String actualEventName = card.findElement(By.tagName("h3")).getText().trim();

            if (actualEventName.equalsIgnoreCase(EVENT_NAME)){
                card.click();
                return;
            }
        }
    }

    @And("I enter booking information")
    public void i_enter_booking_information() {
        bookingData = BookingDataFactory.createBooking();
        myBookingPage.fillBookingInformation(bookingData);
    }

    @And("my booked events should be displayed")
    public void my_booked_events_should_be_displayed() {
        List<WebElement> bookingList = myBookingPage.getBookingList();
        WebUI.verifyTrue(!bookingList.isEmpty(), "No booked events are displayed");
        LogUtils.info("Total booked events: " +  bookingList.size());
    }

    @Then("I should be redirected to booking detail page")
    public void i_should_be_redirected_to_booking_detail_page() {
        WebUI.verifyTextVisible(EVENT_NAME);
    }

    @And("I should see customer information:")
    public void i_should_see_customer_information(DataTable dataTable) {
        List<String> fields = dataTable.asList();

        Map<String, String> actualBookingInfo = myBookingPage.getCustomerInformation(fields);

        Map<String, String> expectedBookingInfo = new HashMap<>();
        expectedBookingInfo.put("Name", bookingData.getFullName());
        expectedBookingInfo.put("Email", bookingData.getEmail());
        expectedBookingInfo.put("Phone", bookingData.getPhoneNumber());
        expectedBookingInfo.put("Tickets", String.valueOf(bookingData.getNumOfTickets()));

        expectedBookingInfo.forEach((field, expectedValue) -> {
            String actualValue = actualBookingInfo.get(field);

            WebUI.verifyEquals(actualValue, expectedValue);
        });
    }
}
