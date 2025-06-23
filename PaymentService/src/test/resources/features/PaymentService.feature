Feature: Payment Service API
  As a user of the Payment Service
  I want to perform operations on payments
  So that I can manage payments and generate invoices effectively

  Background:
    Given the payment service is running

  Scenario: Get all payments
    Given there are payments in the system
    When I send a GET request to "/api/payments/"
    Then the response status code should be 200
    And the response should contain a list of payments

  Scenario: Get payment by ID
    Given there is a payment in the system
    When I send a GET request to "/api/payments/{id}" with the payment ID
    Then the response status code should be 200
    And the response should contain the payment details

  Scenario: Get payment by non-existent ID
    When I send a GET request to "/api/payments/99999"
    Then the response status code should be 404

  Scenario: Create a new payment
    Given I have a valid payment object with clients
    When I send a POST request to "/api/payments/admin/create"
    Then the response status code should be 200
    And the response should contain the created payment details

  Scenario: Generate invoice for a payment
    Given there is a payment with associated clients in the system
    When I send a GET request to "/api/payments/invoice/{id}" with the payment ID
    Then the response status code should be 200
    And the response should contain an invoice with correct payment and client information