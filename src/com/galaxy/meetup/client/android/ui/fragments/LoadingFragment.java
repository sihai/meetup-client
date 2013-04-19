/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class LoadingFragment extends Fragment {

	public LoadingFragment()
    {
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        return layoutinflater.inflate(R.layout.loading_message, viewgroup, false);
    }
}
