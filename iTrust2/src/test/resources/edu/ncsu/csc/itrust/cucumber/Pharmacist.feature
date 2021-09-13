#Author ywu44 schen42
Feature: Add Pharmacist
	As an admin
	I want to add a new user as a pharmacist with pharmacy assigned
	So a pharmacist can use iTrust
	As a pharmacist
	I want to use iTrust to help patient fill and record their prescriptions
	So that patient can go to pharmacy to pick their prescriptions up
	
Scenario: Assign Pharmacy to a New Pharmacist while Adding
Given There is a pharmacy cvs
When I am logged in as an Admin
When I go to Add User page
When I fill values to add a pharmacist
Then The pharmacist is created successfully
Then The pharmacist can view the prescription of the pharmacy's patients
