#Author: esnewman

Feature: Fill Prescriptions
	As a Pharmacist
	I want to fill prescriptions
	So that patients can be provided with prescriptions from the pharmacy

Scenario Outline: Valid Perscription Filled with Correct Drug Type
Given there exists a Pharmacy in the database
Given the user logs in as a pharmacist
Given the Pharmacy has 4 patients and 4 prescriptions for each patient
When I navigate to the Fill Prescription page
When I fill <name>'s <drug> prescription properly
Then <name>'s <drug> prescription will become filled

Examples:
  | name               | drug           |
  | test0              | 1000-0001-10   |
  | test1              | 1000-0001-11   |
  | test2              | 1000-0001-12   |
  | test3              | 1000-0001-13   |
  
  
Scenario Outline: Valid Prescription Filled With Incorrect Drug Type
Given there exists a Pharmacy in the database
Given the user logs in as a pharmacist
Given the Pharmacy has 4 patients and 4 prescriptions for each patient
When I navigate to the Fill Prescription page
When I fill <name>'s <drug> prescription with <type> not available
Then <name>'s <drug> prescription will become filled

Examples:
  | name               | drug             | type       |
  | test0              | 1000-0001-10     | Generic    |
  | test1              | 1000-0001-11     | Generic    |
  | test2              | 1000-0001-12     | Generic    |
  | test3              | 1000-0001-13     | Generic    |

Scenario Outline: Valid Prescription Filled With No Drug Preference
Given there exists a Pharmacy in the database
Given the user logs in as a pharmacist
Given the Pharmacy has 4 patients with no drug preference and 4 prescriptions for each patient
When I navigate to the Fill Prescription page
When I fill <name>'s <drug> prescription by choosing <drugType> drug type
Then <name>'s <drug> prescription will become filled

Examples:
  | name               | drug             | drugType     |
  | test0              | 1000-0001-10     | generic      |
  | test1              | 1000-0001-11     | brand        |
  | test2              | 1000-0001-12     | generic      |
  | test3              | 1000-0001-13     | brand        |