package com.example.goldencarrot.data.model.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * utilization of the User interface.
 * This class sets the acceptable values for the user type.
 *
 * Provides common stirngs used by User objects and controllers
 * */
public class UserUtils {
    public static final String USER_TYPE = "USER_TYPE";
    public static final String ORGANIZER_TYPE = "ORGANIZER";
    public static final String PARTICIPANT_TYPE = "PARTICIPANT";
    public static final String ADMIN_TYPE = "ADMIN";

    // waitlist user statuses
    public static final String WAITING_STATUS = "waiting";
    public static final String ACCEPTED_STATUS = "accepted";
    public static final String DECLINED_STATUS = "declined";

    // List of valid user types, immutable for safety
    public static final List<String> validUserTypes = Collections.unmodifiableList(
            Arrays.asList(ORGANIZER_TYPE, PARTICIPANT_TYPE, ADMIN_TYPE)
    );

    public static final Exception invalidUserTypeException =
            new Exception("Invalid User Type");
}
