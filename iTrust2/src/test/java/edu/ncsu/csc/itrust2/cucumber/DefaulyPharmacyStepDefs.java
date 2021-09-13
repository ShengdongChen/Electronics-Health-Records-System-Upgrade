package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * The Step Definitions for Editing user Demographics.
 *
 * @author Shengdong Chen
 *
 */
public class DefaulyPharmacyStepDefs extends CucumberTest {
    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String patientString = "PforPhar";

    @Given ( "A patient and a pharmacy exist in the system" )
    public void patientExists () {
        attemptLogout();

        // Create the test User
        final User user = new User( patientString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user.save();

        final Patient patient = new Patient( user.getUsername() );
        patient.save();

        final Pharmacy pharmacy = new Pharmacy();
        pharmacy.setAddress( "100 main st" );
        pharmacy.setName( "phar4cucumber" );
        pharmacy.setState( State.NC );
        pharmacy.setZip( "27603" );
        pharmacy.save();
    }

    @When ( "the user is logged in as patient" )
    public void loginAsPatient () {
        attemptLogout();

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( patientString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: Patient Home", driver.getTitle() );
    }

    @When ( "the user edits their demographics" )
    public void editDemographics () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics-patient').click();" );
        waitForAngular();
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Karl" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Liebknecht" );

        final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
        preferredName.clear();

        final WebElement mother = driver.findElement( By.id( "mother" ) );
        mother.clear();

        final WebElement father = driver.findElement( By.id( "father" ) );
        father.clear();

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "karl_liebknecht@mail.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Karl Liebknecht Haus. Alexanderplatz" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Berlin" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CA" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "91505" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
        dob.clear();
        dob.sendKeys( "08131950" ); // Enter date without slashes

        // #51 Bug Edit Ethnicity
        final WebElement ethnicity = driver.findElement( By.id( "ethnicity" ) );
        final Select drop = new Select( ethnicity );
        drop.selectByVisibleText( "African American" );

        // UC36
        final WebElement prescriptionPreference = driver.findElement( By.id( "prescriptionPreference" ) );
        final Select presDrop = new Select( prescriptionPreference );
        presDrop.selectByVisibleText( "Brand Name" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @Then ( "the user can select a default pharmacy" )
    public void selectPharmacy () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editDefaultPharmacy').click();" );
        waitForAngular();

        driver.findElement( By.id( "phar4cucumber-select" ) ).click();
        waitForAngular();
        assertEquals( "phar4cucumber", Patient.getByName( patientString ).getDefaultPharmacy().getName() );

    }
}
