@api
Feature: Login API

  Background:
    Given I set login API endpoint
    And I set request headers
    And I prepare login API base configuration

  Scenario: User cannot login with invalid credentials via API
    Given I prepare invalid login API payload
    And the login request body should match login request schema
    When I send "POST" request to "login" API
    Then the API response status should be 400
    And the login API response should match error schema
