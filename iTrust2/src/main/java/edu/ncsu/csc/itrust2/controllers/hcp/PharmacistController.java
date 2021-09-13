package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that enables Prescription Functionality for pharmacists
 *
 * @author ywu44
 *
 */
@Controller
public class PharmacistController {

    /**
     * Returns the page allowing pharmacists to fill prescriptions
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/fillPrescriptionPharmacy" )
    @PreAuthorize ( "hasRole('ROLE_PHARMACIST')" )
    public String fillPrescription () {
        return "/hcp/fillPrescriptionPharmacy";
    }

    /**
     * Returns the page allowing Virologists to record prescription
     *
     * @return The page to display
     */
    @GetMapping ( "/prescription/recordPrescriptions" )
    @PreAuthorize ( "hasRole('ROLE_PHARMACIST')" )
    public String recordPrescription () {
        return "/prescription/recordPrescriptions";
    }

}
