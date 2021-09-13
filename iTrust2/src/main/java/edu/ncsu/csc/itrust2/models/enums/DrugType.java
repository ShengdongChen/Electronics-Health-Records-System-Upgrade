package edu.ncsu.csc.itrust2.models.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing possible types of drugs,
 * (e.g. brand name or generic)
 *
 * @author krrhodes
 *
 */
public enum DrugType {

    /**
     * Generic
     */
    Generic ( "Generic" ),
    /**
     * Brand Name
     */
    BrandName ( "Brand Name" ),
    /**
     * Not Specified
     */
    NotSpecified ( "Not Specified" );

    /**
     * Name of the DrugType
     */
    private String name;

    /**
     * Constructor for DrugType.
     *
     * @param name
     *            Name of the DrugType to create
     */
    private DrugType ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieve the Name of the DrugType
     *
     * @return Name of the DrugType
     */
    public String getName () {
        return this.name;
    }

    /**
     * Returns a map from field name to value, which is more easily serialized
     * for sending to front-end.
     *
     * @return map from field name to value for each of the fields in this enum
     */
    public Map<String, Object> getInfo () {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "id", name() );
        map.put( "name", getName() );
        return map;
    }

    /**
     * Convert the DrugType to a String
     */
    @Override
    public String toString () {
        return getName();
    }

    /**
     * Find the matching DrugType for the string provided.
     *
     * @param drugTypeStr
     *            DrugType String to find an DrugType Enum for
     * @return The DrugType parsed or NotSpecified if not found
     */
    public static DrugType parse ( final String drugTypeStr ) {
        for ( final DrugType drugType : values() ) {
            if ( drugType.getName().equals( drugTypeStr ) ) {
                return drugType;
            }
        }
        return NotSpecified;
    }

}
