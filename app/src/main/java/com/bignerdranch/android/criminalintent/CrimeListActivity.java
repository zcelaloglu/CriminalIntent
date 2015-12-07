package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by zafer on 7.12.15.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
