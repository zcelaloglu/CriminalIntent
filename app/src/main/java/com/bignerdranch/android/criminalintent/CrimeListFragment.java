package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zafer on 7.12.15.
 */
public class CrimeListFragment extends Fragment {

    private static final String ADAPTER_POSITION = "adapter_position";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    private int mLastAdapterClickPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        if (savedInstanceState != null) {
            savedInstanceState.getInt(ADAPTER_POSITION,0);
        }
        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ADAPTER_POSITION,mLastAdapterClickPosition);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        }else {
            if(mLastAdapterClickPosition < 0) {
                mCrimeAdapter.notifyDataSetChanged();
            } else {
                mCrimeAdapter.notifyItemChanged(mLastAdapterClickPosition);
                mLastAdapterClickPosition = -1;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnResume","OnResume called!");
        updateUI();
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);

        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mCrimeCheckBox;

        private Crime mCrime;

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mCrimeCheckBox.setChecked(mCrime.isSolved());
        }

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_crime_date_text_view);
            mCrimeCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        @Override
        public void onClick(View v) {
            mLastAdapterClickPosition = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getContext(),mCrime.getId());
            startActivity(intent);
        }
    }
}
