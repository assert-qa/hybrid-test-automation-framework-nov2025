package steps;

import factory.BookingDataFactory;
import factory.DriverManager;
import helpers.PopupHelper;
import hooks.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import keywords.WebUI;
import org.openqa.selenium.By;
import pages.EventPage;
import pages.LoginPage;
import pages.MyBookingPage;
import pages.RegisterPage;
import pages.models.EventBookDetailDataObject;
import pages.models.SelectedEventDataObject;

import java.util.Locale;
import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class CommonSteps {

    private final TestContext testContext;
    private final LoginPage loginPage = new LoginPage();
    private final RegisterPage registerPage = new RegisterPage();
    private final EventPage eventPage = new EventPage();
    private final MyBookingPage myBookingPage  = new MyBookingPage();
    private final Properties setUp = loadAllFiles();

    public CommonSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    public CommonSteps() {
        this(new TestContext());
    }

    @Given("I launch the browser")
    public void iLaunchTheBrowser() {
        WebUI.verifyTrue(DriverManager.getDriver() != null,
                "WebDriver is not initialized. Please check CucumberHooks @Before setup.");
    }

    @When("I navigate to url {string}")
    public void iNavigateToUrl(String url) {
        WebUI.openURL(url);
    }

    @Then("I verify that {string} is visible successfully")
    public void iVerifyThatLoginPageIsVisibleSuccessfully(String pageTitle) {
        String normalizedTitle = pageTitle.trim().toLowerCase(Locale.ROOT);

        switch (normalizedTitle){
            case "sign in to eventhub" -> WebUI.verifyElementVisible(By.xpath(setUp.getProperty("LOGIN_PAGE_LABEL")), "Sign in to EventHub is not visible.");
            case "create your account" -> WebUI.verifyElementVisible(By.xpath(setUp.getProperty("REGISTER_PAGE_LABEL")), "Register page label is not visible.");
            case "upcoming events" -> WebUI.verifyElementVisible(By.xpath(setUp.getProperty("EVENT_PAGE_LABEL")), "Event page label is not visible.");
            case "my bookings" -> WebUI.verifyElementVisible(By.xpath(setUp.getProperty("MY_BOOKING_PAGE_LABEL")), "My Bookings page label is not visible");
            default -> throw new IllegalArgumentException("unsupported page title in common step: " + pageTitle);
        }
    }

    @When("I click {string} button")
    public void iClickButton(String buttonName) {
        String normalizedButton = buttonName.trim().toLowerCase(Locale.ROOT);

        switch (normalizedButton) {
            case "sign in", "login" -> {
                loginPage.clickSignInButton();
                // Reduced sleep to catch toast faster
                WebUI.sleep(0.3);
                PopupHelper.handlePasswordManagerPopupCombined();
            }
            case "log out", "logout" -> loginPage.clickLogOutButton();
            case "register" -> registerPage.goToRegisterPage();
            case "create account" -> registerPage.createAccountButton();
            case "add new event" -> eventPage.clickAddNewEventButton();
            case "add event" -> eventPage.clickAddEventButton();
            case "confirm booking" -> myBookingPage.clickConfirmBookingButton();
            case "view details" -> myBookingPage.clickViewDetailsButton();
            case "clear all bookings" -> myBookingPage.clickClearAllBookingsTextButton();
            case "cancel booking" -> myBookingPage.clickCancelButton();
            case "yes, cancel it" -> myBookingPage.clickConfirmBookingCancellationButton();
            default -> throw new IllegalArgumentException("Unsupported button in common step: " + buttonName);
        }
    }

    @And("I navigate to {string} menu")
    public void iNavigateToMenu(String menuName) {
        String normalizedMenu = menuName.trim().toLowerCase(Locale.ROOT);

        switch (normalizedMenu) {
            case "events" -> {
                PopupHelper.handlePasswordManagerPopupCombined();
                WebUI.sleep(1);
                eventPage.goToEventPage();
            }
            case "my bookings" -> myBookingPage.goToMyBookingPage();
            default -> throw new IllegalArgumentException("Unsupported menu in common step: " + menuName);
        }
    }

    @When("I enter {string} in search field")
    public void i_enter_in_search_field(String keyword) {
        eventPage.searchEvent(keyword);
    }

    @When("I press enter")
    public void i_press_enter() {
        eventPage.pressEnter();
    }

    @When("I select {string} from category dropdown")
    public void i_select_from_category_dropdown(String category) {
        eventPage.selectEventCategory(category);
    }

    @And("I select {string} from city dropdown")
    public void i_select_from_city_dropdown(String city) {
        eventPage.selectEventCity(city);
    }

    @Then("I should see the {string} message")
    public void i_should_see_the_message(String message) {
        WebUI.verifyTextVisible(message);
    }

    // Booking
    @Given("I have an existing booking")
    public void i_have_an_existing_booking(){
        eventPage.goToEventPage();
        SelectedEventDataObject selectedEvent = eventPage.clickAnyAvailableEventAndGetData();
        // get selected event and it's price in TestContext
        testContext.setSelectedEventName(selectedEvent.getEventName());
        testContext.setSelectedEventPrice(selectedEvent.getEventPrice());

        createAndFillBookingInformation();

        myBookingPage.clickConfirmBookingButton();
        WebUI.verifyEquals(myBookingPage.verifyBookingSuccess(), "Your tickets are reserved.");
    }

    private void createAndFillBookingInformation() {
        EventBookDetailDataObject bookingData = BookingDataFactory.createBooking();
        testContext.setBookingData(bookingData);
        myBookingPage.fillBookingInformation(bookingData);
    }

    @Then("no booked event should no longer appear in My Bookings")
    public void no_booked_event_should_no_longer_appear_in_my_bookings() {
        myBookingPage.waitUntilBookingsCleared();

        WebUI.verifyTrue(myBookingPage.getBookingList().isEmpty(),
                "Bookings are still displayed after clearing all bookings.");
    }
}
