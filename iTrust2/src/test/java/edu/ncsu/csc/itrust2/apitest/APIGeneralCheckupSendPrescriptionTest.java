package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import edu.ncsu.csc.itrust2.forms.admin.DrugForm;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp.GeneralCheckupForm;
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for the API functionality for interacting with office visits
 *
 * @author Yizhuo Wu
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIGeneralCheckupSendPrescriptionTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        final Patient p = Patient.getByName( "patient" );
        if ( p != null ) {
            p.delete();
        }

        for ( final Pharmacy phar : Pharmacy.getPharmacies() ) {
            phar.setDrugs( new HashSet<Drug>() );
            phar.setPrescriptions( new HashSet<>() );
            phar.save();
        }
        Prescription.deleteAll( Prescription.class );
        Drug.deleteAll( Drug.class );

    }

    /**
     * Tests OfficeVisitAPI to send prescription to patient's default pharmacy
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT", "HCP", "ADMIN" } )
    public void testGeneralCheckupAPISendPrescription () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        final Pharmacy phar = new Pharmacy( "CVS Test", "123 Main", "27606", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( phar ) ) );

        final DrugForm drug = new DrugForm();
        drug.setCode( "0000-0000-01" );
        drug.setName( "Drug Testing" );
        drug.setDescription( "Test drug" );
        drug.setGenericName( "DRUG TEST" );

        mvc.perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug ) ) ).andExpect( status().isOk() );

        final PatientForm pat = new PatientForm();
        pat.setAddress1( "1 Test Street" );
        pat.setAddress2( "Some Location" );
        pat.setBloodType( BloodType.APos.toString() );
        pat.setCity( "Raleigh" );
        pat.setDateOfBirth( "1977-06-15" );
        pat.setEmail( "ywu44@ncsu.ed" );
        pat.setEthnicity( Ethnicity.Asian.toString() );
        pat.setFirstName( "Wu" );
        pat.setGender( Gender.Male.toString() );
        pat.setLastName( "Woo" );
        pat.setPhone( "123-456-7890" );
        pat.setSelf( "patient" );
        pat.setState( State.NC.toString() );
        pat.setZip( "27514" );
        pat.setDefaultPharmacy( "CVS Test" );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pat ) ) );

        final List<PrescriptionForm> preList = new ArrayList<PrescriptionForm>();

        final PrescriptionForm pre = new PrescriptionForm();
        pre.setDrug( drug.getCode() );
        pre.setDosage( 200 );
        pre.setRenewals( 3 );
        pre.setPatient( "patient" );
        pre.setStartDate( "2020-11-06" );
        pre.setEndDate( "2020-11-30" );

        preList.add( pre );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final GeneralCheckupForm visit = new GeneralCheckupForm();
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        visit.setPrescriptions( preList );

        mvc.perform( post( "/api/v1/generalcheckups" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();
        mvc.perform( delete( "/api/v1/generalcheckups/" + id ) ).andExpect( status().isOk() );

    }

    /**
     * Tests OfficeVisitAPI to send prescription to patient's default pharmacy
     * but without email
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT", "HCP", "ADMIN" } )
    public void testGeneralCheckupAPISendPrescriptionNoEmail () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        final Pharmacy phar = new Pharmacy( "CVS Test", "123 Main", "27606", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( phar ) ) );

        final DrugForm drug = new DrugForm();
        drug.setCode( "0000-0000-01" );
        drug.setName( "Drug Testing" );
        drug.setDescription( "Test drug" );
        drug.setGenericName( "DRUG TEST" );

        mvc.perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug ) ) ).andExpect( status().isOk() );

        final PatientForm pat = new PatientForm();
        pat.setAddress1( "1 Test Street" );
        pat.setAddress2( "Some Location" );
        pat.setBloodType( BloodType.APos.toString() );
        pat.setCity( "Raleigh" );
        pat.setDateOfBirth( "1977-06-15" );
        pat.setEthnicity( Ethnicity.Asian.toString() );
        pat.setFirstName( "Wu" );
        pat.setGender( Gender.Male.toString() );
        pat.setLastName( "Woo" );
        pat.setPhone( "123-456-7890" );
        pat.setSelf( "patient" );
        pat.setState( State.NC.toString() );
        pat.setZip( "27514" );
        pat.setDefaultPharmacy( "CVS Test" );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pat ) ) );

        final List<PrescriptionForm> preList = new ArrayList<PrescriptionForm>();

        final PrescriptionForm pre = new PrescriptionForm();
        pre.setDrug( drug.getCode() );
        pre.setDosage( 200 );
        pre.setRenewals( 3 );
        pre.setPatient( "patient" );
        pre.setStartDate( "2020-11-06" );
        pre.setEndDate( "2020-11-30" );

        preList.add( pre );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final GeneralCheckupForm visit = new GeneralCheckupForm();
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        visit.setPrescriptions( preList );

        mvc.perform( post( "/api/v1/generalcheckups" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();
        mvc.perform( delete( "/api/v1/generalcheckups/" + id ) ).andExpect( status().isOk() );

    }

    /**
     * Tests OfficeVisitAPI to send prescription to the pharmacy that HCP
     * selected and send email to patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT", "HCP", "ADMIN" } )
    public void testGeneralCheckupAPISendPrescriptionNoDefaultPharmacy () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        final Pharmacy phar = new Pharmacy( "CVS Test", "123 Main", "27606", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( phar ) ) );

        final DrugForm drug = new DrugForm();
        drug.setCode( "0000-0000-01" );
        drug.setName( "Drug Testing" );
        drug.setDescription( "Test drug" );
        drug.setGenericName( "DRUG TEST" );

        mvc.perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug ) ) ).andExpect( status().isOk() );

        final PatientForm pat = new PatientForm();
        pat.setAddress1( "1 Test Street" );
        pat.setAddress2( "Some Location" );
        pat.setBloodType( BloodType.APos.toString() );
        pat.setCity( "Raleigh" );
        pat.setDateOfBirth( "1977-06-15" );
        pat.setEthnicity( Ethnicity.Asian.toString() );
        pat.setFirstName( "Wu" );
        pat.setGender( Gender.Male.toString() );
        pat.setLastName( "Woo" );
        pat.setEmail( "ywu44@ncsu.ed" );
        pat.setPhone( "123-456-7890" );
        pat.setSelf( "patient" );
        pat.setState( State.NC.toString() );
        pat.setZip( "27514" );
        pat.setDefaultPharmacy( null );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pat ) ) );

        final List<PrescriptionForm> preList = new ArrayList<PrescriptionForm>();

        final PrescriptionForm pre = new PrescriptionForm();
        pre.setDrug( drug.getCode() );
        pre.setDosage( 200 );
        pre.setRenewals( 3 );
        pre.setPatient( "patient" );
        pre.setStartDate( "2020-11-06" );
        pre.setEndDate( "2020-11-30" );

        preList.add( pre );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final GeneralCheckupForm visit = new GeneralCheckupForm();
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        visit.setPrescriptions( preList );

        mvc.perform( post( "/api/v1/generalcheckups/" + "CVS Test" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();
        mvc.perform( delete( "/api/v1/generalcheckups/" + id ) ).andExpect( status().isOk() );

    }

    /**
     * Tests OfficeVisitAPI to send prescription to pharmacy that HCP selected
     * but not send email to patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT", "HCP", "ADMIN" } )
    public void testGeneralCheckupAPISendPrescriptionNoDefaultPharmacyNoEmail () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        final Pharmacy phar = new Pharmacy( "CVS Test", "123 Main", "27606", "NC" );
        mvc.perform( post( "/api/v1/pharmacies" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( phar ) ) );

        final DrugForm drug = new DrugForm();
        drug.setCode( "0000-0000-01" );
        drug.setName( "Drug Testing" );
        drug.setDescription( "Test drug" );
        drug.setGenericName( "DRUG TEST" );

        mvc.perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug ) ) ).andExpect( status().isOk() );

        final PatientForm pat = new PatientForm();
        pat.setAddress1( "1 Test Street" );
        pat.setAddress2( "Some Location" );
        pat.setBloodType( BloodType.APos.toString() );
        pat.setCity( "Raleigh" );
        pat.setDateOfBirth( "1977-06-15" );
        pat.setEthnicity( Ethnicity.Asian.toString() );
        pat.setFirstName( "Wu" );
        pat.setGender( Gender.Male.toString() );
        pat.setLastName( "Woo" );
        pat.setPhone( "123-456-7890" );
        pat.setSelf( "patient" );
        pat.setState( State.NC.toString() );
        pat.setZip( "27514" );
        pat.setDefaultPharmacy( null );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pat ) ) );

        final List<PrescriptionForm> preList = new ArrayList<PrescriptionForm>();

        final PrescriptionForm pre = new PrescriptionForm();
        pre.setDrug( drug.getCode() );
        pre.setDosage( 200 );
        pre.setRenewals( 3 );
        pre.setPatient( "patient" );
        pre.setStartDate( "2020-11-06" );
        pre.setEndDate( "2020-11-30" );

        preList.add( pre );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final GeneralCheckupForm visit = new GeneralCheckupForm();
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        visit.setPrescriptions( preList );

        mvc.perform( post( "/api/v1/generalcheckups/" + "CVS Test" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();
        mvc.perform( delete( "/api/v1/generalcheckups/" + id ) ).andExpect( status().isOk() );

    }

}
