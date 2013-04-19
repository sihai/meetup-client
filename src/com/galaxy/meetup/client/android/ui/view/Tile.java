/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.os.Bundle;

/**
 * 
 * @author sihai
 * 
 */
public interface Tile {

	public static interface ParticipantPresenceListener {

		public abstract void onParticipantPresenceChanged();
	}

	public abstract void onCreate(Bundle bundle);

	public abstract void onPause();

	public abstract void onResume();

	public abstract void onSaveInstanceState(Bundle bundle);

	public abstract void onStart();

	public abstract void onStop();

	public abstract void onTilePause();

	public abstract void onTileResume();

	public abstract void onTileStart();

	public abstract void onTileStop();

	public abstract void setVisibility(int i);
}
