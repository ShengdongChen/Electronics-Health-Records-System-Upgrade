package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.DrugType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.PrescriptionStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the fill Prescriptions page
 *
 * @author Eli Newman
 *
 */
public class FillPrescriptionStepDefs extends CucumberTest {

    /** Base url for testing */
    private final String baseUrl = "http://localhost:8080/iTrust2";

    /**
     * Adds a pharmacy to use for the test
     */
    @Given ( "^there exists a Pharmacy in the database" )
    public void addPharmacy () {
        final Pharmacy test = new Pharmacy( "test", "test Address", "12345", "AL" );
        test.save();
        // The Pharmacist needs to be assigned to this pharmacy
    }

    /**
     * Admin login
     */
    @Given ( "^the user logs in as a pharmacist$" )
    public void loginPharmacist () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "pharmacist" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Adds patients and prescriptions to the pharmacy
     *
     */
    @Given ( "^the Pharmacy has 4 patients and 4 prescriptions for each patient$" )
    public void startWithPopulatedList () {
        for ( int i = 0; i < 4; i++ ) {
            final Patient alice = new Patient();
            alice.setFirstName( "test" + i );
            final User aliceUser = new User( "test" + i, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            aliceUser.save();
            alice.setSelf( aliceUser );
            alice.setLastName( "test" + i );
            alice.setDateOfBirth( LocalDate.now().minusYears( 13 ) );
            alice.setPrescriptionPreference( DrugType.Generic );
            alice.setGender( Gender.Female );
            aliceUser.setPharmacy( Pharmacy.getByName( "Dr Mario's Pill Palace" ) );
            alice.save();

            final Drug d = new Drug();
            d.setCode( "1000-0001-1" + i );
            d.setName( "drug" + i );
            d.setGenericName( "Quetiapine" );
            d.setType( DrugType.BrandName );
            d.setDescription( "atypical antipsychotic and antidepressant" );
            d.save();
            final Pharmacy pharm = Pharmacy.getByName( "Dr Mario's Pill Palace" );

            final Prescription presc = new Prescription();
            presc.setDrug( d );
            presc.setDosage( 25 * ( i + 1 ) );
            presc.setStartDate( LocalDate.of( 2020, 1, 1 ) );
            presc.setEndDate( LocalDate.of( 2020, 2, 1 ) );
            presc.setPatient( aliceUser );
            presc.setRenewals( i + 5 );
            presc.setStatus( PrescriptionStatus.SENT_TO_PHARMACY );
            presc.save();
            pharm.addPrescriptions( presc );
            pharm.addDrug( d );
            pharm.save();
        }
    }

    /**
     * Adds patients and prescriptions to the pharmacy
     *
     */
    @Given ( "^the Pharmacy has 4 patients with no drug preference and 4 prescriptions for each patient$" )
    public void startWithPopulatedListNoPreference () {

        for ( int i = 0; i < 4; i++ ) {
            final Patient alice = new Patient();
            alice.setFirstName( "test" + i );
            final User aliceUser = new User( "test" + i, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            aliceUser.save();
            alice.setSelf( aliceUser );
            alice.setLastName( "test" + i );
            alice.setDateOfBirth( LocalDate.now().minusYears( 13 ) );
            alice.setPrescriptionPreference( DrugType.NotSpecified );
            alice.setGender( Gender.Female );
            aliceUser.setPharmacy( Pharmacy.getByName( "Dr Mario's Pill Palace" ) );
            alice.save();

            final Drug d = new Drug();
            d.setCode( "1000-0001-1" + i );
            d.setName( "drug" + i );
            d.setGenericName( "Quetiapine" );
            if ( i % 2 == 0 ) {
                d.setType( DrugType.Generic );
            }
            else {
                d.setType( DrugType.BrandName );
            }
            d.setDescription( "atypical antipsychotic and antidepressant" );
            d.save();
            final Pharmacy pharm = Pharmacy.getByName( "Dr Mario's Pill Palace" );

            final Prescription presc = new Prescription();
            presc.setDrug( d );
            presc.setDosage( 25 * ( i + 1 ) );
            presc.setStartDate( LocalDate.of( 2020, 1, 1 ) );
            presc.setEndDate( LocalDate.of( 2020, 2, 1 ) );
            presc.setPatient( aliceUser );
            presc.setRenewals( i + 5 );
            presc.setStatus( PrescriptionStatus.SENT_TO_PHARMACY );
            presc.save();
            pharm.addPrescriptions( presc );
            pharm.addDrug( d );
            pharm.save();
        }

    }

    /**
     * Add pharmacy page
     */
    @When ( "^I navigate to the Fill Prescription page$" )
    public void managePharmaciesPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('uploadPassengerData').click();" );
    }

    /**
     * Fills the prescription of a patient with no changes to prescription
     *
     * @param name
     *            for name of patient
     * @param drug
     *            for drug prescription to fill
     * @throws InterruptedException
     */
    @When ( "^I fill (.+)'s (.+) prescription properly" )
    public void fillNormalPrescription ( final String name, final String drug ) throws InterruptedException {
        Thread.sleep( 3000 );
        driver.findElement( By.xpath( "//input[@id='" + name + "-select']" ) ).click();
        assertTrue( driver.getPageSource().contains( "Select Prescription" ) );
        driver.findElement( By.xpath( "//input[@id='" + drug + "-select']" ) ).click();
        assertTrue( driver.getPageSource().contains( "Fill Prescription" ) );
        driver.findElement( By.id( "fillPrescription-submit" ) ).click();
        assertTrue( driver.getPageSource().contains( "Prescription filled successfully" ) );
    }

    /**
     * Tests if the prescription was removed from the pharmacy database
     *
     * @param name
     *            for name of patient with prescription
     * @param drug
     *            for drug removed
     */
    @Then ( "^(.+)'s (.+) prescription will become filled$" )
    public void prescriptionRemoved ( final String name, final String drug ) {
        final List<Prescription> p = Prescription.getForPatient( name );
        for ( final Prescription presc : p ) {
            if ( presc.getDrug().getName().equals( drug ) ) {
                assertTrue( presc.getStatus().equals( PrescriptionStatus.FILLED ) );
            }
        }
    }

    /**
     * Fills a prescription where the drug type preferred was not available
     *
     * @param name
     *            for name of patient
     * @param drug
     *            for drug to provide
     * @param type
     *            for drug type used in prescription
     * @throws InterruptedException
     */
    @When ( "^I fill (.+)'s (.+) prescription with (.+) not available$" )
    public void fillChangedPrescription ( final String name, final String drug, final String type )
            throws InterruptedException {
        Thread.sleep( 3000 );
        driver.findElement( By.xpath( "//input[@id='" + name + "-select']" ) ).click();
        driver.findElement( By.xpath( "//input[@id='" + drug + "-select']" ) ).click();
        // This needs to be the id of the other drug type
        waitForAngular();
        assertTrue( driver.findElementByCssSelector( "input[id='brand'][type='radio']" ).isSelected() );
        assertTrue( driver.getPageSource().contains( "Drug Preference not available. Patient will be notified." ) );
        driver.findElement( By.id( "fillPrescription-submit" ) ).click();
        assertTrue( driver.getPageSource().contains( "Prescription filled successfully" ) );
    }

    /**
     * Fills a prescription where the drug type preference is not given
     *
     * @param name
     *            for name of patient
     * @param drug
     *            for drug to provide
     * @param drugType
     *            for drug type used in prescription
     * @throws InterruptedException
     */
    @When ( "^I fill (.+)'s (.+) prescription by choosing (.+) drug type$" )
    public void fillChosenPrescription ( final String name, final String drug, final String drugType )
            throws InterruptedException {
        Thread.sleep( 3000 );
        driver.findElement( By.xpath( "//input[@id='" + name + "-select']" ) ).click();
        driver.findElement( By.xpath( "//input[@id='" + drug + "-select']" ) ).click();
        // This needs to be the id of the other drug type
        final WebElement radio = driver.findElement( By.id( drugType ) );
        radio.click();
        driver.findElement( By.id( "fillPrescription-submit" ) ).click();
    }

}
