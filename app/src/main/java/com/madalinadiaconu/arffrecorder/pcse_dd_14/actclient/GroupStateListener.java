package com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient;

import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.CoordinatorClient.UserState;

/**
 * A GroupStateListener will be notified of any user activity changes.
 */
public interface GroupStateListener {
	void groupStateChanged(UserState[] groupState);
}
