package edu.ncsu.csc.itrust2.forms.admin;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;

/**
 * Form used for adding drugs to pharmacy. Seperate from the Pharmacy form to
 * avoid updates to the Pharamcy removing drugs
 *
 * @author Dominic Brown
 *
 */
public class StockedDrugsForm {

    @NotEmpty
    private PharmacyForm  pharm;

    private Set<DrugForm> drugs;

    public StockedDrugsForm ( final Pharmacy pharm ) {
        setDrugs( pharm.getDrugs() );
    }

    private void setDrugs ( final Set<Drug> drugs ) {
        for ( final Drug drug : drugs ) {
            getDrugs().add( new DrugForm( drug ) );
        }
    }

    /**
     * Get the stockedDrugs
     *
     * @return the stockedDrugs
     */
    public Set<DrugForm> getDrugs () {
        if ( drugs == null ) {
            drugs = new HashSet<DrugForm>();
        }
        return drugs;
    }

}
