package edu.ncsu.csc.itrust2.controllers.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.PrescriptionStatus;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.EmailUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with prescriptions. Exposes functionality
 * to add, edit, fetch, and delete prescriptions.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPrescriptionController extends APIController {

    /**
     * Adds a new prescription to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new prescription
     * @return the created prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PHARMACIST', 'ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @PostMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity addPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = new Prescription( form );
            p.save();
            notifyPatientOfPrescriptionChange( p.getPatient(), PrescriptionStatus.CREATED, p.getStatus() );
            LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Created prescription with id " + p.getId() );
            return new ResponseEntity( new PrescriptionForm( p ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(),
                    "Failed to create prescription" );
            return new ResponseEntity( errorResponse( "Could not save the prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits an existing prescription in the system. Matches prescriptions by
     * ids. Requires HCP permissions.
     *
     * @param form
     *            the form containing the details of the new prescription
     * @return the edited prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PHARMACIST', 'ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @PutMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity editPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = new Prescription( form );
            final Prescription saved = Prescription.getById( p.getId() );
            if ( saved == null ) {
                LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(),
                        "No prescription found with id " + p.getId() );
                return new ResponseEntity( errorResponse( "No prescription found with id " + p.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            final PrescriptionStatus oldStatus = saved.getStatus();
            p.save(); /* Overwrite existing */
            notifyPatientOfPrescriptionChange( p.getPatient(), oldStatus, p.getStatus() );
            LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Edited prescription with id " + p.getId() );
            return new ResponseEntity( new PrescriptionForm( p ), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(),
                    "Failed to edit prescription" );
            return new ResponseEntity( errorResponse( "Failed to update prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the prescription with the given id.
     *
     * @param id
     *            the id
     * @return the id of the deleted prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PHARMACIST', 'ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @DeleteMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity deletePrescription ( @PathVariable final Long id ) {
        final Prescription p = Prescription.getById( id );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "No prescription found with id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            p.delete();
            LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Deleted prescription with id " + p.getId() );
            return new ResponseEntity( p.getId(), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Failed to delete prescription" );
            return new ResponseEntity( errorResponse( "Failed to delete prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all the prescriptions in the system.
     *
     * @return all saved prescriptions
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PHARMACIST', 'ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST', 'ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/prescriptions" )
    public Set<PrescriptionForm> getPrescriptions () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        if ( self.isDoctor() ) {
            // Return all prescriptions in system
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "HCP viewed a list of all prescriptions" );
            final Set<PrescriptionForm> response = new HashSet<>();
            for ( final Prescription p : Prescription.getPrescriptions() ) {
                response.add( new PrescriptionForm( p ) );
            }
            return response;
        }
        else {
            // Issue #106
            // Return only prescriptions assigned to the patient
            LoggerUtil.log( TransactionType.PATIENT_PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "Patient viewed a list of their prescriptions" );
            final List<Prescription> prescriptions = Prescription.getForPatient( LoggerUtil.currentUser() );
            final Set<PrescriptionForm> response = new HashSet<>();
            for ( final Prescription p : prescriptions ) {
                response.add( new PrescriptionForm( p ) );
            }
            return response;
        }
    }

    /**
     * Returns a single prescription using the given id.
     *
     * @param id
     *            the id of the desired prescription
     * @return the requested prescription
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PHARMACIST', 'ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @GetMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity getPrescription ( @PathVariable final Long id ) {
        final Prescription p = Prescription.getById( id );
        if ( p == null ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "Failed to find prescription with id " + id );
            return new ResponseEntity( errorResponse( "No prescription found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(), "Viewed prescription  " + id );
            return new ResponseEntity( new PrescriptionForm( p ), HttpStatus.OK );
        }
    }

    private void notifyPatientOfPrescriptionChange ( User patient, PrescriptionStatus oldStatus,
            PrescriptionStatus newStatus ) {
        if ( oldStatus == null || newStatus == null || patient == null ) {
            return;
        }

        final String patientEmail = EmailUtil.getEmailByUsername( patient.getId() );
        if ( oldStatus == PrescriptionStatus.CREATED && newStatus == PrescriptionStatus.SENT_TO_PHARMACY ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_SENT_DEFAULT_PHARMACY, patient.getId(),
                    LoggerUtil.currentUser() );
        }

        // Prescription created/sent -> filled
        if ( ( oldStatus == PrescriptionStatus.CREATED || oldStatus == PrescriptionStatus.SENT_TO_PHARMACY )
                && newStatus == PrescriptionStatus.FILLED ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_FILLED, patient.getId(), LoggerUtil.currentUser() );
            emailPatientOfPrescriptionChange( patient, TransactionType.PRESCRIPTION_FILLED );
        }

        // Prescription created/sent -> canceled
        if ( ( oldStatus == PrescriptionStatus.CREATED || oldStatus == PrescriptionStatus.SENT_TO_PHARMACY )
                && newStatus == PrescriptionStatus.CANCELLED ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_CANCELED, patient.getId(), LoggerUtil.currentUser() );
            emailPatientOfPrescriptionChange( patient, TransactionType.PRESCRIPTION_FILLED );
        }

    }

    private void emailPatientOfPrescriptionChange ( User patient, TransactionType updateType ) {
        if ( patient == null ) {
            return;
        }

        final String patientEmail = EmailUtil.getEmailByUsername( patient.getUsername() );
        if ( patientEmail != null ) {
            try {
                EmailUtil.sendEmail( patientEmail, updateType.getDescription(),
                        updateType.getDescription() + "\n Please check iTrust2 for more information" );
            }
            catch ( final MessagingException e ) {
                LoggerUtil.log( TransactionType.PRESCRIPTION_EMAIL_NOT_SENT, LoggerUtil.currentUser(),
                        patient.getId() );
            }
        }
    }

}
