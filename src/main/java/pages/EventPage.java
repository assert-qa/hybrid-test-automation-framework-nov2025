package pages;

import factory.DriverFactory;
import keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pages.dto.NewEventDataObject;
import utils.LogUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pages.dto.SelectedEventDataObject;

import static helpers.PropertiesHelper.loadAllFiles;

public class EventPage extends DriverFactory {
    Properties setUp = loadAllFiles();

    public EventPage(){
    }

    private static final Random RANDOM = new Random();

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
        return clickAnyAvailableEventAndGetData(1);
    }

    public SelectedEventDataObject clickAnyAvailableEventAndGetData(int requiredTickets) {
        List<WebElement> candidates = new ArrayList<>();

        for (WebElement card : getEvents()) {
            if (isBookNowButtonAvailable(card) && getAvailableSeats(card) >= requiredTickets) {
                candidates.add(card);
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No bookable event found with at least " + requiredTickets + " seat(s).");
        }

        WebElement selectedCard = candidates.get(RANDOM.nextInt(candidates.size()));
        String eventName = selectedCard.findElement(By.tagName("h3")).getText().trim();
        int eventPrice = getEventPrice(selectedCard);
        int availableSeats = getAvailableSeats(selectedCard);

        WebElement bookNow = selectedCard.findElement(By.xpath(bookNowButton));
        WebUI.scrollToElement(bookNow);
        bookNow.click();
        LogUtils.info("Selected event for booking: " + eventName);
        return new SelectedEventDataObject(eventName, eventPrice, availableSeats);
    }

    public int getAnyEventPrice(String eventName) {
        for (WebElement card : getEvents()) {
            String actualEvent = card.findElement(By.tagName("h3")).getText().trim();

            if (actualEvent.equalsIgnoreCase(eventName)) {
                return getEventPrice(card);
            }
        }
        throw new NoSuchElementException("Event not found: " + eventName);
    }

    private boolean isBookNowButtonAvailable(WebElement card) {
        List<WebElement> buttons = card.findElements(By.xpath(bookNowButton));
        return !buttons.isEmpty() && buttons.get(0).isDisplayed() && buttons.get(0).isEnabled();
    }

    private int getEventPrice(WebElement card) {
        String priceText = card.findElement(By.cssSelector("p.text-lg.font-bold.text-indigo-700")).getText();
        String numericText = priceText.replaceAll("[^\\d]", "");

        if (numericText.isEmpty()) {
            throw new IllegalStateException("Event price is not numeric: " + priceText);
        }

        return Integer.parseInt(numericText);
    }

    private int getAvailableSeats(WebElement card) {
        String cardText = card.getText();

        if (cardText.toLowerCase(Locale.ROOT).contains("sold out")) {
            return 0;
        }

        Matcher seatMatcher = Pattern.compile("(\\d+)\\s+seats?\\s+(left|available)", Pattern.CASE_INSENSITIVE)
                .matcher(cardText);

        if (seatMatcher.find()) {
            return Integer.parseInt(seatMatcher.group(1));
        }

        return Integer.MAX_VALUE;
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
        WebUI.waitForElementVisible(By.xpath(createEventPage));
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
