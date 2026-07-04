@api
Feature: Get list all events

  Background:
    Given I set "events" API endpoint
    And I set request headers
    And I prepare API base configuration

  Scenario: User successfully get paginated list of events
    Given I prepare events API query params
      | category | <category> |
      | city     | <city>     |
      | search   | <search>   |
      | page     | <page>     |
      | limit    | <limit>    |
    When I send "GET" request to "events" API
    Then the API response status should be 200
    And the "events" API response should match "success" schema

    Example:
      | category  | city      | search | page | limit|
      | Conference| Bangalore | Diwali | 1    | 10   |