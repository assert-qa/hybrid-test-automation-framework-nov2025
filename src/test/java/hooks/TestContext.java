package hooks;

import factory.DriverManager;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.RegisterPage;
import pages.models.EventBookDetailDataObject;

public class TestContext {
    private static final ThreadLocal<EventBookDetailDataObject> bookingData = new ThreadLocal<>();
    private static final ThreadLocal<String> selectedEventName = new ThreadLocal<>();

    private LoginPage loginPage;
    private RegisterPage registerPage;

    public TestContext() {
    }

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    public LoginPage getLoginPage() {
        return (loginPage == null) ? loginPage = new LoginPage() : loginPage;
    }

    public RegisterPage getRegisterPage() {
        return (registerPage == null) ? registerPage = new RegisterPage() : registerPage;
    }

    public EventBookDetailDataObject getBookingData() {
        return bookingData.get();
    }

    public void setBookingData(EventBookDetailDataObject data) {
        bookingData.set(data);
    }

    public String getSelectedEventName() {
        return selectedEventName.get();
    }

    public void setSelectedEventName(String eventName) {
        selectedEventName.set(eventName);
    }

    public static void reset() {
        bookingData.remove();
        selectedEventName.remove();
    }
}
