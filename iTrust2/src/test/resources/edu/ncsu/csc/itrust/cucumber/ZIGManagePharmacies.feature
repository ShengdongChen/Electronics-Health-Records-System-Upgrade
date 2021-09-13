#Author: acagatuc

Feature: Manage Pharmacies
	As an Administrator
	I want to add and delete Pharmacies from the list
	So that doctors and patients have a list of pharmacies to send prescriptions to

Scenario Outline: Valid Pharmacy added to an empty list
Given The desired Pharmacy named <name> doesn't exist
Given the user logs in as an administrator
When I navigate to the Manage Pharmacies page
When I add a new Pharmacy with <name>, <address>, <state>, and <zipcode>
Then the pharmacy is created successfully

Examples:
  | name               | address             | state | zipcode    |
  | Walgreens          | 101 Pharmacy Block  | NY    | 10101-0101 |
  | Generic Pharmacy   | 123 Main Street     | VA    | 54321      |
  | Gurley's Pharmacy  | 114 W Main Street   | NC    | 27701      |
  | Brand Name Pharacy | 321 Not Main Street | WA    | 12345      |
  
  
Scenario Outline: Valid Pharmacy added to current Pharmacy List
Given The desired Pharmacy named <name> doesn't exist
Given the user logs in as an administrator
When I navigate to the Manage Pharmacies page
When I add a new Pharmacy with <name>, <address>, <state>, and <zipcode>
Then the pharmacy is created successfully
And that pharmacy <name> exists

Examples:
  | name               | address             | state | zipcode    | 
  | Something new 1    | 101 Pharmacy Block  | NY    | 10101-0101 | 
  | Dr Seuss Pharmacy  | 123 Main Street     | VA    | 54321      | 
  | Alfyn's Apothecary | 114 W Main Street   | NC    | 27701      | 
  | Spendthrift Bow    | 321 Not Main Street | WA    | 12345      | 

Scenario Outline: Delete Pharmacy from List
Given The desired Pharmacy named <name> doesn't exist
Given the user logs in as an administrator
When I navigate to the Manage Pharmacies page
When the pharmacy with <name>, <address>, <state>, and <zipcode> is deleted from the list
Then the list no longer contains a pharmacy with <name>, <address>, <state>, and <zipcode>

Examples:
  | name               | address             | state | zipcode    |
  | Something new 1    | 101 Pharmacy Block  | NY    | 10101-0101 |
  | Dr Seuss Pharmacy  | 123 Main Street     | VA    | 54321      |
  | Alfyn's Apothecary | 114 W Main Street   | NC    | 27701      |
  | Spendthrift Bow    | 321 Not Main Street | WA    | 12345      |
	
Scenario Outline: Invalid Pharmacy data input
Given The desired Pharmacy named <name> doesn't exist
Given the user logs in as an administrator
When I navigate to the Manage Pharmacies page
When there is an attempt to add a pharmacy with <name>, <address>, <state>, and <zipcode> to the list
Then that pharmacy <name> does not exist
And an error occurs with <error> and <errorMessage> is printed

Examples:
  | name               | address             | state       | zipcode       | error   | errorMessage                                                    | 
  | error name{}       | 101 Pharmacy Block  | NY          | 10101-0101    | name    | Cannot contain non-alphanumeric symbols other than -,' or space | 
  | Generic Pharmacy   | error address,,     | VA          | 54321         | address | Cannot contain non-alphanumeric symbols other than -, or .      | 
  | Brand Name Pharacy | 321 Not Main Street | WA          | not a zipcode | zipcode | 5 or 9 digit integer required                                   | 