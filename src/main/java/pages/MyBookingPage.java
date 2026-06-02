package pages;

import factory.DriverFactory;
import keywords.WebUI;
import lombok.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.models.EventBookDetailDataObject;

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
    String totalEventPrice = setUp.getProperty("TOTAL_PRICE_TICKET");
    String listOfBookedEvents = setUp.getProperty("LIST_OF_BOOKED_EVENT");


    public void goToMyBookingPage(){
        WebUI.clickElement(By.xpath(bookingPage));
    }

    public String bookingEmptyStateLabel(){
        return WebUI.getElementText(By.xpath(emptyStateLabel));
    }

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
    public void fillBookingInformation(EventBookDetailDataObject data){
        enterNumOfTickets(data.getNumOfTickets());
        enterFullName(data.getFullName());
        enterEmail(data.getEmail());
        enterPhoneNumber(String.valueOf(data.getPhoneNumber()));
    }

    public void clickConfirmBookingButton(){
        WebUI.clickElement(By.xpath(confirmBookingButton));
    }

    public String successBookingLabel(){
        return WebUI.getElementText(By.xpath(successBookingLabel));
    }

    public List<WebElement> getBookingList(){
        return WebUI.getWebElements(By.xpath(listOfBookedEvents));
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



}