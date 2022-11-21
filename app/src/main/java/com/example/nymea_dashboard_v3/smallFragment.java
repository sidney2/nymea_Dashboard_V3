package com.example.nymea_dashboard_v3;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.UUID;


public class smallFragment extends Fragment {

    House _house = House.getInstance();

    WebView _camView;
    ConstraintLayout camLayout;
    pwrFragment mPwrFragment;
    static int roomId;
    SharedPreferences prefs;

    public smallFragment() {
        // Required empty public constructor
    }


    public static smallFragment newInstance(String param1, String param2) {
        smallFragment fragment = new smallFragment();

        return fragment;
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        getActivity().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_small, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(_house.rooms.get(roomId).mName);
        prefs = getActivity().getSharedPreferences("nymeaDashboard",0);

        ConstraintLayout smLayout = view.findViewById(R.id.smLayout);
        smLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(_house.defaultRoom.equals(_house.rooms.get(roomId).mName))
                {
                    _house.defaultRoom = "";
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

        _camView = view.findViewById(R.id.smWVCam);
        camLayout = view.findViewById(R.id.smCamLayout);
        FragmentManager hFragmentManager = getChildFragmentManager();

        FragmentTransaction mFragmentTransaction = hFragmentManager.beginTransaction();
        for(UUID thingId :_house.rooms.get(roomId).ThingIds){
            switch(_house.getThingById(thingId).classDisplayName.toUpperCase()){
                case "GENERIC THING":
                    switch(_house.getThingById(thingId).name.toUpperCase()){
                        case "DIGITAL CLOCK":
                            digitalClockFragment digitalClockFragment = new digitalClockFragment();

                            mFragmentTransaction.add(R.id.smClockFragHolder,digitalClockFragment);

                            break;

                        case "ANALOG CLOCK":
                            analogClockFragment analogClockFragment = new analogClockFragment();

                            mFragmentTransaction.add(R.id.smClockFragHolder,analogClockFragment);

                            break;

                        case "DAY CALENDAR":
                            calendarFragment mcalendarFragment = new calendarFragment();
                            mFragmentTransaction.add(R.id.smCalFragHolder,mcalendarFragment);
                            break;

                    }
                    break;

                case "WEATHER":
                    weatherFragment weatherFragment = new weatherFragment();

                    mFragmentTransaction.add(R.id.smWeatherFragHolder,weatherFragment);

                    break;

                case "GENERIC TEMPERATURE SENSOR":
                    if(_house.getThingById(thingId).name.toUpperCase().contains(_house.rooms.get(roomId).mName.toUpperCase())) {
                        roomEnvFragment roomEnvFragment = new roomEnvFragment();
                        roomEnvFragment.roomId = roomId;
                        mFragmentTransaction.add(R.id.smRoomEnvFragHolder, roomEnvFragment);
                    }
                    break;

                case "WEBVIEW":
                    camLayout.setVisibility(View.VISIBLE);
                    int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                    if(screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE){
                        ViewGroup.LayoutParams layoutParams = camLayout.getLayoutParams();
                        layoutParams.width =508;
                        layoutParams.height =408;
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
                    if(mPwrFragment ==null)
                    {
                        mPwrFragment = new pwrFragment();
                        mFragmentTransaction.add(R.id.smPwrFragHolder,mPwrFragment);
                        mPwrFragment.things.clear();
                    }

                    mPwrFragment.things.add(thingId);

                    break;
            }
        }

        houseEnvFragment houseEnvFragment = new houseEnvFragment();

        mFragmentTransaction.add(R.id.smHouseEnvFragHolder,houseEnvFragment);

        roomTemperatureFragment roomTemperatureFragment = new roomTemperatureFragment();
        roomTemperatureFragment.currRoom = _house.rooms.get(roomId).mName;
        mFragmentTransaction.add(R.id.smRoomTempFragHolder,roomTemperatureFragment);

        presenceFragment mPresenceFragment  =new presenceFragment();
        mFragmentTransaction.add(R.id.smPresenceFragHolder,mPresenceFragment);

        mFragmentTransaction.commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(UUID thingid : _house.rooms.get(roomId).ThingIds)
        {
            _house.getThingById(thingid).listener = null;
        }
        houseFrag.returning =true;
    }
}