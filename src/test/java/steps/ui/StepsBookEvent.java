package steps.ui;

import factory.BookingDataFactory;
import hooks.TestContext;
import io.cucumber.java.en.And;
import pages.MyBookingPage;
import pages.models.EventBookDetailDataObject;

public class StepsBookEvent {
    private TestContext testContext;
    private MyBookingPage myBookingPage;

    public StepsBookEvent(TestContext testContext) {
        this.testContext = testContext;
        this.myBookingPage = new MyBookingPage();
    }

    public StepsBookEvent() {
        this(new TestContext());
    }

    @And("I enter booking information")
    public void i_enter_booking_information() {
        EventBookDetailDataObject bookingData = BookingDataFactory.createBooking();
        myBookingPage.fillBookingInformation(bookingData);
    }
}
