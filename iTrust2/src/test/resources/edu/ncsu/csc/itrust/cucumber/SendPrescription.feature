#Author krrhodes
Feature: Send Prescription to Pharmacy
	As a HCP
	I want to be able to send a prescription to a pharmacy when it is prescribed
	So that the pharmacy can fill that prescription

Scenario Outline: Create prescription
	Given A patient exists in the system
	And A pharmacy named <pharmacy> exists in the system
	When The HCP logs in and navigates to the Document Office Visit page to send a prescription
	And I add a prescription to an office visit with <pharmacy> as the pharmacy
	Then The prescription is sent to <pharmacy>
	
Examples:
	| pharmacy    |
	| Wallgreens  |