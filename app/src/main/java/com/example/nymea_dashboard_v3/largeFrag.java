package com.example.nymea_dashboard_v3;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nymea_dashboard_v3.databinding.FragmentLargeBinding;

import java.util.UUID;

public class largeFrag extends Fragment {

    private FragmentLargeBinding binding;

    static final String TAG = largeFrag.class.getSimpleName();
    House _house = House.getInstance();

    WebView _camView;
    ConstraintLayout camLayout;
    pwrFragment mPwrFragment;
    static int roomId;
    SharedPreferences prefs;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLargeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle(_house.rooms.get(roomId).mName);
        prefs = getActivity().getSharedPreferences("nymeaDashboard",0);
        ConstraintLayout lrgLayout = view.findViewById(R.id.largeLayout);
        lrgLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(_house.defaultRoom.equals(_house.rooms.get(roomId).mName))
                {
                    _house.defaultRoom ="";
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("defaultroom",_house.defaultRoom);
                    editor.commit();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Room no long default on this device",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    _house.defaultRoom = _house.rooms.get(roomId).mName;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("defaultroom",_house.defaultRoom);
                    editor.commit();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Room set as default for this device",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return false;
            }
        });
        _camView = view.findViewById(R.id.wvCamera);
        camLayout = view.findViewById(R.id.camLayout);
        FragmentManager hFragmentManager = getChildFragmentManager();

        FragmentTransaction mFragmentTransaction = hFragmentManager.beginTransaction();
        for(UUID thingId :_house.rooms.get(roomId).ThingIds){
            if(_house.getThingById(thingId).classDisplayName !=null) {
                switch (_house.getThingById(thingId).classDisplayName.toUpperCase()) {
                    case "GENERIC THING":
                        switch (_house.getThingById(thingId).name.toUpperCase()) {
                            case "DIGITAL CLOCK":
                                digitalClockFragment digitalClockFragment = new digitalClockFragment();

                                mFragmentTransaction.add(R.id.lrgClockFrag, digitalClockFragment);

                                break;

                            case "ANALOG CLOCK":
                                analogClockFragment analogClockFragment = new analogClockFragment();

                                mFragmentTransaction.add(R.id.lrgClockFrag, analogClockFragment);

                                break;

                            case "DAY CALENDAR":
                                calendarFragment mcalendarFragment = new calendarFragment();
                                mFragmentTransaction.add(R.id.lrgCalFragHolder, mcalendarFragment);
                                break;

                        }
                        break;

                    case "WEATHER":
                        weatherFragment weatherFragment = new weatherFragment();

                        mFragmentTransaction.add(R.id.lrgWeatherFrag, weatherFragment);

                        break;

                    case "GENERIC TEMPERATURE SENSOR":
                        if (_house.getThingById(thingId).name.toUpperCase().contains(_house.rooms.get(roomId).mName.toUpperCase())) {
                            roomEnvFragment roomEnvFragment = new roomEnvFragment();
                            roomEnvFragment.roomId = roomId;
                            mFragmentTransaction.add(R.id.lrgRoomEnv, roomEnvFragment);
                        }
                        break;

                    case "WEBVIEW":
                        camLayout.setVisibility(View.VISIBLE);
                        int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                        if (screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                            ViewGroup.LayoutParams layoutParams = camLayout.getLayoutParams();
                            layoutParams.width = 508;
                            layoutParams.height = 408;
                            camLayout.setLayoutParams(layoutParams);
                        }
                        _camView.getSettings().setJavaScriptEnabled(true);
                        _camView.getSettings().setDomStorageEnabled(true);
                        _camView.getSettings().setAllowContentAccess(true);
                        _camView.getSettings().setAppCacheEnabled(true);
                        _camView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                        _camView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                        _camView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
                        _camView.loadUrl(_house.getThingById(thingId).name);
                        break;

                    case "TUYA SWITCH":
                        if (mPwrFragment == null) {
                            mPwrFragment = new pwrFragment();
                            mFragmentTransaction.add(R.id.pwrFragHolder, mPwrFragment);
                            mPwrFragment.things.clear();
                        }

                        mPwrFragment.things.add(thingId);

                        break;
                }
            }
            else
            {
                Log.d(TAG, "onViewCreated: " + _house.getThingById(thingId).name);
            }
        }

        houseEnvFragment houseEnvFragment = new houseEnvFragment();

        mFragmentTransaction.add(R.id.lrgHouseEnvFrag,houseEnvFragment);

        roomTemperatureFragment roomTemperatureFragment = new roomTemperatureFragment();
        roomTemperatureFragment.currRoom = _house.rooms.get(roomId).mName;
        mFragmentTransaction.add(R.id.roomtemperatureHolder,roomTemperatureFragment);

        presenceFragment mPresenceFragment  =new presenceFragment();
        mFragmentTransaction.add(R.id.presencesFragHolder,mPresenceFragment);

        mFragmentTransaction.commit();
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        getActivity().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        for(UUID thingid : _house.rooms.get(roomId).ThingIds)
        {
            _house.getThingById(thingid).listener = null;
        }
        houseFrag.returning =true;
    }

}