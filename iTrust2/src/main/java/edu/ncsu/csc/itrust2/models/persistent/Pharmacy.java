package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.forms.admin.DrugForm;
import edu.ncsu.csc.itrust2.forms.admin.PharmacyForm;
import edu.ncsu.csc.itrust2.forms.admin.StockedDrugsForm;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * Class representing a Pharmacy in the database
 *
 * @author Eli Newman
 *
 */
@Entity
@Table ( name = "Pharmacies" )
public class Pharmacy extends DomainObject<Pharmacy> implements Serializable {
    /**
     * Used for serializing the object.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a Pharmacy object from the PharmacyForm object provided
     *
     * @param pf
     *            A PharmacyForm to convert to a Pharmacy
     */
    public Pharmacy ( final PharmacyForm pf ) {
        setName( pf.getName() );
        setAddress( pf.getAddress() );
        setZip( pf.getZip() );
        setState( State.parse( pf.getState() ) );
    }

    /**
     * Construct a Pharmacy object from all of its individual fields.
     *
     * @param name
     *            Name of the Pharmacy
     * @param address
     *            Address of the Pharmacy
     * @param zip
     *            ZIP of the Pharmacy
     * @param state
     *            State of the Pharmacy
     */
    public Pharmacy ( final String name, final String address, final String zip, final String state ) {
        setName( name );
        setAddress( address );
        setZip( zip );
        setState( State.parse( state ) );
    }

    /**
     * Retrieve a Pharmacy from the database or in-memory cache by name.
     *
     * @param name
     *            Name of the Pharmacy to retrieve
     * @return The Pharmacy found, or null if none was found.
     */
    public static Pharmacy getByName ( final String name ) {
        try {
            return getWhere( eqList( "name", name ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Retrieve a list of Pharmacies from the database or in-memory cache by
     * zipcode.
     *
     * @param zipcode
     *            zipcde of the Pharmacy to retrieve
     * @return The list of Pharmacies found, or null if none was found.
     */
    public static List<Pharmacy> getByZipcode ( final String zipcode ) {
        try {
            return getWhere( eqList( "zip", zipcode ) );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Retrieve all matching Pharmacys from the database that match a where
     * clause provided.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The matching Pharmacys
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Pharmacy> getWhere ( final List<Criterion> where ) {
        return (List<Pharmacy>) getWhere( Pharmacy.class, where );
    }

    /**
     * Retrieve all Pharmacys from the database
     *
     * @return Pharmacys found
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Pharmacy> getPharmacies () {
        return (List<Pharmacy>) getAll( Pharmacy.class );
    }

    /**
     * Construct an empty Pharmacy record. Used for Hibernate.
     */
    public Pharmacy () {
    }

    /**
     * Name of the Pharmacy
     */
    @NotEmpty
    @Length ( max = 100 )
    @Id
    private String              name;

    /**
     * Address of the Pharmacy
     */
    @NotEmpty
    @Length ( max = 100 )
    private String              address;

    /**
     * State of the Pharmacy
     */
    @Enumerated ( EnumType.STRING )
    private State               state;

    /**
     * ZIP code of the Pharmacy
     */
    @NotEmpty
    @Length ( min = 5, max = 10 )
    private String              zip;

    /**
     * The list of drugs the Pharmacy stocks
     */
    @ManyToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private Set<Drug>           drugs;

    /**
     * The list of Prescriptions assigned to this Pharmacy
     */
    @JoinColumn ( name = "pharmacy_id" )
    @OneToMany ( cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST },
            fetch = FetchType.EAGER )
    private Set<Prescription>   prescriptions;

    /**
     * List of Users that have this pharmacy as their preferred pharmacy
     */
    @OneToMany ( cascade = CascadeType.ALL, mappedBy = "phar", fetch = FetchType.EAGER )
    private transient Set<User> patients;

    /**
     * Retrieves the name of this Pharmacy
     *
     * @return The Name of the Pharmacy
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of this Pharmacy
     *
     * @param name
     *            New Name for the Pharmacy
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the Address of this Pharmacy
     *
     * @return Address of the Pharmacy
     */
    public String getAddress () {
        return address;
    }

    /**
     * Sets the Address of this Pharmacy
     *
     * @param address
     *            New Address of the Pharmacy
     */
    public void setAddress ( final String address ) {
        this.address = address;
    }

    /**
     * Gets the State of this Pharmacy
     *
     * @return The State of the Pharmacy
     */
    public State getState () {
        return state;
    }

    /**
     * Sets the State of this Pharmacy
     *
     * @param state
     *            New State of the Pharmacy
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Gets the ZIP code of this Pharmacy
     *
     * @return The ZIP of the Pharmacy
     */
    public String getZip () {
        return zip;
    }

    /**
     * Sets the ZIP of this Pharmacy
     *
     * @param zip
     *            New ZIP code for the Pharmacy
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Getter for stockedDrugs
     *
     * @return the stockedDrugs
     */
    public Set<Drug> getDrugs () {
        if ( drugs == null ) {
            this.drugs = new HashSet<>();
        }
        return drugs;
    }

    /**
     * Setter for stockedDrugs
     *
     * @param stockedDrugs
     *            the stockedDrugs to set
     */

    public void setDrugs ( final Set<Drug> stockedDrugs ) {
        this.drugs = stockedDrugs;
    }

    public void setDrugs ( final StockedDrugsForm drugsF ) {
        final Set<Drug> newDrugs = new HashSet<>();
        for ( final DrugForm entry : drugsF.getDrugs() ) {
            final Drug drug = Drug.getById( entry.getId() );
            newDrugs.add( drug );
        }
        setDrugs( newDrugs );
    }

    public void addDrug ( final Drug drug ) {
        getDrugs().add( drug );

    }

    /**
     * Getter for prescriptions
     *
     * @return the prescriptions
     */
    public Set<Prescription> getPrescriptions () {
        if ( this.prescriptions == null ) {
            this.prescriptions = new HashSet<>();
        }
        return prescriptions;
    }

    /**
     * This assigns the list of prescriptions to the pharmacy where they will be
     * filled.
     *
     * @param prescriptions
     *            the prescriptions to set
     */
    public void setPrescriptions ( final Set<Prescription> prescriptions ) {
        this.prescriptions = prescriptions;
    }

    /**
     * Adds a prescription to pharmacy list
     *
     * @param prescription
     *            the prescription to add
     */
    public void addPrescriptions ( final Prescription prescription ) {
        getPrescriptions().add( prescription );
    }

    public Set<User> getPatients () {
        if ( this.patients == null ) {
            this.patients = new HashSet<>();
        }
        return patients;
    }

    public void setPatients ( final Set<User> patients ) {
        this.patients = patients;
    }

    /**
     * Retrieves the ID (Name) of this Pharmacy
     */
    @Override
    public String getId () {
        return getName();
    }

    @Override
    public String toString () {
        final String s = this.name + "  " + this.address;
        return s;
    }
}
