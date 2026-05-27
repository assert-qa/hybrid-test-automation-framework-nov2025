package pages;

import factory.DriverFactory;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pages.models.NewEventDataObject;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static helpers.PropertiesHelper.loadAllFiles;

public class EventPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public EventPage(){
    }

    String eventPage = setUp.getProperty("NAVIGATE_TO_EVENT_PAGE");
    String searchEvent = setUp.getProperty("SEARCH_EVENT_INPUT");
    String eventList = setUp.getProperty("EVENT_LIST");
    String selectEventCategory = setUp.getProperty("SELECT_EVENT_CATEGORY");
    String selectEventCity = setUp.getProperty("SELECT_EVENT_CITY");
    String clearFilterButton = setUp.getProperty("CLEAR_FILTER");
    String addNewEventButton = setUp.getProperty("ADD_NEW_EVENT");

    String createEventPage = setUp.getProperty("CREATE_EVENT_PAGE");
    String eventTitle = setUp.getProperty("EVENT_TITLE");
    String eventDescription = setUp.getProperty("EVENT_DESCRIPTION");
    String eventCategory = setUp.getProperty("EVENT_CATEGORY");
    String eventCity = setUp.getProperty("EVENT_CITY");
    String eventVenue = setUp.getProperty("EVENT_VENUE");
    String eventDate = setUp.getProperty("EVENT_DATE");
    String eventPrice = setUp.getProperty("EVENT_PRICE");
    String eventSeat = setUp.getProperty("EVENT_SEAT");
    String eventImageURL = setUp.getProperty("EVENT_IMAGE_URL");
    String addEventButton = setUp.getProperty("ADD_EVENT_BUTTON");
    String eventNotFoundMessage = setUp.getProperty("NO_EVENT_FOUND_MESSAGE");


    String confirmBookingButton = setUp.getProperty("CONFIRM_BOOKING_BUTTON");
    String totalEventPrice = setUp.getProperty("TOTAL_PRICE_TICKET");

    String eventInformationDetail = setUp.getProperty("EVENT_INFORMATION_DETAIL");
    String aboutEventDetail = setUp.getProperty("ABOUT_EVENT_DETAIL");

    // Event Page
    public void goToEventPage(){
        WebUI.clickElement(By.xpath(eventPage));
    }

    public void listOfEvents(){
        List<WebElement> events = WebUI.getWebElements(By.cssSelector(eventList));

        for (WebElement event : events){
            if (!events.isEmpty()){
                WebUI.verifyTrue(event.isDisplayed());
            }else {
                WebUI.verifyFalse(event.isDisplayed());
            }
        }
    }

    public void selectEventCategory(String category){
         Select dropDown = new Select(WebUI.getWebElement(By.cssSelector(selectEventCategory)));
         dropDown.selectByVisibleText(category);
    }

    public void selectEventCity(String city){
        Select dropDown = new Select(WebUI.getWebElement(By.cssSelector(selectEventCity)));
        dropDown.selectByVisibleText(city);
    }

    public List<WebElement> getEvents(){
        return WebUI.getWebElements(By.cssSelector(eventList));
    }

    // Event detail
    public void isEventInformationDisplayed(){
        List <WebElement> eventInformation = WebUI.getWebElements(By.cssSelector(eventInformationDetail));

        if (!eventInformation.isEmpty()) {
            LogUtils.info("Event information section is displayed");
        }else {
            LogUtils.info("Event information section is not displayed");
        }
    }

    public void isEventDetailDisplayed() {
        if (WebUI.isElementDisplayed(By.xpath(aboutEventDetail))) {
            LogUtils.info("About Event Detail is displayed");
        } else {
            LogUtils.error("About Event Detail is not displayed");
        }
    }

    // Add Event
    public void clickAddNewEventButton(){
        WebUI.clickElement(By.xpath(addNewEventButton));
    }

    public void createEventPage(){
        WebUI.verifyTrue(WebUI.isElementDisplayed(By.xpath(createEventPage)));
    }

    public void clickAddEventButton(){
        WebUI.clickElement(By.xpath(addEventButton));
    }

    public void createNewEventForm(NewEventDataObject data){
        WebUI.setText(By.xpath(eventTitle), data.getEventTitle());
        WebUI.setText(By.xpath(eventDescription), data.getEventDescription());

        Select dropDown = new Select(WebUI.getWebElement(By.xpath(eventCategory)));
        dropDown.selectByVisibleText(data.getEventCategory());

        WebUI.setText(By.xpath(eventCity), data.getEventCity());
        WebUI.setText(By.xpath(eventVenue), data.getEventVenue());
        WebUI.setText(By.xpath(eventDate), data.getEventStartDate());
        WebUI.setText(By.xpath(eventPrice), data.getEventPrice());
        WebUI.setText(By.xpath(eventSeat), String.valueOf(data.getTotalSeats()));
        WebUI.setText(By.xpath(eventImageURL), data.getEventImageURLPath());
    }

    // Search Event
    public void searchEvent(String eventName){
        WebUI.setText(By.xpath(searchEvent), eventName);
    }

    public void pressEnter(){
        WebUI.pressENTER();
    }

    public void clearFilterButton(){
        WebUI.clickElement(By.xpath(clearFilterButton));
    }

    public String searchNotFound(){
        WebUI.waitForElementVisible(By.xpath(eventNotFoundMessage));
        return WebUI.getWebElement(By.xpath(eventNotFoundMessage)).getText();
    }

}
