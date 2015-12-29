package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by zafer on 2.12.15.
 */
public class Crime {

    private String mTitle;
    private UUID mId;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private long mContactId;

    public long getContactId() {
        return mContactId;
    }

    public void setContactId(long contactId) {
        mContactId = contactId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Crime() {
        this(UUID.randomUUID());
    }
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
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

    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getFormattedDate() {
        String format = "EEEE, MMM d, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(mDate);
    }

    public String getFormattedTime() {
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(mDate);
    }
}
