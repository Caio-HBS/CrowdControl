package com.caiohbs.crowdcontrol.model;

public enum Permission {

    /**
     * Allows user to read their own information (includes {@link Payment},
     * {@link SickNote} and {@link UserInfo}).
     */
    READ_SELF,
    /**
     * Allows the user to update their own profile present on the {@link User}
     * class (only base info, such as password and e-mail).
     */
    UPDATE_SELF,

    /**
     * Allows user to create new users ({@link User}).
     */
    CREATE_USER_GENERAL,

    /**
     * Allows user to create new roles ({@link Role}).
     */
    CREATE_ROLE_GENERAL,

    /**
     * Allows the user to create their profile info ({@link UserInfo}).
     */
    CREATE_INFO_SELF,
    /**
     * Allows the user to update their own profile info ({@link UserInfo}).
     */
    UPDATE_INFO_SELF,

    /**
     * Allows the user to create sick notes for their profile ({@link SickNote}).
     */
    CREATE_SICK_NOTE_SELF,
    /**
     * Allows the user to create sick notes other profiles ({@link SickNote}).
     */
    CREATE_SICK_NOTE_GENERAL,
    /**
     * Allows the user to delete sick notes from their profile ({@link SickNote}).
     */
    DELETE_SICK_NOTE_SELF,

    /**
     * Allows the user to create a {@link Payment} for a single user.
     */
    CREATE_PAYMENT_GENERAL,
    /**
     * Allows the user to create auto payments for every user in a given role
     * ({@link Payment}).
     */
    CREATE_PAYMENT_FOR_ROLE,

    /**
     * Allows user to access info on all other users (including {@link UserInfo}
     * and {@link SickNote}).
     */
    READ_GENERAL,
    /**
     * Allows user to update info on all other users (including {@link User},
     * {@link UserInfo} and {@link Role}).
     */
    UPDATE_GENERAL,
    /**
     * Allows user to delete whatever can be deleted on app ({@link User},
     * {@link Payment}, {@link SickNote} and {@link Role}).
     */
    DELETE_GENERAL

}

