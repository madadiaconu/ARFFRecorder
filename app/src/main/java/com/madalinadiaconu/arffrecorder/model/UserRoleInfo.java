package com.madalinadiaconu.arffrecorder.model;

/**
 * Created by Diaconu Madalina on 21.01.17.
 * Model class used to hold the number of users that hold each role
 */

public class UserRoleInfo {

    private int listeners;
    private int speakers;
    private int transitionUsers;

    public UserRoleInfo(int listeners, int speakers, int transitionUsers) {
        this.listeners = listeners;
        this.speakers = speakers;
        this.transitionUsers = transitionUsers;
    }

    public int getListeners() {
        return listeners;
    }

    public int getSpeakers() {
        return speakers;
    }

    public int getTransitionUsers() {
        return transitionUsers;
    }
}
