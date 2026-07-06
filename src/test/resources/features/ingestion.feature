Feature: DTFT File Ingestion API

  Background:
    Given the ingestion API is running

  Scenario: Upload a valid DTFT file
    Given a valid DTFT file with 100 records
    When I upload the file to the ingest endpoint
    Then the response status is 200
    And the job status is "COMPLETED"
    And rows_inserted is 100
    And rows_failed is 0

  Scenario: Upload a file with bad records
    Given a DTFT file with 5 valid records and 3 invalid records
    When I upload the file to the ingest endpoint
    Then the response status is 200
    And the job status is "COMPLETED"
    And rows_inserted is 5
    And rows_failed is 3

  Scenario: Shard routing - MachineID routed to correct shard
    Given a DTFT file containing MachineID 32 and MachineID 33
    When I upload the file to the ingest endpoint
    Then the response status is 200
    And MachineID 32 is stored in shard table "machine_log_shard_00"
    And MachineID 33 is stored in shard table "machine_log_shard_01"

  Scenario: Upload a large file spanning multiple batches
    Given a valid DTFT file with 3000 records
    When I upload the file to the ingest endpoint
    Then the response status is 200
    And the job status is "COMPLETED"
    And rows_inserted is 3000
    And rows_failed is 0

  Scenario: Upload an empty file returns 400
    Given an empty DTFT file
    When I upload the file to the ingest endpoint
    Then the response status is 400
