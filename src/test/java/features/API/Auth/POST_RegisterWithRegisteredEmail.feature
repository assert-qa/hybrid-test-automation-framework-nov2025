@api
@allure.label.epic:API
@allure.label.feature:Authentication
@allure.label.story:DuplicateEmailRegistration
@allure.label.severity:normal
Feature: Register with registered email

  Background:
    Given I set "register" API endpoint
    And I set request headers
    And I prepare API base configuration

  Scenario: User cannot register with registered email via API
    Given I prepare register API payload with registered email
    And the register request body should match register request schema
    When I send "POST" request to "register" API
    Then the API response status should be 400
    And the "register" API response should match "error" schema
    And the "register" API response error should be "Email already registered"
    And the "register" API response details should be empty