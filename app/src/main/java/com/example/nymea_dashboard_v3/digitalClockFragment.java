package com.example.nymea_dashboard_v3;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DigitalClock;
import android.widget.TextView;


public class digitalClockFragment extends Fragment {

    public digitalClockFragment() {
        // Required empty public constructor
    }

  TextView tvDateD;
    DigitalClock dClock;
    public static digitalClockFragment newInstance(String param1, String param2) {
        digitalClockFragment fragment = new digitalClockFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_clock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDateD = view.findViewById(R.id.tvDateD);
        dClock =view.findViewById(R.id.digitalClock);
        int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

        }
        else
        {
            tvDateD.setTextSize(20);
            dClock.setTextSize(40);
        }
    }
}