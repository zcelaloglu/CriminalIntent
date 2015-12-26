package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by zafer on 3.12.15.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment dialog = new TimePickerFragment().newInstance(mCrime.getDate());
                FragmentManager manager = getFragmentManager();
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);

            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();
                //Old style for implicit intent
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
//                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //This dummy code is disable suspect button to open contact app by using ContentProvider
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect);
                mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri contectUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String selectClause = ContactsContract.CommonDataKinds.Phone._ID + " = ?";

                        String[] fields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                        String[] selectParams = {Long.toString(mCrime.getContactId())};

                        Cursor cursor = getActivity().getContentResolver().query(contectUri, fields, selectClause, selectParams, null);

                        if(cursor != null && cursor.getCount() > 0) {

                            try {
                                cursor.moveToFirst();
                                String number = cursor.getString(0);
                                Uri phoneNumber  = Uri.parse("tel:" + number);
                                Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                                mCallSuspectButton.setText(String.valueOf(phoneNumber));
                                startActivity(intent);
                            }finally {
                                cursor.close();
                            }
                        }
                    }
                });

        if(mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }else {
            mCallSuspectButton.setEnabled(false);
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
            updateTime();
        } else if(requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            //specify which fields you want your query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };
            //Perform your query - the contactUri is like a "where" clause here
            Cursor c = null;

            try {
                c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
                //Double-check that you actually got results
                if(c.getCount() == 0) {
                    return;
                }
                //Pull out the first column of the first row of data that is your suspect's name.
                c.moveToFirst();
                String suspect = c.getString(0);
                long contactId = c.getLong(1);

                mCrime.setSuspect(suspect);
                mCrime.setContactId(contactId);
                mSuspectButton.setText(suspect);

            }finally {
                c.close();
            }
        }
    }
    private void updateDate() {
        mDateButton.setText(mCrime.getFormattedDate());
    }
    private void updateTime() {
        mTimeButton.setText(mCrime.getFormattedTime());
    }

    private String getCrimeReport() {
        String solvedString = mCrime.isSolved() ? getString(R.string.crime_report_solved) : getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MMM dd";
        String dateString = new SimpleDateFormat(dateFormat, Locale.US).format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                if(mCrime != null) {
                    CrimeLab.get(getActivity()).deleteCrime(mCrime);
                    Toast.makeText(getActivity(), mCrime.getTitle() + " deleted Successfully", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}