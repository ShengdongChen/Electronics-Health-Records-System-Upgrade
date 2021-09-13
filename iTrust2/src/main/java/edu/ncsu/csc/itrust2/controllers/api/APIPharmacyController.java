package edu.ncsu.csc.itrust2.controllers.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.PharmacyForm;
import edu.ncsu.csc.itrust2.forms.admin.StockedDrugsForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the Pharmacy model. In all
 * requests made to this controller, the {id} provided is a String that is the
 * name of the pharmacy desired.
 *
 * @author Addison Agatucci
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIPharmacyController extends APIController {

    /**
     * Retrieves a list of all Pharmacies in the database
     *
     * @return list of pharmacies
     */
    @GetMapping ( BASE_PATH + "/pharmacies" )
    public Set<PharmacyForm> getPharmacies () {
        final List<Pharmacy> pharmacies = Pharmacy.getPharmacies();
        final Set<PharmacyForm> response = new HashSet<>();
        for ( final Pharmacy pharmacy : pharmacies ) {
            response.add( new PharmacyForm( pharmacy ) );
        }
        return response;
    }

    /**
     * Retrieves the Pharmacy specified by the name provided
     *
     * @param id
     *            The name of the pharmacy
     * @return response
     */
    @Transactional
    @GetMapping ( BASE_PATH + "/pharmacies/{id}" )
    public ResponseEntity getPharmacy ( @PathVariable ( "id" ) final String id ) {
        final Pharmacy pharmacy = Pharmacy.getByName( id );
        if ( null != pharmacy ) {
            LoggerUtil.log( TransactionType.VIEW_PHARMACY, LoggerUtil.currentUser() );
        }
        if ( null == pharmacy ) {
            return new ResponseEntity( errorResponse( "No pharmacy found for name " + id ), HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( new PharmacyForm( pharmacy ), HttpStatus.OK );
    }

    /**
     * Retrieves the Pharmacies specified by the zipcode provided
     *
     * @param zip
     *            The zipcode of the pharmacy
     * @return response
     */
    @GetMapping ( BASE_PATH + "/pharmacies/zipcode/{zip}" )
    public ResponseEntity getPharmaciesByZipcode ( @PathVariable ( "zip" ) final String zip ) {
        final List<Pharmacy> pharmacies = Pharmacy.getByZipcode( zip );
        if ( null != pharmacies ) {
            LoggerUtil.log( TransactionType.VIEW_PHARMACY, LoggerUtil.currentUser() );
        }
        if ( pharmacies.isEmpty() ) {
            return new ResponseEntity( errorResponse( "No pharmacy found in zipcode " + zip ), HttpStatus.NOT_FOUND );
        }
        return null == pharmacies
                ? new ResponseEntity( errorResponse( "Error searching zipcode " + zip ), HttpStatus.BAD_REQUEST )
                : new ResponseEntity( pharmacies, HttpStatus.OK );
    }

    /**
     * Creates a new Pharmacy from the RequestBody provided.
     *
     * @param pharmacyF
     *            The Pharmacy to be validated and saved to the database.
     * @return response
     */
    @PostMapping ( BASE_PATH + "/pharmacies" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN') " )
    public ResponseEntity createPharmacy ( @RequestBody final PharmacyForm pharmacyF ) {
        final Pharmacy pharmacy = new Pharmacy( pharmacyF );
        if ( null != Pharmacy.getByName( pharmacy.getName() ) ) {
            return new ResponseEntity(
                    errorResponse( "Pharmacy with the name " + pharmacy.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        try {
            pharmacy.setDrugs( new HashSet( Drug.getAll() ) );
            pharmacy.save();
            LoggerUtil.log( TransactionType.CREATE_PHARMACY, LoggerUtil.currentUser() );
            return new ResponseEntity( new PharmacyForm( pharmacy ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Error occured while validating or saving " + pharmacy.toString()
                    + " because of " + e.getMessage() ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the pharmacy with the name provided by overwriting it with the
     * new Pharmacy provided.
     *
     * @param id
     *            Name of the pharmacy to update
     * @param pharmacyF
     *            The new pharmacy to save to this name
     * @return response
     */
    @PutMapping ( BASE_PATH + "/pharmacies/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN') " )
    public ResponseEntity updatePharmacy ( @PathVariable final String id, @RequestBody final PharmacyForm pharmacyF ) {
        final Pharmacy pharmacy = new Pharmacy( pharmacyF );
        final Pharmacy dbPharmacy = Pharmacy.getByName( id );
        if ( null == dbPharmacy ) {
            return new ResponseEntity( errorResponse( "No pharmacy found for name " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            // We have a seperate endpoints for these
            pharmacy.setDrugs( dbPharmacy.getDrugs() );
            pharmacy.setPrescriptions( dbPharmacy.getPrescriptions() );

            pharmacy.save(); /* Will overwrite existing request */
            if ( !pharmacy.getName().equals( id ) ) {
                // If we are editing the name, we have to delete the old record,
                // because name is used as the primary key in hibernate.
                dbPharmacy.delete();
            }
            LoggerUtil.log( TransactionType.EDIT_PHARMACY, LoggerUtil.currentUser() );
            return new ResponseEntity( new PharmacyForm( pharmacy ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the pharmacy with the id matching the given id. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the pharmacy to delete
     * @return the id of the deleted pharmacy
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/pharmacies/{id}" )
    public ResponseEntity deletePharmacy ( @PathVariable final String id ) {
        try {
            final Pharmacy pharmacy = Pharmacy.getByName( id );
            if ( pharmacy == null ) {
                LoggerUtil.log( TransactionType.DELETE_PHARMACY, LoggerUtil.currentUser(),
                        "Could not find pharmacy with id " + id );
                return new ResponseEntity( errorResponse( "No pharmacy found with name " + id ), HttpStatus.NOT_FOUND );
            }
            pharmacy.delete();
            LoggerUtil.log( TransactionType.DELETE_PHARMACY, LoggerUtil.currentUser(),
                    "Deleted pharmacy with name " + pharmacy.getName() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.DELETE_PHARMACY, LoggerUtil.currentUser(), "Failed to delete pharmacy" );
            return new ResponseEntity( errorResponse( "Could not delete pharmacy: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/pharmacies/drugs/{id}" )
    public ResponseEntity setStockedDrugs ( @PathVariable final String id,
            @RequestBody final StockedDrugsForm drugsF ) {
        final Pharmacy dbPharmacy = Pharmacy.getByName( id );
        if ( dbPharmacy == null ) {
            return new ResponseEntity( errorResponse( "No pharmacy found with name " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            dbPharmacy.setDrugs( drugsF );
            dbPharmacy.save();
            return new ResponseEntity( new PharmacyForm( dbPharmacy ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update inventory " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    @PreAuthorize ( "hasRole('ROLE_PHARMACIST')" )
    @GetMapping ( BASE_PATH + "/pharmacies/drugs/{id}" )
    public ResponseEntity getStockedDrugs ( @PathVariable final String id ) {
        final Pharmacy dbPharmacy = Pharmacy.getByName( id );
        if ( dbPharmacy == null ) {
            return new ResponseEntity( errorResponse( "No pharmacy found with name " + id ), HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( new PharmacyForm( dbPharmacy ).getDrugs(), HttpStatus.OK );
    }

    @PreAuthorize ( "hasRole('ROLE_PHARMACIST')" )
    @GetMapping ( BASE_PATH + "/pharmacies/prescriptions/{id}" )
    public ResponseEntity getPrescriptions ( @PathVariable final String id ) {
        final Pharmacy dbPharmacy = Pharmacy.getByName( id );
        if ( dbPharmacy == null ) {
            return new ResponseEntity( errorResponse( "No pharmacy found with name " + id ), HttpStatus.NOT_FOUND );
        }
        fillPrescriptionWithPatientData( dbPharmacy );

        return new ResponseEntity( new PharmacyForm( dbPharmacy ).getPrescriptions(), HttpStatus.OK );
    }

    /**
     * If you are logged in as a Pharmacist, you can use this to get the
     * pharmacy you are connected to.
     *
     * @return The pharmacy object of the current pharmacist
     */
    @GetMapping ( BASE_PATH + "/pharmacies/current" )
    @PreAuthorize ( "hasRole('ROLE_PHARMACIST')" )
    public ResponseEntity getCurrentPharmacy () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        final Pharmacy pharm = self.getPharmacy();
        if ( pharm == null ) {
            return new ResponseEntity(
                    errorResponse( "Could not find a pharmacy entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            return new ResponseEntity( unlinkCircularReferences( pharm ), HttpStatus.OK );
        }
    }

    /**
     * Helper function to convert the prescription's User into a Patient. Also
     * removes circular references in patient
     *
     * @param dbPharmacy
     *            The pharmacy getting returned
     */
    private void fillPrescriptionWithPatientData ( final Pharmacy dbPharmacy ) {
        for ( final Prescription p : dbPharmacy.getPrescriptions() ) {
            final Patient patient = Patient.getByName( p.getPatient().getId() );
            if ( patient == null ) {
                continue;
            }
            patient.setRepresentatives( null );
            patient.setRepresented( null );
            User.unlinkCircularReferences( patient.getSelf() );
            p.setPatientModel( patient );
        }
    }

    /**
     * Helper function to remove circular references from the Pharmacy object
     *
     * @param pharmacy
     *            The pharmcy to return
     * @return The pharmacy without circular references
     */
    private Pharmacy unlinkCircularReferences ( final Pharmacy pharmacy ) {
        for ( final User patient : pharmacy.getPatients() ) {
            patient.setPharmacy( null );
        }
        for ( final Prescription prescription : pharmacy.getPrescriptions() ) {
            User.unlinkCircularReferences( prescription.getPatient() );
            Prescription.unlinkCircularReferences( prescription );
        }
        return pharmacy;
    }

}
