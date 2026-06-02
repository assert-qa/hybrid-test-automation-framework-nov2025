Feature: My Bookings
  As a logged-in user
  I want to manage my event bookings
  So that I can review and track my registered events

  Background:
    Given I launch the browser
    When I navigate to url "https://eventhub.rahulshettyacademy.com/login"
    And I enter registered email address and password
    And I click "sign in" button

    And I navigate to "Events" menu
    And I click on any available event card
    And I enter booking information

    And I click "Confirm Booking" button
    Then I should see the "Your tickets are reserved" message

    When I navigate to "My Bookings" menu

  Scenario: View Booking List
    Then I verify that "My Bookings" is visible successfully
    And my booked events should be displayed

  Scenario: View Booking Details
    When I click "View Details" button
    Then I should be redirected to booking detail page
    And I should see the "confirmed" message

  Scenario: Verify Booking Information
    Then I should see customer information:
      | Name    |
      | Email   |
      | Phone   |
      | Tickets |

  Scenario: Verify Total Paid

  Scenario: Cancel Booking
    When I click "Cancel Booking" button
    And I confirm booking cancellation
    Then I should see "Booking cancelled successfully" message

  Scenario: Delete Booked Event
    When I click "Delete Booking" button
    And I confirm booking deletion
    Then the booking should no longer appear in My Bookings