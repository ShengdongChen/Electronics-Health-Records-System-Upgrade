package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;

import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * A form for REST API communication. Contains fields for constructing
 * Prescription objects.
 *
 * @author Connor
 */
public class PrescriptionForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            drug;
    private String            drugName;
    private int               dosage;
    private String            startDate;
    private String            endDate;
    private int               renewals;
    private String            patient;
    private Long              id;
    private String            pharmacy;

    /**
     * The status of this prescription
     */

    private String            status;

    /**
     * Empty constructor for filling in fields without a Prescription object.
     */
    public PrescriptionForm () {
    }

    /**
     * Constructs a new form with information from the given prescription.
     *
     * @param prescription
     *            the prescription object
     */
    public PrescriptionForm ( final Prescription prescription ) {
        setId( prescription.getId() );
        setDrug( prescription.getDrug().getCode() );
        setDrugName( prescription.getDrug().getName() );
        setDosage( prescription.getDosage() );
        setStartDate( prescription.getStartDate().toString() );
        setEndDate( prescription.getEndDate().toString() );
        setRenewals( prescription.getRenewals() );
        setPatient( prescription.getPatient().getUsername() );
        setStatus( prescription.getStatus().toString() );
        setPharmacy( prescription.getPharmacy() );
    }

    /**
     * Returns the drug associated with this Prescription
     *
     * @return the prescription's drug
     */
    public String getDrug () {
        return drug;
    }

    /**
     * Associates this prescription with the given drug.
     *
     * @param drug
     *            the drug
     */
    public void setDrug ( final String drug ) {
        this.drug = drug;
    }

    public String getDrugName () {
        return drugName;
    }

    public void setDrugName ( String drugName ) {
        this.drugName = drugName;
    }

    /**
     * Returns the prescribed dosage of the drug.
     *
     * @return the drug dosage
     */
    public int getDosage () {
        return dosage;
    }

    /**
     * Sets the prescription's dosage in milligrams.
     *
     * @param dosage
     *            prescription dosage
     */
    public void setDosage ( final int dosage ) {
        this.dosage = dosage;
    }

    /**
     * Returns the drug's first valid day.
     *
     * @return the start date
     */
    public String getStartDate () {
        return startDate;
    }

    /**
     * Sets the prescription's first valid day to the given date.
     *
     * @param startDate
     *            the first valid day
     */
    public void setStartDate ( final String startDate ) {
        this.startDate = startDate;
    }

    /**
     * Returns the prescription's final valid date.
     *
     * @return the prescription's end date
     */
    public String getEndDate () {
        return endDate;
    }

    /**
     * Sets the prescription's final valid date.
     *
     * @param endDate
     *            the end date
     */
    public void setEndDate ( final String endDate ) {
        this.endDate = endDate;
    }

    /**
     * Gets the prescription's number of renewals.
     *
     * @return the number of renewals
     */
    public int getRenewals () {
        return renewals;
    }

    /**
     * Sets the prescription's number of renewals to the given number.
     *
     * @param renewals
     *            the number of renewals
     */
    public void setRenewals ( final int renewals ) {
        this.renewals = renewals;
    }

    /**
     * Returns the user associated with this prescription.
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the prescription's patient to the given user
     *
     * @param patient
     *            the patient
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Returns the id associated with the Prescription.
     *
     * @return the prescription id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the Prescription's unique id.
     *
     * @param id
     *            the prescription id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus ( final String status ) {
        this.status = status;
    }

    /**
     * Returns the pharmacy that stores the prescription
     *
     * @return pharmacy of prescription
     */
    public String getPharmacy () {
        return pharmacy;
    }

    /**
     * Sets the prescription's pharmacy
     *
     * @param pharmacy
     *            to set
     */
    public void setPharmacy ( final String pharmacy ) {
        this.pharmacy = pharmacy;

    }

    public void setPharmacy ( final Pharmacy pharmacy ) {
        if ( pharmacy != null ) {
            setPharmacy( pharmacy.getName() );
        }
    }
}
