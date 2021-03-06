package edu.ncsu.csc.itrust2.controllers.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.DrugType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.EyeSurgeryType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.LabResultScale;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PrescriptionStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Specialty;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * This class provides GET endpoints for all of the Enums, so that they can be
 * used for creating proper DomainObjects
 *
 * @author Kai Presler-Marshall
 */
@RestController
public class APIEnumController extends APIController {

    /**
     * Gets appointment types
     *
     * @return appointment types
     */
    @GetMapping ( BASE_PATH + "/appointmenttype" )
    public List<AppointmentType> getAppointmentTypes () {
        final User user = User.getByName( LoggerUtil.currentUser() );
        final Role role = user.getRole();
        if ( role.equals( Role.ROLE_HCP ) || role.equals( Role.ROLE_PHARMACIST ) ) {
            return Arrays.asList( AppointmentType.GENERAL_CHECKUP );
        }
        if ( role.equals( Role.ROLE_OD ) ) {
            return Arrays.asList( AppointmentType.GENERAL_CHECKUP, AppointmentType.GENERAL_OPHTHALMOLOGY );
        }
        return Arrays.asList( AppointmentType.values() );
    }

    /**
     * Gets ophthalomogy surgery types
     *
     * @return ophthalomogy surgery types
     */
    @GetMapping ( BASE_PATH + "/ophthalmologysurgerytype" )
    public List<EyeSurgeryType> getOphthalmologySurgeryTypes () {
        return Arrays.asList( EyeSurgeryType.values() );
    }

    /**
     * Gets appointment statuses
     *
     * @return appointment statuses
     */
    @GetMapping ( BASE_PATH + "/appointmentstatus" )
    public List<Status> getAppointmentStatuses () {
        return Arrays.asList( Status.values() );
    }

    /**
     * Get the blood types
     *
     * @return blood types
     */
    @GetMapping ( BASE_PATH + "/bloodtype" )
    public List<Map<String, Object>> getBloodTypes () {
        return Arrays.asList( BloodType.values() ).stream().map( bt -> bt.getInfo() ).collect( Collectors.toList() );
    }

    /**
     * Get ethnicity
     *
     * @return ethnicity
     */
    @GetMapping ( BASE_PATH + "/ethnicity" )
    public List<Map<String, Object>> getEthnicity () {
        return Arrays.asList( Ethnicity.values() ).stream().map( eth -> eth.getInfo() ).collect( Collectors.toList() );
    }

    /**
     * Get genders
     *
     * @return genders
     */
    @GetMapping ( BASE_PATH + "/gender" )
    public List<Map<String, Object>> getGenders () {
        return Arrays.asList( Gender.values() ).stream().map( gen -> gen.getInfo() ).collect( Collectors.toList() );
    }

    /**
     * Get drug types
     *
     * @return drugTypes
     */
    @GetMapping ( BASE_PATH + "/drugtype" )
    public List<Map<String, Object>> getDrugTypes () {
        return Arrays.asList( DrugType.values() ).stream().map( type -> type.getInfo() ).collect( Collectors.toList() );
    }

    /**
     * Get prescription statuses
     *
     * @return drugTypes
     */
    @GetMapping ( BASE_PATH + "/prescriptionStatuses" )
    public List<Map<String, Object>> getPrescriptionStatuses () {
        return Arrays.asList( PrescriptionStatus.values() ).stream().map( type -> type.getInfo() )
                .collect( Collectors.toList() );
    }

    /**
     * Get states
     *
     * @return states
     */
    @GetMapping ( BASE_PATH + "/state" )
    public List<Map<String, Object>> getStates () {
        return Arrays.asList( State.values() ).stream().map( st -> st.getInfo() ).collect( Collectors.toList() );
    }

    /**
     * Get house smoking statuses
     *
     * @return house smoking statuses
     */
    @GetMapping ( BASE_PATH + "/housesmoking" )
    public List<HouseholdSmokingStatus> getHouseSmokingStatuses () {
        final List<HouseholdSmokingStatus> ret = Arrays.asList( HouseholdSmokingStatus.values() ).subList( 1,
                HouseholdSmokingStatus.values().length );
        return ret;
    }

    /**
     * Get patient smoking statuses
     *
     * @return patient smoking statuses
     */
    @GetMapping ( BASE_PATH + "/patientsmoking" )
    public List<PatientSmokingStatus> getPatientSmokingStatuses () {
        final List<PatientSmokingStatus> ret = Arrays.asList( PatientSmokingStatus.values() ).subList( 1,
                PatientSmokingStatus.values().length );
        return ret;
    }

    /**
     * Gets lab result scales
     *
     * @return lab result scale
     */
    @GetMapping ( BASE_PATH + "/labresultscale" )
    public List<LabResultScale> getLabResultScale () {
        return Arrays.asList( LabResultScale.values() );
    }

    /**
     * Get specialty types
     *
     * @return list of specialty types
     */
    @GetMapping ( BASE_PATH + "/specialties" )
    public List<Specialty> getSpecialtyTypes () {
        return Arrays.asList( Specialty.values() );
    }

    /**
     * Get specialty type names
     *
     * @return list of specialty types
     */
    @GetMapping ( BASE_PATH + "/specialtynames" )
    public List<String> getSpecialtyNames () {
        return Specialty.getAllNames();
    }

    /**
     * Gets symptom severity levels
     *
     * @return list of symptom severity levels
     */
    @GetMapping ( BASE_PATH + "/symptomseverities" )
    public List<SymptomSeverity> getSymptomSeverityTypes () {
        return Arrays.asList( SymptomSeverity.values() );
    }

}
