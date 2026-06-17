package steps.ui;

import hooks.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import keywords.WebUI;
import pages.EventPage;
import pages.MyBookingPage;
import pages.dto.EventBookDetailDataObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepsViewBookedEventDetails {
    private TestContext testContext;
    private MyBookingPage myBookingPage;
    private EventPage eventPage;
    private EventBookDetailDataObject bookingData;

    public StepsViewBookedEventDetails(TestContext testContext) {
        this.testContext = testContext;
        this.myBookingPage = new MyBookingPage();
        this.eventPage = new EventPage();
        this.bookingData = new EventBookDetailDataObject();
    }

    public StepsViewBookedEventDetails() {
        this(new TestContext());
    }

    @Then("I should be redirected to booking detail page")
    public void i_should_be_redirected_to_booking_detail_page(){
        String selectedEventName = testContext.getSelectedEventName();
        if (selectedEventName == null) {
            throw new IllegalStateException("Selected event name is not available.");
        }

        WebUI.verifyTextVisible(selectedEventName);

        String currentUrl = myBookingPage.getCurrentURL();
        WebUI.verifyTrue(currentUrl.matches(".*/bookings/\\d+$"),
                "Expected URL to contain /bookings, but current URL is: " + currentUrl);
    }

    @Then("I should see customer information:")
    public void i_should_see_customer_information(DataTable dataTable) {
        List<String> fields = dataTable.asList();
        bookingData = testContext.getBookingData();

        if (bookingData == null) {
            throw new IllegalStateException("Booking data is not available. Create a booking before verifying customer information.");
        }

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

    @Then("the total paid amount should be calculated correctly")
    public void the_total_paid_amount_should_be_calculated_correctly() {
        bookingData = testContext.getBookingData();
        Integer selectedEventPrice = testContext.getSelectedEventPrice();

        if (bookingData == null){
            throw new IllegalStateException("Booking data is not available");
        }

        if (selectedEventPrice == null){
            throw new IllegalStateException("Selected event price is not available");
        }

        int expectedTotalPaidAmount = bookingData.getNumOfTickets() * selectedEventPrice;
        int actualTotalPaidAmount = myBookingPage.getCurrentTotalPaidAmount();

        WebUI.verifyEquals(actualTotalPaidAmount, expectedTotalPaidAmount);
    }
}
