
package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.enums.DrugType;
import edu.ncsu.csc.itrust2.models.persistent.Drug;

/**
 * A form for REST API communication. Contains fields for constructing Drug
 * objects.
 *
 * @author Connor
 */
public class DrugForm {

    private Long   id;
    private String name;
    private String genericName;
    private String code;
    private String description;
    /** Type of drug, (e.g. brand or generic) **/
    private String type;

    /**
     * Empty constructor for filling in fields without a Drug object.
     */
    public DrugForm () {
        setType( DrugType.NotSpecified.toString() );
    }

    /**
     * Constructs a new form with information from the given drug.
     *
     * @param drug
     *            the drug object
     */
    public DrugForm ( final Drug drug ) {
        setId( drug.getId() );
        setName( drug.getName() );
        setGenericName( drug.getGenericName() );
        setCode( drug.getCode() );
        setDescription( drug.getDescription() );
        if ( null != drug.getType() ) {
            setType( drug.getType().toString() );
        }
    }

    /**
     * Sets the drug's id to the given value. All saved drugs must have unique
     * ids.
     *
     * @return the drug id
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the drug's NDC
     *
     * @return the NDC
     */
    public String getCode () {
        return code;
    }

    /**
     * The name of the drug.
     *
     * @return the drug's name
     */
    public String getName () {
        return name;
    }

    /**
     * Getter for genericName
     *
     * @return the genericName
     */
    public String getGenericName () {
        return genericName;
    }

    /**
     * Setter for genericName
     *
     * @param genericName
     *            the genericName to set
     */
    public void setGenericName ( final String genericName ) {
        this.genericName = genericName;
    }

    /**
     * Gets this drug's description.
     *
     * @return this description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the id associated with this drug.
     *
     * @param id
     *            the drug's id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Sets the NDC to the given string. Must be in the format "####-####-##".
     *
     * @param code
     *            the NDC
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Sets the drug name.
     *
     * @param name
     *            the name of the drug
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Sets this drug's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

    /**
     * Gets this drug's type.
     *
     * @return the type
     */
    public String getType () {
        return type;
    }

    /**
     * Sets this drug's type to the given value.
     *
     * @param type
     *            the type to set
     */
    public void setType ( final String type ) {
        this.type = type;
    }
}
