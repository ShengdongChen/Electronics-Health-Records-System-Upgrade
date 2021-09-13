package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.PharmacyForm;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;

/**
 * Tests for the Pharmacy Form class
 *
 * @author Eli Newman
 *
 */
public class PharmacyFormTest {

    /**
     * Tests the whole class
     */
    @Test
    public void testPharmacyForm () {
        final Pharmacy pharmacy = new Pharmacy();
        pharmacy.setAddress( "somewhere" );
        pharmacy.setName( "Pharmacy" );
        pharmacy.setState( State.NC );
        pharmacy.setZip( "27040" );
        final PharmacyForm form = new PharmacyForm( pharmacy );
        assertEquals( pharmacy.getAddress(), form.getAddress() );
        assertEquals( pharmacy.getName(), form.getName() );
        assertEquals( pharmacy.getState().getName(), form.getState() );
        assertEquals( pharmacy.getZip(), form.getZip() );
    }

    /**
     * Tests the search by zipcode functionality
     */
    @Test
    public void testSearchByZipcode () {
        final Pharmacy test1 = new Pharmacy( "generic pharmacy", "123 main street", "27608", "AL" );
        test1.save();
        final Pharmacy test2 = new Pharmacy( "brand pharmacy", "123 main street", "27608", "AL" );
        test2.save();
        final Pharmacy test3 = new Pharmacy( "nine digit zip", "123 main street", "27616-1234", "AL" );
        test3.save();
        final Pharmacy test4 = new Pharmacy( "list with only one entry", "123 main street", "10101", "AL" );
        test4.save();

        final List<Pharmacy> list1 = Pharmacy.getByZipcode( "27608" );
        assertEquals( list1.size(), 2 );
        assertEquals( list1.get( 0 ).getName(), "brand pharmacy" );
        assertEquals( list1.get( 1 ).getName(), "generic pharmacy" );

        final List<Pharmacy> list2 = Pharmacy.getByZipcode( "27616-1234" );
        assertEquals( list2.size(), 1 );
        assertEquals( list2.get( 0 ).getName(), "nine digit zip" );

        final List<Pharmacy> list3 = Pharmacy.getByZipcode( "10101" );
        assertEquals( list3.size(), 1 );
        assertEquals( list3.get( 0 ).getName(), "list with only one entry" );

        assertTrue( Pharmacy.getByZipcode( "not a zipcode" ).isEmpty() );
    }

}
