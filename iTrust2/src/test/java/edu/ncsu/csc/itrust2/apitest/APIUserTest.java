package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Pharmacy;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with Users
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIUserTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests getting a non existent user and ensures that the correct status is
     * returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentUser () throws Exception {
        mvc.perform( get( "/api/v1/users/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests UserAPI
     *
     * @throws Exception
     */
    @Test
    public void testUserAPI () throws Exception {

        try {
            final User u = User.getByName( "sven_forkbeard" );
            if ( null != u ) {
                u.delete();
            }

        }
        catch ( final Exception e ) {
            //
        }

        final UserForm sven = new UserForm( "sven_forkbeard", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/users/sven_forkbeard" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        sven.setEnabled( null );

        mvc.perform( put( "/api/v1/users/sven_forkbeard" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Put should not work if the id in the url doesn't match the name
        mvc.perform( put( "/api/v1/users/sven_badname" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isConflict() );

        // Should be not found if the name matches, but does not exist
        sven.setUsername( "sven_badname" );
        mvc.perform( put( "/api/v1/users/sven_badname" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isNotFound() );

    }

    @Test
    public void testPharmacist () throws Exception {

        try {
            final User u = User.getByName( "sven_forkbeard" );
            final Pharmacy phar = Pharmacy.getByName( "p1" );
            if ( null != u ) {
                u.delete();
            }
            if ( null != phar ) {
                phar.delete();
            }

        }
        catch ( final Exception e ) {
            //
        }
        final Pharmacy p1 = new Pharmacy();
        p1.setName( "p1" );
        final UserForm sven = new UserForm( "sven_forkbeard", "123456", Role.ROLE_PHARMACIST, 1, p1.getId() );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/users/sven_forkbeard" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        sven.setEnabled( null );

        mvc.perform( put( "/api/v1/users/sven_forkbeard" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Put should not work if the id in the url doesn't match the name
        mvc.perform( put( "/api/v1/users/sven_badname" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isConflict() );

        // Should be not found if the name matches, but does not exist
        sven.setUsername( "sven_badname" );
        mvc.perform( put( "/api/v1/users/sven_badname" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sven ) ) ).andExpect( status().isNotFound() );

    }

    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testAdminRole () throws Exception {
        final String content = mvc.perform( get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        assertTrue( content.contains( "ROLE_ADMIN" ) );

    }

    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "NIL" } )
    public void testNoRole () throws Exception {
        final String content = mvc.perform( get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        assertTrue( content.contains( "UNAUTHORIZED" ) );

    }

    @Test
    @WithMockUser ( username = "pharmacist", roles = { "USER", "PHARMACIST" } )
    public void testPharmacistRole () throws Exception {
        final String content = mvc.perform( get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        assertTrue( content.contains( "ROLE_PHARMACIST" ) );

    }

}
