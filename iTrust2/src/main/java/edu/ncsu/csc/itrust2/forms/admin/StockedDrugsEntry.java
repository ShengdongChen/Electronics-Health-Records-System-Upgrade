package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.NotEmpty;

public class StockedDrugsEntry {

    @NotEmpty
    private String   dosage;

    @NotEmpty
    private DrugForm drug;

    public StockedDrugsEntry ( final String dosage, final DrugForm drug ) {
        this.dosage = dosage;
        this.drug = drug;
    }

    /**
     * Getter for dosage
     *
     * @return the dosage
     */
    public String getDosage () {
        return dosage;
    }

    /**
     * Setter for dosage
     *
     * @param dosage
     *            the dosage to set
     */
    public void setDosage ( final String dosage ) {
        this.dosage = dosage;
    }

    /**
     * Getter for drugId
     *
     * @return the drugId
     */
    public DrugForm getDrug () {
        return drug;
    }

    /**
     * Setter for drugId
     *
     * @param drugId
     *            the drugId to set
     */
    public void setDrugId ( final DrugForm drugId ) {
        this.drug = drugId;
    }

}
