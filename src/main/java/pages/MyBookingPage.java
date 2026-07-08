package pages;

import factory.DriverFactory;
import factory.DriverManager;
import keywords.WebUI;
import lombok.NonNull;
import managers.ConfigManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dto.EventBookDetailDataObject;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class MyBookingPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public MyBookingPage(){
    }

    String bookingPage = setUp.getProperty("NAVIGATE_TO_MY_BOOKING_PAGE");
    String emptyStateLabel = setUp.getProperty("EMPTY_STATE_LABEL");
    String currentTicketQty = setUp.getProperty("CURRENT_TICKET_QTY");
    String addTicketButton =  setUp.getProperty("ADD_TICKET");
    String minusTicketButton =  setUp.getProperty("MIN_TICKET");
    String customerName = setUp.getProperty("CUSTOMER_NAME");
    String customerEmail = setUp.getProperty("CUSTOMER_EMAIL");
    String customerPhone = setUp.getProperty("CUSTOMER_PHONE");
    String confirmBookingButton = setUp.getProperty("CONFIRM_BOOKING_BUTTON");
    String successBookingLabel = setUp.getProperty("SUCCESS_BOOKING_LABEL");
    String viewMyBookingButton = setUp.getProperty("VIEW_MY_BOOKINGS");
    String listOfBookedEvents = setUp.getProperty("LIST_OF_BOOKED_EVENT");
    String viewDetailsButton = setUp.getProperty("VIEW_DETAILS_BUTTON");
    String currentTotalPaidLabel = setUp.getProperty("ACTUAL_TOTAL_PRICE");
    String clearAllBookingsTextButton = setUp.getProperty("DELETE_BOOKING_BUTTON");
    String cancelBookingButton = setUp.getProperty("CANCEL_BOOKING_BUTTON");
    String cancelConfirmButton = setUp.getProperty("CANCEL_CONFIRM_BUTTON");
    String refundSection = setUp.getProperty("REFUND_SECTION");
    String checkEligibleOrNoEligibleTextButton = setUp.getProperty("CHECK_ELIGIBLE_OR_NO_ELIGIBLE_BUTTON");


    public void goToMyBookingPage(){
        WebUI.clickElement(By.xpath(bookingPage));
    }

    public String bookingEmptyStateLabel(){
        return WebUI.getElementText(By.xpath(emptyStateLabel));
    }

    public String getCurrentURL(){
        return WebUI.getCurrentUrl();
    }

    // Book Information
    public int getCurrentTicketQuantity(){
        return Integer.parseInt(WebUI.getElementText(By.xpath(currentTicketQty)));
    }

    public void clickPlusButton(){
        WebUI.clickElement(By.xpath(addTicketButton));
    }

    public void clickMinusButton(){
        WebUI.clickElement(By.xpath(minusTicketButton));
    }

    public void enterFullName(String fullName){
        WebUI.setText(By.xpath(customerName), fullName);
    }

    public void enterEmail(String email){
        WebUI.setText(By.xpath(customerEmail), email);
    }

    public void enterPhoneNumber(String phoneNumber){
        WebUI.setText(By.xpath(customerPhone), phoneNumber);
    }

    public void enterNumOfTickets(int desiredQty) {
        if (desiredQty < 1 || desiredQty > 10) {
            throw new IllegalArgumentException("Ticket quantity must be between 1 and 10");
        }

        int currentQty = getCurrentTicketQuantity();

        while (currentQty < desiredQty) {
            clickPlusButton();
            currentQty++;
        }

        while (currentQty > desiredQty) {
            clickMinusButton();
            currentQty--;
        }
    }

    // Book method
    public void waitForBookingFormDisplayed(){
        WebUI.waitForElementVisible(By.xpath(currentTicketQty));
        WebUI.waitForElementVisible(By.xpath(customerName));
        WebUI.waitForElementVisible(By.xpath(customerEmail));
        WebUI.waitForElementVisible(By.xpath(customerPhone));
    }

    public void fillBookingInformation(EventBookDetailDataObject data){
        enterNumOfTickets(data.getNumOfTickets());
        enterFullName(data.getFullName());
        enterEmail(data.getEmail());
        enterPhoneNumber(String.valueOf(data.getPhoneNumber()));
    }

    public void clickConfirmBookingButton(){
        WebUI.clickElement(By.xpath(confirmBookingButton));
    }

    public String verifyBookingSuccess(){
        return WebUI.getElementText(By.xpath(successBookingLabel));
    }

    public List<WebElement> getBookingList(){
        return WebUI.getWebElements(By.cssSelector(listOfBookedEvents));
    }

    public Map<String, String> getEventInformation(List<String> expectedFields){
        return getInformation(expectedFields);
    }

    public Map<String, String> getCustomerInformation(List<String> expectedFields){
        return getInformation(expectedFields);
    }

    @NonNull
    private Map<String, String> getInformation(List<String> expectedFields) {
        Map<String, String> information = new HashMap<>();

        for (String field : expectedFields) {
            By valueLocator = By.xpath(
                    "//span[normalize-space()='" + field + "']/following-sibling::span"
            );

            information.put(field, WebUI.getElementText(valueLocator));
        }
        return information;
    }

    public void clickViewMyBookingsButton(){
        WebUI.clickElement(By.xpath(viewMyBookingButton));
    }

    public void clickViewDetailsButton(){
        WebUI.clickElement(By.xpath(viewDetailsButton));
    }

    public int getCurrentTotalPaidAmount(){
        String totalPaidText = WebUI.getElementText(By.cssSelector(currentTotalPaidLabel));
        String numericText = totalPaidText.replaceAll("[^\\d]", "");

        if (numericText.isEmpty()){
            throw new IllegalStateException("Total paid amount is not numeric: " + totalPaidText);
        }
        return Integer.parseInt(numericText);
    }

    public void clickClearAllBookingsTextButton(){
        WebUI.clickElement(By.xpath(clearAllBookingsTextButton));
    }

    public void confirmBookingDeletion(){
        Alert alert = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(ConfigManager.getExplicitWaitTimeout()))
                .until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    public void waitUntilBookingsCleared(){
        By bookingCardLocator = By.cssSelector(listOfBookedEvents);

        new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(ConfigManager.getExplicitWaitTimeout()))
                .until(driver -> WebUI.getWebElements(bookingCardLocator).isEmpty()
                        || WebUI.isElementDisplayed(By.xpath(emptyStateLabel)));
    }

    public boolean isClearAllBookingsTextButtonDisplayed(){
        return WebUI.isElementDisplayed(By.xpath(clearAllBookingsTextButton));
    }

    public String getFirstBookedEventName(){
        List<WebElement> bookings = getBookingList();

        if (bookings.isEmpty()) {
            throw new IllegalStateException("No booked events are displayed.");
        }

        return bookings.get(0).findElement(By.tagName("h3")).getText().trim();
    }

//    public boolean isBookedEventDisplayed(String bookedEventName){
//        return getBookingList().stream()
//                .anyMatch(booking -> booking.getText().contains(bookedEventName));
//    }

    public void clickCancelButton(){
        WebUI.clickElement(By.xpath(cancelBookingButton));
    }

    public void clickConfirmBookingCancellationButton(){
        WebUI.waitForElementClickable(By.xpath(cancelConfirmButton));
        WebUI.clickElement(By.xpath(cancelConfirmButton));
    }

    public void waitForRefundSectionDisplayed(){
        WebUI.scrollToPosition(0, 99999);
        WebUI.waitForElementVisible(By.xpath(refundSection));
    }

    public void clickCheckEligible(){
        By checkEligibleButton = By.xpath(checkEligibleOrNoEligibleTextButton);
        WebUI.scrollToPosition(0, 99999);
        WebUI.waitForElementVisible(checkEligibleButton);

        WebUI.clickElement(By.xpath(checkEligibleOrNoEligibleTextButton));
    }

}
