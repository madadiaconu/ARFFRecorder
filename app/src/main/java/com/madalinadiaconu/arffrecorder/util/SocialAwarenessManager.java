package com.madalinadiaconu.arffrecorder.util;

import android.os.Handler;

import com.madalinadiaconu.arffrecorder.model.UserRoleInfo;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.ClassLabel;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.CoordinatorClient;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.RoomState;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;

import static com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.UserRole.*;

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
        userStatesToBeProcessed = new ConcurrentHashMap<>();
        computedUserRoles = new ConcurrentHashMap<>();
        roomState = RoomState.transition;
        lastUserStates = new CoordinatorClient.UserState[0];
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

    /**
     * Assumptions:
     * - a user is a listener when he's 75% of the time sitting
     * - a user is a speaker when he's walking and standing 80% of the time
     */
    private void computeUserRoles() {
        for (String userId : userStatesToBeProcessed.keySet()) {
            List<ClassLabel> classLabels = userStatesToBeProcessed.get(userId);
            int occurrencesSitting = Collections.frequency(classLabels, ClassLabel.sitting);
            int occurrencesStanding = Collections.frequency(classLabels, ClassLabel.standing);
            int occurrencesWalking = Collections.frequency(classLabels, ClassLabel.walking);
            if (occurrencesSitting > 0.75 * classLabels.size()) {
                computedUserRoles.put(userId, listener);
            } else if (occurrencesWalking + occurrencesStanding > 0.8 * classLabels.size()) {
                computedUserRoles.put(userId, UserRole.speaker);
            } else {
                computedUserRoles.put(userId, UserRole.transition);
            }
            userStatesToBeProcessed.get(userId).clear(); //erase previous states after the role has been computed
        }
    }

    private void sendUserRoles() {
        int listeners = 0, speakers = 0, transitionUsers = 0;
        for (CoordinatorClient.UserState currentUserState : lastUserStates) {
            UserRole userRole = computedUserRoles.get(currentUserState.getUserId());
            if (userRole != null) {
                currentUserState.setRole(userRole);
                switch (userRole) {
                    case listener:
                        listeners++;
                        break;
                    case speaker:
                        speakers++;
                        break;
                    case transition:
                        transitionUsers++;
                        break;
                }
            } else {
                currentUserState.setRole(null);
            }
        }
        EventBus.getDefault().post(new UserRoleInfo(listeners, speakers, transitionUsers));
    }

    private void computeRoomState() {
        //room is empty if no one else is in the room, current user excluded
        if (computedUserRoles.values().size() == 1 && computedUserRoles.containsKey("1627905")) {
            roomState = RoomState.empty;
        } else {
            int occurrencesListener = Collections.frequency(computedUserRoles.values(), listener);
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
            EventBus.getDefault().post(roomState);
        }
    }

    /**
     * Updates the states to be processed for each users and removes the users with an age greater than 10sec
     * Called each time an update is available
     * @param lastUserStates update array for each of the users
     */
    public void updateUserStates(CoordinatorClient.UserState[] lastUserStates) {
        this.lastUserStates = lastUserStates;
        for (CoordinatorClient.UserState userState : this.lastUserStates) {
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
