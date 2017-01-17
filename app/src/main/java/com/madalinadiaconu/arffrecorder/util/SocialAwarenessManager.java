package com.madalinadiaconu.arffrecorder.util;

import android.os.Handler;

import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.ClassLabel;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.CoordinatorClient;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.RoomState;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Madalina Diaconu on 17.01.17.
 * Utility class meant to determine the role of a user and the room state
 */

public class SocialAwarenessManager {

    private static SocialAwarenessManager instance;
    private Map<String, List<ClassLabel>> userStatesToBeProcessed;
    private Map<String, UserRole> computedUserRoles;
    private CoordinatorClient.UserState[] lastUserStates;
    private RoomState roomState;

    private static final long NOTIFY_INTERVAL = 6000;
    private Handler mHandler = new Handler();

    private CoordinatorClient coordinatorClient;

    public static SocialAwarenessManager getInstance() {
        if (instance == null) {
            instance = new SocialAwarenessManager();
        }
        return instance;
    }

    public void setCoordinatorClient(CoordinatorClient coordinatorClient) {
        this.coordinatorClient = coordinatorClient;
    }

    private SocialAwarenessManager() {
        userStatesToBeProcessed = new HashMap<>();
        computedUserRoles = new HashMap<>();
        roomState = RoomState.transition;
        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        computeUserRoles();
                        sendUserRoles();
                        computeRoomState();
                        sendRoomState();
                    }
                });
            }
        }, 0, NOTIFY_INTERVAL);
    }

    private void sendUserRoles() {
        for (CoordinatorClient.UserState currentUserState : lastUserStates) {
            UserRole userRole = computedUserRoles.get(currentUserState.getUserId());
            if (userRole != null) {
                currentUserState.setRole(userRole);
            } else {
                currentUserState.setRole(UserRole.transition);
            }
        }
    }

    private void computeUserRoles() {
        for (String userId : userStatesToBeProcessed.keySet()) {
            List<ClassLabel> classLabels = userStatesToBeProcessed.get(userId);
            int occurrencesSitting = Collections.frequency(classLabels, ClassLabel.sitting);
            int occurrencesStanding = Collections.frequency(classLabels, ClassLabel.standing);
            int occurrencesWalking = Collections.frequency(classLabels, ClassLabel.walking);
            if (occurrencesSitting > 0.75 * classLabels.size()) {
                computedUserRoles.put(userId, UserRole.listener);
            } else if (occurrencesWalking + occurrencesStanding > 0.8 * classLabels.size()) {
                computedUserRoles.put(userId, UserRole.speaker);
            } else {
                computedUserRoles.put(userId, UserRole.transition);
            }
        }
    }

    private void computeRoomState() {
        if (computedUserRoles.values().size() == 0) {
            roomState = RoomState.empty;
        } else {
            int occurrencesListener = Collections.frequency(computedUserRoles.values(), UserRole.listener);
            int occurrencesSpeakers = Collections.frequency(computedUserRoles.values(), UserRole.speaker);
            if (occurrencesListener == computedUserRoles.keySet().size() - 1 &&
                    occurrencesSpeakers == 1) {
                roomState = RoomState.lecture;
            } else {
                roomState = RoomState.transition;
            }
        }
    }

    private void sendRoomState() {
        if (coordinatorClient != null) {
            coordinatorClient.setRoomState(roomState);
        }
    }

    public void updateUserStates(CoordinatorClient.UserState[] lastUserStates) {
        this.lastUserStates = lastUserStates;
        for (CoordinatorClient.UserState userState : lastUserStates) {
            if (userState.getUpdateAge() < 10000) {
                List<ClassLabel> userClassLabels = userStatesToBeProcessed.get(userState.getUserId());
                if (userClassLabels == null) {
                    userClassLabels = new ArrayList<>();
                }
                userClassLabels.add(userState.getActivity());
                userStatesToBeProcessed.put(userState.getUserId(), userClassLabels);
            } else {
                userStatesToBeProcessed.remove(userState.getUserId());
            }
        }
    }
}
