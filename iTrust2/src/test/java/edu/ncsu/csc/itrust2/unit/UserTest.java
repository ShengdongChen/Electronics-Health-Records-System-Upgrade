package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Unit tests for the User class.
 *
 * @author jshore
 *
 */
public class UserTest {

    /**
     * Tests equals comparison of two user objects. Also verifies getters and
     * setters of the used properties.
     */
    @Test
    public void testEqualsAndProperties () {
        final User u1 = new User();
        final User u2 = new User();

        assertFalse( u1.equals( new Object() ) );
        assertFalse( u1.equals( null ) );
        assertTrue( u1.equals( u1 ) );

        u1.setEnabled( 1 );
        assertTrue( 1 == u1.getEnabled() );
        u2.setEnabled( 1 );

        u1.setPassword( "abcdefg" );
        assertEquals( "abcdefg", u1.getPassword() );
        u2.setPassword( "abcdefg" );

        u1.setRole( Role.valueOf( "ROLE_PATIENT" ) );
        assertEquals( Role.valueOf( "ROLE_PATIENT" ), u1.getRole() );
        u2.setRole( Role.valueOf( "ROLE_PATIENT" ) );

        u1.setUsername( "abcdefg" );
        assertEquals( "abcdefg", u1.getUsername() );
        u2.setUsername( "abcdefg" );

        assertTrue( u1.equals( u2 ) );
    }

    /**
     * Test creation of pharmacist
     */
    @Test
    public void testPharmacist () {
        final Pharmacy p1 = new Pharmacy();

        p1.setName( "p1" );
        p1.setAddress( "asdasdas" );
        p1.setState( State.AK );
        p1.setZip( "12345" );
        p1.save();
        User u1 = null;
        try {
            u1 = new User( "u1", "123456", Role.ROLE_PHARMACIST, 1 );
            fail();
        }
        catch ( final Exception e ) {
            assertTrue( u1 == null );
        }
        u1 = new User( "u1", "123456", Role.ROLE_PHARMACIST, 1, p1 );
        u1.save();
        assertTrue( User.getByName( "u1" ).equals( u1 ) );
        assertTrue( User.getByName( "u1" ).equals( u1 ) );
        assertEquals( u1.getPharmacy(), p1 );
        u1.delete();
        p1.delete();

    }

}
