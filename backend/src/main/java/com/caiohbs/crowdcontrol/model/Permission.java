package com.caiohbs.crowdcontrol.model;

public enum Permission {

    /**
     * Allows user to read their own information.
     */
    READ_SELF,
    /**
     * Allows the user to update their own information.
     */
    UPDATE_SELF,
    /**
     * Allows the user to delete their own profile.
     */
    DELETE_SELF,

    /**
     * Allows user to create new roles.
     */
    CREATE_ROLE_GENERAL,
    /**
     * Allows user to create new users.
     */
    CREATE_USER_GENERAL,
    /**
     * Allows user to access info on all other users.
     */
    READ_GENERAL,
    /**
     * Allows user to update info on all other users.
     */
    UPDATE_GENERAL,
    /**
     * Allows user to delete profiles/roles.
     */
    DELETE_GENERAL

}

