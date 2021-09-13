package edu.ncsu.csc.itrust2.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum PrescriptionStatus {

    /**
     * The status of a prescription that's been created, but otherwise
     * unassigned
     */
    CREATED ( "Created" ),

    /**
     * The status of a prescription that has been sent to a pharmacy to be
     * filled
     */
    SENT_TO_PHARMACY ( "Sent" ),

    /**
     * The status when a prescription has been successfully filled
     */
    FILLED ( "Filled" ),

    /**
     * The status when a prescription cannot be filled by a pharmacy
     */
    CANCELLED ( "Cancelled" );

    /**
     * The name of the status
     */
    private String name;

    /**
     * Constructor for Prescription
     *
     * @param name
     *            Name of the status
     */
    private PrescriptionStatus ( String name ) {
        this.name = name;
    }

    /**
     * Returns the name of the status
     *
     * @return the name of the status
     */
    public String getName () {
        return name;
    }

    public Map<String, Object> getInfo () {
        final Map<String, Object> map = new HashMap<>();
        map.put( "id", name() );
        map.put( "name", getName() );
        return map;
    }

    @Override
    public String toString () {
        return getName();
    }

    public static PrescriptionStatus parse ( final String statusStr ) {
        for ( final PrescriptionStatus status : values() ) {
            if ( status.getName().equals( statusStr ) ) {
                return status;
            }
        }
        return CREATED;
    }

}
