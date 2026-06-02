package steps;

import factory.DriverManager;
import helpers.PopupHelper;
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

import java.util.Locale;
import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class CommonSteps {

    private final LoginPage loginPage = new LoginPage();
    private final RegisterPage registerPage = new RegisterPage();
    private final EventPage eventPage = new EventPage();
    private final MyBookingPage myBookingPage  = new MyBookingPage();
    private final Properties setUp = loadAllFiles();

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

}