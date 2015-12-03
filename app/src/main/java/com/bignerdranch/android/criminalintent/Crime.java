package com.bignerdranch.android.criminalintent;

import java.util.UUID;

/**
 * Created by zafer on 2.12.15.
 */
public class Crime {

    private String mTitle;
    private UUID mId;

    public Crime() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
