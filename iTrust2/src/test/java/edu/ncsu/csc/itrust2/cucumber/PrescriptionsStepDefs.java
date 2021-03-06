package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class PrescriptionsStepDefs extends CucumberTest {

    private static final String BASE_URL  = "http://localhost:8080/iTrust2/";
    private static final String VISIT_URL = BASE_URL + "hcp/documentOfficeVisit.html";
    private static final String VIEW_URL  = BASE_URL + "patient/officeVisit/viewPrescriptions.html";
    private static final String DRUG_URL  = BASE_URL + "admin/drugs.html";

    private final String        baseUrl   = "http://localhost:8080/iTrust2";

    private String getUserName ( final String first, final String last ) {
        return first.substring( 0, 1 ).toLowerCase() + last.toLowerCase();
    }

    private void enterValue ( final String name, final String value ) {
        final WebElement field = driver.findElement( By.name( name ) );
        field.clear();
        field.sendKeys( String.valueOf( value ) );
    }

    /**
     * Fills in the date and time fields with the specified date and time.
     *
     * @param date
     *            The date to enter.
     * @param time
     *            The time to enter.
     */
    private void fillInDateTime ( final String dateField, final String date, final String timeField,
            final String time ) {
        fillInDate( dateField, date );
        fillInTime( timeField, time );
    }

    /**
     * Fills in the date field with the specified date.
     *
     * @param date
     *            The date to enter.
     */
    private void fillInDate ( final String dateField, final String date ) {
        driver.findElement( By.name( dateField ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( dateField ) );
        dateElement.sendKeys( date.replace( "/", "" ) );
    }

    /**
     * Fills in the time field with the specified time.
     *
     * @param time
     *            The time to enter.
     */
    private void fillInTime ( final String timeField, String time ) {
        // Zero-pad the time for entry
        if ( time.length() == 7 ) {
            time = "0" + time;
        }

        driver.findElement( By.name( timeField ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( timeField ) );
        timeElement.sendKeys( time.replace( ":", "" ).replace( " ", "" ) );
    }

    private void selectItem ( final String name, final String value ) {
        final By selector = By.cssSelector( "input[name='" + name + "'][value='" + value + "']" );
        waitForAngular();
        final WebElement element = driver.findElement( selector );
        element.click();
    }

    private void selectName ( final String name ) {
        final WebElement element = driver.findElement( By.cssSelector( "input[name='" + name + "']" ) );
        element.click();
    }

    private void ensurePatientExists ( final String username, final String firstName, final String lastName,
            final String dob ) {
        if ( Patient.getByName( username ) == null ) {
            User user = User.getByName( username );
            if ( user == null ) {
                user = new User( username, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                        Role.ROLE_PATIENT, 1 );
                user.save();
            }
            final Patient patient = new Patient();
            patient.setFirstName( firstName );
            patient.setSelf( user );
            patient.setLastName( lastName );
            patient.setDateOfBirth( LocalDate.parse( dob, DateTimeFormatter.ofPattern( "MM/dd/yyyy" ) ) );
            patient.save();
        }
    }

    private void ensureCVSExists () {
        Pharmacy pharm = Pharmacy.getByName( "CVS" );
        if ( pharm == null ) {
            pharm = new Pharmacy();
            pharm.setName( "CVS" );
            pharm.setState( State.NC );
            pharm.setAddress( "123 Main Street" );
            pharm.setZip( "80001" );
            pharm.save();
        }
    }

    @Given ( "I have logged in with username: (.+)" )
    public void login ( final String username ) {
        attemptLogout();

        driver.get( baseUrl );

        enterValue( "username", username );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I start documenting an office visit for the patient with name: (.+) (.+) and date of birth: (.+)" )
    public void startOfficeVisit ( final String firstName, final String lastName, final String dob ) {
        final String patient = getUserName( firstName, lastName );
        ensurePatientExists( patient, firstName, lastName, dob );
        ensureCVSExists();

        driver.get( VISIT_URL );

        try {
            Prescription.getForPatient( patient ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }
        try {
            Diagnosis.getForPatient( User.getByName( patient ) ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }
        try {
            OfficeVisit.getForPatient( patient ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }

        waitForAngular();
        selectItem( "name", patient );
    }

    @When ( "fill in the office visit with date: (.+), hospital: (.+), notes: (.*), weight: (.+), height: (.+), blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), hdl: (.+), ldl: (.+), and triglycerides: (.+)" )
    public void fillOfficeVisitForm ( final String date, final String hospital, final String notes, final String weight,
            final String height, final String bloodPressure, final String hss, final String pss, final String hdl,
            final String ldl, final String triglycerides ) {

        waitForAngular();

        fillInDateTime( "date", date, "time", "10:10 AM" );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementsByName('hospital')[0].click();" );
        waitForAngular();
        driver.findElement( By.name( "GENERAL_CHECKUP" ) ).click();
        waitForAngular();
        enterValue( "notes", notes );
        enterValue( "weight", weight );
        enterValue( "height", height );
        enterValue( "systolic", bloodPressure.split( "/" )[0] );
        enterValue( "diastolic", bloodPressure.split( "/" )[1] );
        selectItem( "houseSmokingStatus", hss );
        selectItem( "patientSmokingStatus", pss );
        enterValue( "hdl", hdl );
        enterValue( "ldl", ldl );
        enterValue( "tri", triglycerides );
    }

    @When ( "add a prescription for (.+) with a dosage of (.+) starting on (.+) and ending on (.+) with (.+) renewals" )
    public void addPrescription ( final String drug, final String dosage, final String startDate, final String endDate,
            final String renewals ) {
        waitForAngular();

        enterValue( "dosageEntry", dosage );
        fillInDate( "startEntry", startDate );
        fillInDate( "endEntry", endDate );
        enterValue( "renewalEntry", renewals );
        selectName( drug );

        final Select pharmacyElement = new Select( driver.findElement( By.name( "pharmacy" ) ) );
        pharmacyElement.selectByValue( "CVS" );

        driver.findElement( By.name( "fillPrescription" ) ).click();
        assertEquals( "", driver.findElement( By.name( "errorMsg" ) ).getText() );
    }

    @When ( "submit the office visit" )
    public void submitOfficeVisit () {
        driver.findElement( By.name( "submit" ) ).click();
        waitForAngular();
    }

    @Then ( "A message indicates the visit was submitted successfully" )
    public void officeVisitSuccessful () {
        waitForAngular();
        final WebElement msg = driver.findElement( By.name( "success" ) );
        assertEquals( "Office visit created successfully", msg.getText() );
    }

    @When ( "I choose to view my prescriptions" )
    public void viewPrescriptions () {
        driver.get( VIEW_URL );
    }

    @Then ( "I see a prescription for (.+) with a dosage of (.+) starting on (.+) and ending on (.+) with (.+) renewals" )
    public void prescriptionVisible ( final String drug, final String dosage, final String startDate,
            final String endDate, final String renewals ) {
        waitForAngular();
        final List<WebElement> rows = driver.findElements( By.name( "prescriptionTableRow" ) );

        List<WebElement> data = null;
        for ( final WebElement r : rows ) {
            if ( r.getText().contains( drug ) ) {
                waitForAngular();
                data = r.findElements( By.tagName( "td" ) );
                break;
            }
        }

        assertEquals( drug, data.get( 0 ).getText() );
        assertEquals( dosage, data.get( 1 ).getText() );
        assertEquals( renewals, data.get( 4 ).getText() );
    }

}
