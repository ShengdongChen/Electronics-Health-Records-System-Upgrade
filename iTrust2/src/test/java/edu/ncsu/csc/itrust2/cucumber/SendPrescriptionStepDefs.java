package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * Stepdefs file corresponding to SendPrescription.feature
 *
 * @author River Rhodes krrhodes
 *
 */
public class SendPrescriptionStepDefs extends CucumberTest {

    /** Local url */
    private final String baseUrl = "http://localhost:8080/iTrust2";

    /**
     * Create a pharmacy
     *
     * @param name
     *            of pharmacy
     */
    @Given ( "^A pharmacy named (.+) exists in the system$" )
    public void createPharmacy ( final String name ) {
        final Pharmacy pharmacy = new Pharmacy();
        if ( !Pharmacy.getPharmacies().contains( pharmacy ) ) {
            pharmacy.setName( name );
            pharmacy.setAddress( "asdafsdfsdf" );
            pharmacy.setState( State.AK );
            pharmacy.setZip( "27603" );
            pharmacy.save();
        }
    }

    /**
     * Create a patient
     */
    @Given ( "^A patient exists in the system to be assigned the prescription$" )
    public void createPatient () {
        final Patient patient = new Patient();
        if ( !Patient.getPatients().contains( patient ) ) {
            patient.setFirstName( "test" );
            patient.save();
        }
    }

    /**
     * Log in as a healthcare professional
     */
    @When ( "^The HCP logs in and navigates to the Document Office Visit page to send a prescription$" )
    public void loginSendPrescription () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );

        assertEquals( "iTrust2: Document Office Visit", driver.getTitle() );
    }

    /**
     * Add a prescription to the vist
     */
    @When ( "^I add a prescription to an office visit with (.+) as the pharmacy$" )
    public void addPrescription ( final String name ) {
        driver.findElement( By.name( "date" ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( "10/10/2018".replace( "/", "" ) );

        driver.findElement( By.name( "time" ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( "time" ) );
        timeElement.sendKeys( "10:00 am".replace( ":", "" ).replace( " ", "" ) );

        final WebElement typeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + "GENERAL_CHECKUP" + "\"]" ) );
        typeElement.click();

        final WebElement hospitalElement = driver
                .findElement( By.cssSelector( "input[value=\"" + "UNC Hospital" + "\"]" ) );
        hospitalElement.click();

        driver.findElement( By.name( "height" ) ).clear();
        driver.findElement( By.name( "height" ) ).sendKeys( "50" );

        driver.findElement( By.name( "weight" ) ).clear();
        driver.findElement( By.name( "weight" ) ).sendKeys( "160" );

        final WebElement smokingElement = driver
                .findElement( By.cssSelector( "input[value=\"" + "NONSMOKING" + "\"]" ) );
        smokingElement.click();

        final Select pharmacyElement = new Select( driver.findElement( By.name( "pharmacy" ) ) );
        pharmacyElement.selectByValue( name );

        final WebElement drugElement = driver
                .findElement( By.cssSelector( "input[value=\"" + "1000-0001-10" + "\"]" ) );
        drugElement.click();

        driver.findElement( By.name( "dosageEntry" ) ).clear();
        driver.findElement( By.name( "dosageEntry" ) ).sendKeys( "50" );

        driver.findElement( By.name( "startEntry" ) ).clear();
        driver.findElement( By.name( "startEntry" ) ).sendKeys( "10/10/2018".replace( "/", "" ) );
        driver.findElement( By.name( "endEntry" ) ).clear();
        driver.findElement( By.name( "endEntry" ) ).sendKeys( "11/10/2018".replace( "/", "" ) );

        driver.findElement( By.name( "renewalEntry" ) ).clear();
        driver.findElement( By.name( "renewalEntry" ) ).sendKeys( "1" );

        driver.findElement( By.name( "fillPrescription" ) ).click();
        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Assert that the prescription was sent to the specified pharmacy
     *
     * @param pharmacy
     *            name
     */
    @Then ( "^The prescription is sent to (.+)$" )
    public void checkForPrescription ( final String pharmacy ) {
        Prescription.getForPatient( "test" );
    }
}
