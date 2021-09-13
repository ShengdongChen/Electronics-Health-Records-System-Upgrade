package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;

/**
 * Step definitions for ManagePharmacies feature
 *
 * @author Addison Agatucci
 */
public class ManagePharmaciesStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

    @Given ( "The desired Pharmacy named (.+) doesn't exist$" )
    public void noPharmacy ( final String name ) {
        final Pharmacy p = Pharmacy.getByName( name );
        if ( p != null ) {
            p.setDrugs( new HashSet<>() );
            p.setPrescriptions( new HashSet<>() );
            p.delete();
        }

    }

    @Given ( "^The desired Pharmacy does exist$" )
    public void yesPharmacy () {
        attemptLogout();
    }

    /**
     * Admin login
     */
    @Given ( "^the user logs in as an administrator$" )
    public void loginAdminP () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Add pharmacy page
     */
    @When ( "^I navigate to the Manage Pharmacies page$" )
    public void managePharmaciesPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('deletepharmacy').click();" );
    }

    @Given ( "^the Pharmacy list has (\\d+) Pharmacies in the list$" )
    public void startWithPopulatedList ( final int size ) {
        final List<Pharmacy> p = Pharmacy.getPharmacies();
        for ( int i = p.size(); i < size; i++ ) {
            final Pharmacy test = new Pharmacy( "test" + i, "test Address", "12345", "AL" );
            test.save();
        }
    }

    @When ( "^I add a new Pharmacy with (.+), (.+), (.+), and (.+)$" )
    public void addNewPharmacyToEmptyList ( final String name, final String address, final String state,
            final String zipcode ) {
        final WebElement nameInput = driver.findElement( By.id( "name" ) );
        nameInput.clear();
        nameInput.sendKeys( name );

        final WebElement addressInput = driver.findElement( By.id( "address" ) );
        addressInput.clear();
        addressInput.sendKeys( address );

        final Select dropdown = new Select( driver.findElement( By.id( "state" ) ) );
        dropdown.selectByValue( state );

        final WebElement zipInput = driver.findElement( By.id( "zip" ) );
        zipInput.clear();
        zipInput.sendKeys( zipcode );

        driver.manage().timeouts().implicitlyWait( 10, TimeUnit.SECONDS );
        driver.findElement( By.id( "submit" ) ).click();
        final WebDriverWait wait = new WebDriverWait( driver, 30 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "success" ) ) );
    }

    @When ( "^the pharmacy with (.+), (.+), (.+), and (.+) is deleted from the list$" )
    public void deletePharmacyFromList ( final String name, final String address, final String state,
            final String zipcode ) {
        try {
            driver.findElement( By.id( name + "-delete" ) ).click();
        }
        catch ( final Exception e ) {

        }
    }

    @When ( "^there is an attempt to add a pharmacy with (.+), (.+), (.+), and (.+) to the list$" )
    public void invalidAddPharmacyToCurrentList ( final String name, final String address, final String state,
            final String zipcode ) {
        final WebElement nameInput = driver.findElement( By.id( "name" ) );
        nameInput.clear();
        nameInput.sendKeys( name );

        final WebElement addressInput = driver.findElement( By.id( "address" ) );
        addressInput.clear();
        addressInput.sendKeys( address );

        final Select dropdown = new Select( driver.findElement( By.id( "state" ) ) );
        dropdown.selectByValue( state );

        final WebElement zipInput = driver.findElement( By.id( "zip" ) );
        zipInput.clear();
        zipInput.sendKeys( zipcode );

        driver.manage().timeouts().implicitlyWait( 10, TimeUnit.SECONDS );
        driver.findElement( By.id( "submit" ) ).click();
        final WebDriverWait wait = new WebDriverWait( driver, 30 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "errP" ) ) );
    }

    @Then ( "^the pharmacy is created successfully$" )
    public void validAddToEmptyFinish () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Pharmacy added successfully" ) );
    }

    @Then ( "^that pharmacy (.+) exists$" )
    public void validAddToCurrentFinish ( String name ) {
        final Pharmacy p = Pharmacy.getByName( name );
        assertNotNull( p );
    }

    @Then ( "^that pharmacy (.+) does not exist$" )
    public void invalidAddToCurrentFinish ( String name ) {
        final Pharmacy p = Pharmacy.getByName( name );
        assertNull( p );
    }

    @Then ( "^the list no longer contains a pharmacy with (.+), (.+), (.+), and (.+)$" )
    public void validDeleteFromCurrentFinish ( final String name, final String address, final String state,
            final String zipcode ) {
        waitForAngular();
        assertFalse( driver.getPageSource().contains( name ) );
    }

    @Then ( "^an error occurs with (.+) and (.+) is printed$" )
    public void ensureErrorMessageFinish ( final String error, final String errorMessage ) {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Could not add Pharmacy." ) );
        assertTrue( driver.getPageSource().contains( errorMessage ) );
    }
}
