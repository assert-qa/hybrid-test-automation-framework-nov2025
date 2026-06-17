@api
Feature: Login API

  Scenario: User can login with valid credentials via API
    Given I prepare valid login API payload
    When I send POST request to login API
    Then the login API response status should be 200
    And the login API response should contain access token