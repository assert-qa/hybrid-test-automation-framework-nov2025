package pages;

import factory.DriverFactory;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pages.models.NewEventDataObject;
import utils.LogUtils;

import java.util.*;
import pages.models.SelectedEventDataObject;

import static helpers.PropertiesHelper.loadAllFiles;

public class EventPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public EventPage(){
    }

    private static final Random RANDOM = new Random();

    private static final List<String> AVAILABLE_EVENTS = List.of(
            "Dilli Diwali Mela",
            "Hollywood Monsoon Night — Los Angeles",
            "World Tech Summit"
    );

    String eventPage = setUp.getProperty("NAVIGATE_TO_EVENT_PAGE");
    String searchEvent = setUp.getProperty("SEARCH_EVENT_INPUT");
    String eventList = setUp.getProperty("EVENT_LIST");
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

    String eventInformationDetail = setUp.getProperty("EVENT_INFORMATION_DETAIL");
    String aboutEventDetail = setUp.getProperty("ABOUT_EVENT_DETAIL");

    String selectEventCategory = setUp.getProperty("SELECT_EVENT_CATEGORY");
    String selectEventCity = setUp.getProperty("SELECT_EVENT_CITY");

    String bookNowButton = setUp.getProperty("BOOK_NOW_BUTTON");

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

    public List<WebElement> getEvents(){
        WebUI.waitForElementVisible(By.cssSelector(eventList));
        return WebUI.getWebElements(By.cssSelector(eventList));
    }

//    public void clickAnyAvailableEvent() {
//        clickAnyAvailableEventAndGetName();
//    }
    public SelectedEventDataObject clickAnyAvailableEventAndGetData() {
        String selectedEvent = AVAILABLE_EVENTS.get(
                RANDOM.nextInt(AVAILABLE_EVENTS.size())
        );

        for (WebElement card : getEvents()){
            String actualEventName = card.findElement(By.tagName("h3")).getText().trim();

            if (actualEventName.equalsIgnoreCase(selectedEvent)){
                String priceText = card.findElement(By.cssSelector("p.text-lg.font-bold.text-indigo-700")).getText();
                int eventPrice = Integer.parseInt(priceText.replaceAll("[^\\d]", ""));

                card.findElement(By.xpath(bookNowButton)).click();
                LogUtils.info("Selected event for booking: " + actualEventName);
                return new SelectedEventDataObject(actualEventName, eventPrice);
            }
        }
        throw new IllegalStateException("No available event card found for selected event: " + selectedEvent);
    }

    public int getAnyEventPrice(String eventName) {
        for (WebElement card : getEvents()) {
            String actualEvent = card.findElement(By.tagName("h3")).getText().trim();

            if (actualEvent.equalsIgnoreCase(eventName)) {
                String priceText = card.findElement(By.tagName("p")).getText().replaceAll("[^\\d]", "");
                return Integer.parseInt(priceText);
            }
        }
        throw new NoSuchElementException("Event not found: " + eventName);
    }

    // Event Information
    public Map<String, String> getEventInformation(List<String> expectedFields) {
        Map<String, String> eventInfo = new HashMap<>();

        for (String field : expectedFields) {
            By valueLocator = By.xpath(
                    "//p[normalize-space()='" + field + "']/following-sibling::p"
            );

            String value = WebUI.getElementText(valueLocator);
            eventInfo.put(field, value);
        }

        return eventInfo;
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

    // Filter Event
    public void selectEventCategory(String category){
        WebUI.selectDropDown(By.cssSelector(selectEventCategory), category);
    }

    public void selectEventCity(String city){
        WebUI.selectDropDown(By.cssSelector(selectEventCity), city);
    }

}
