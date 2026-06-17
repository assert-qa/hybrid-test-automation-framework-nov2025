package hooks;

import factory.DriverManager;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.RegisterPage;
import pages.dto.EventBookDetailDataObject;

// Maintain the state during scenario execution
public class TestContext {
    private static final ThreadLocal<EventBookDetailDataObject> bookingData = new ThreadLocal<>();
    private static final ThreadLocal<String> selectedEventName = new ThreadLocal<>();
    private static final ThreadLocal<Integer> selectedEventPrice = new ThreadLocal<>();
    private static final ThreadLocal<String> notedBookedEventName = new ThreadLocal<>();

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

    public Integer getSelectedEventPrice() {
        return selectedEventPrice.get();
    }

    public void setSelectedEventPrice(Integer price){
        selectedEventPrice.set(price);
    }

    public String getNotedBookedEventName() {
        return notedBookedEventName.get();
    }

    public void setNotedBookedEventName(String eventName) {
        notedBookedEventName.set(eventName);
    }

    public static void reset() {
        bookingData.remove();
        selectedEventName.remove();
        selectedEventPrice.remove();
        notedBookedEventName.remove();
    }
}
