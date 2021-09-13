package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.PharmacyForm;
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.DrugType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with pharmacies
 *
 * @author Eli Newman
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIPharmacyControllerTest {

    /**
     * MVC for controller
     */
    private MockMvc               mvc;

    /**
     * Test fields
     */
    private static Pharmacy       pharmacy;

    private static Drug           drug1;

    private static Drug           drug2;

    private static Prescription   prescription1;

    private static Prescription   prescription2;

    /**
     * Context for mvc
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * Test set up
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        ensureTestUsersExist();
        pharmacy = createTestPharmacy( "Pharmacy with Prescriptions" );
        drug1 = createTestDrug( "0112-0000-00" );
        drug2 = createTestDrug( "0112-0001-00" );
        prescription2 = createTestPrescription( pharmacy, drug2, "BillyBob" );
        prescription1 = createTestPrescription( pharmacy, drug1, "AliceThirteen" );
    }

    /**
     * Cleans up all of the objects so they're fresh at the start of the tests
     *
     * @throws Exception
     *             If it had trouble deleting data
     */
    @After
    public void cleanUp () throws Exception {
        try {
            Prescription.getById( prescription1.getId() ).delete();
            Prescription.getById( prescription2.getId() ).delete();
            pharmacy = Pharmacy.getByName( pharmacy.getName() );
            pharmacy.setDrugs( new HashSet<>() );
            pharmacy.save();
            pharmacy.delete();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            throw new Exception( "Was unable to delete some of the test data. See log" );
        }
    }

    /**
     * Tests getting a non existent Pharmacy and ensures that the correct status
     * is returned.
     *
     * @throws Exception
     *             for possible errors
     */
    @Test
    public void testGetNonExistentPharmacy () throws Exception {
        mvc.perform( get( "/api/v1/pharmacies/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PharmacyAPI
     *
     * @throws Exception
     *             for possible errors
     */
    @SuppressWarnings ( "deprecation" )
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testPharmacyAPI () throws Exception {
        mvc.perform( delete( "/api/v1/pharmacies/iTrust Test Pharmacy 2" ) );
        final Pharmacy pharmacy = new Pharmacy( "iTrust Test Pharmacy 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy ) ) );
        mvc.perform( get( "/api/v1/pharmacies/iTrust Test Pharmacy 2" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Cannot create same Pharmacy twice
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy ) ) ).andExpect( status().isConflict() );

        pharmacy.setAddress( "2 iTrust Test Street" );
        mvc.perform( put( "/api/v1/pharmacies/iTrust Test Pharmacy 2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Make sure that the put didn't break anything
        mvc.perform( get( "/api/v1/pharmacies/iTrust Test Pharmacy 2" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Editing a non-existent Pharmacy should not work

        mvc.perform( put( "/api/v1/pharmacies/This really shouldnt be here" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy ) ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests the search by zipcode functionality
     */
    @SuppressWarnings ( "deprecation" )
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testPharmacySearchByZipcode () throws Exception {
        mvc.perform( delete( "/api/v1/pharmacies" ) );
        final Pharmacy pharmacy2 = new Pharmacy( "iTrust Test Pharmacy 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy2 ) ) );
        final Pharmacy pharmacy1 = new Pharmacy( "iTrust Test Pharmacy 1", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy1 ) ) );
        final Pharmacy pharmacy3 = new Pharmacy( "iTrust Test Pharmacy 3", "1 iTrust Test Street", "12345-5678", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pharmacy3 ) ) );

        // ensuring valid zipcode
        mvc.perform( get( "/api/v1/pharmacies/zipcode/27607" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // searching for a non-existent zipcode
        mvc.perform( get( "/api/v1/pharmacies/zipcode/nozipcode" ) ).andExpect( status().isNotFound() );
    }

    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN", "PHARMACIST" } )
    public void testAddingAndCheckingStockedDrugs () throws Exception {
        // This will instantiate the lists. Needed to make the responses match
        pharmacy.setDrugs( new HashSet<>() );
        pharmacy.setPrescriptions( new HashSet<>() );
        pharmacy.save();

        // The pharmacy exists
        final String pharm = mvc.perform( get( "/api/v1/pharmacies/" + pharmacy.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) ).andReturn().getResponse()
                .getContentAsString();

        assertEquals( TestUtils.asJsonString( new PharmacyForm( pharmacy ) ), pharm );

        // Update Pharmacy with new inventory
        pharmacy.addDrug( drug1 );
        String updatedPharmacy = mvc
                .perform( put( "/api/v1/pharmacies/drugs/" + pharmacy.getId() )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( pharmacy ) ) )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) ).andReturn().getResponse()
                .getContentAsString();

        // Pharmacy should have been updated
        assertEquals( TestUtils.asJsonString( new PharmacyForm( pharmacy ) ), updatedPharmacy );

        pharmacy.setAddress( "New Location" );
        updatedPharmacy = mvc
                .perform( put( "/api/v1/pharmacies/" + pharmacy.getId() ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( pharmacy ) ) )
                .andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) ).andReturn().getResponse()
                .getContentAsString();

        assertEquals( TestUtils.asJsonString( new PharmacyForm( pharmacy ) ), updatedPharmacy );

        // Check that we can get the StockedDrugs from the GET endpoint
        final String drugList = mvc.perform( get( "/api/v1/pharmacies/drugs/" + pharmacy.getId() ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( TestUtils.asJsonString( new PharmacyForm( pharmacy ).getDrugs() ), drugList );
    }

    @Test
    @WithMockUser ( username = "pharmacist", roles = { "ADMIN", "PHARMACIST" } )
    public void testGetPrescriptions () throws Exception {
        pharmacy.setPrescriptions( new HashSet<>() );
        pharmacy.save();

        // Empty prescriptions
        String prescriptionListStr = mvc.perform( get( "/api/v1/pharmacies/prescriptions/" + pharmacy.getId() ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertEquals( "[]", prescriptionListStr );

        // Add a few prescriptions
        pharmacy.addPrescriptions( prescription1 );
        pharmacy.addPrescriptions( prescription2 );
        pharmacy.save();
        prescriptionListStr = mvc.perform( get( "/api/v1/pharmacies/prescriptions/" + pharmacy.getId() ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( prescriptionListStr.contains( prescription1.getDrug().getCode() ) );
        assertTrue( prescriptionListStr.contains( prescription2.getDrug().getCode() ) );
    }

    private Prescription createTestPrescription ( final Pharmacy pharmacy, final Drug drug, final String patientName )
            throws UnsupportedEncodingException, Exception {

        final PrescriptionForm form1 = new PrescriptionForm();
        form1.setDrug( drug.getCode() );
        form1.setDosage( 100 );
        form1.setRenewals( 12 );
        form1.setStartDate( "2009-10-10" ); // 10/10/2009
        form1.setEndDate( "2010-10-10" ); // 10/10/2010
        form1.setPatient( patientName );
        form1.setPharmacy( pharmacy.getId() );
        form1.setStatus( "Created" );

        final Prescription prescription = new Prescription( form1 );
        prescription.save();

        return prescription;
    }

    private Drug createTestDrug ( final String code ) throws Exception {
        Drug drug = Drug.getByCode( code );
        if ( drug != null ) {
            return drug;
        }

        drug = new Drug();
        drug.setCode( code );
        drug.setDescription( "Test Drug" );
        drug.setName( "Test Drug" );
        drug.setGenericName( "Test Drug" );
        drug.setType( DrugType.Generic );
        drug.save();

        return drug;
    }

    private void ensureTestUsersExist () {
        if ( User.getByName( "AliceThirteen" ) == null ) {
            final Patient alice = new Patient();
            alice.setFirstName( "AliceThirteen" );
            final User aliceUser = new User( "AliceThirteen",
                    "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_PATIENT, 1 );
            aliceUser.save();
            alice.setSelf( aliceUser );
            alice.setLastName( "Smith" );
            alice.setDateOfBirth( LocalDate.now().minusYears( 13 ) );
            alice.setPrescriptionPreference( DrugType.Generic );
            alice.save();
        }
        if ( User.getByName( "BillyBob" ) == null ) {
            final Patient billy = new Patient();
            billy.setFirstName( "Billy" );
            final User billyUser = new User( "BillyBob", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                    Role.ROLE_PATIENT, 1 );
            billyUser.save();
            billy.setSelf( billyUser );
            billy.setLastName( "Bob" );
            billy.setDateOfBirth( LocalDate.now().minusYears( 40 ) );
            billy.save();
        }

    }

    private Pharmacy createTestPharmacy ( final String name ) throws Exception {
        Pharmacy pharmacy = Pharmacy.getByName( name );
        if ( pharmacy != null ) {
            pharmacy.delete();
        }

        pharmacy = new Pharmacy( name, "123 Main Street", "80001", "NC" );
        pharmacy.save();
        return pharmacy;
    }

}
