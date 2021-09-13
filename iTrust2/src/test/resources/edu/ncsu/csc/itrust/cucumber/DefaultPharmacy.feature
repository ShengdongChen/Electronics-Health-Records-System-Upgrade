Feature: Default Pharmacies
	As a Patient
	I want to add a default Pharmacy from the list
	So that doctors know to send my prescriptions to that pharmacy
	As an HCP
	I want to be able view a patient's default pharmacy
	So I can select that pharmacy to send the prescription to

Scenario: Adding a default Pharmacy
	Given A patient and a pharmacy exist in the system
	When the user is logged in as patient
	When the user edits their demographics
	Then the user can select a default pharmacy
