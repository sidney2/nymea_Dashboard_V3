package com.example.nymea_dashboard_v3;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.spec.ECField;
import java.util.UUID;


public class roomEnvFragment extends Fragment {

   static int roomId;
   static House _house =House.getInstance();
   static final String TAG = roomEnvFragment.class.getSimpleName();
   ImageView imRoomTemp;
   ImageView imRoomCO;
   ImageView imRoomTVOC;
   TextView tvRoomTemp;
   TextView tvRoomHumidity;
   TextView tvRoomCo;
   TextView tvRoomTVOC;

    public roomEnvFragment() {
        // Required empty public constructor
    }


    public static roomEnvFragment newInstance(String param1, String param2) {
        roomEnvFragment fragment = new roomEnvFragment();

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
        return inflater.inflate(R.layout.fragment_room_env, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imRoomTemp = view.findViewById(R.id.imRoomTemp);
        imRoomCO = view.findViewById(R.id.imRoomC0);
        imRoomTVOC = view.findViewById(R.id.imRoomTVOC);
        tvRoomTemp = view.findViewById(R.id.tvRoomTemp);
        tvRoomHumidity = view.findViewById(R.id.tvRoomHumididty);
        tvRoomCo = view.findViewById(R.id.tvRoomCO);
        tvRoomTVOC = view.findViewById(R.id.tvRoomTVOC);

        int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            tvRoomTemp.setTextSize(20);
            tvRoomHumidity.setTextSize(20);
            tvRoomCo.setTextSize(20);
            tvRoomTVOC.setTextSize(20);
        }
        else
        {
            ViewGroup.LayoutParams rtLayoutParams = tvRoomTemp.getLayoutParams();
            rtLayoutParams.width=65;
            tvRoomTemp.setLayoutParams(rtLayoutParams);

            ViewGroup.LayoutParams rhLaypoutParams = tvRoomHumidity.getLayoutParams();
            rhLaypoutParams.width=65;
            tvRoomHumidity.setLayoutParams(rhLaypoutParams);

            ViewGroup.LayoutParams rcLayoutParams = tvRoomCo.getLayoutParams();
            rcLayoutParams.width=85;
            tvRoomCo.setLayoutParams(rcLayoutParams);

            ViewGroup.LayoutParams rvLayoutParams = tvRoomTVOC.getLayoutParams();
            rvLayoutParams.width=65;
            tvRoomTVOC.setLayoutParams(rvLayoutParams);
        }

        for (UUID thingId : _house.rooms.get(roomId).ThingIds) {

            if (_house.getThingById(thingId).name.toUpperCase().contains(_house.rooms.get(roomId).mName.toUpperCase())) {
                try {
                    if (_house.getThingById(thingId).classDisplayName.toUpperCase().contains("TEMPERATURE")) {

                        String _temperature = _house.getThingById(thingId).stateByName("temperature").value;
                        tvRoomTemp.setText(_temperature + " °C");
                        float iTemperature = Float.parseFloat(_temperature);
                        if (iTemperature <= 18.9) {
                            imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                            imRoomTemp.clearAnimation();
                        } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                            imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                            imRoomTemp.clearAnimation();
                        } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                            imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                            imRoomTemp.clearAnimation();
                        } else if (iTemperature >= 26) {
                            imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                            Animation animation = new AlphaAnimation((float) 1, 0);
                            animation.setDuration(800);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(Animation.REVERSE);
                            imRoomTemp.startAnimation(animation);
                        }
                        _house.getThingById(thingId).setOnStateChangeListener(new Thing.stateChangeListener() {
                            @Override
                            public void onStateValueChange(State state) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                if (state.name.toUpperCase().equals("TEMPERATURE")) {
                                    String _temperature = state.value;
                                    tvRoomTemp.setText(_temperature + " °C");
                                    float iTemperature = Float.parseFloat(_temperature);
                                    if (iTemperature <= 18.9) {
                                        imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                        imRoomTemp.clearAnimation();
                                    } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                                        imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                                        imRoomTemp.clearAnimation();
                                    } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                                        imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                                        imRoomTemp.clearAnimation();
                                    } else if (iTemperature >= 26) {
                                        imRoomTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                        Animation animation = new AlphaAnimation((float) 1, 0);
                                        animation.setDuration(800);
                                        animation.setInterpolator(new LinearInterpolator());
                                        animation.setRepeatCount(Animation.INFINITE);
                                        animation.setRepeatMode(Animation.REVERSE);
                                        imRoomTemp.startAnimation(animation);
                                    }
                                }
                                    }
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG + "/temp", e.getMessage());
                }
                try {
                    if (_house.getThingById(thingId).classDisplayName.toUpperCase().contains("HUMIDITY")) {
                        tvRoomHumidity.setText(_house.getThingById(thingId).stateByName("humidity").value + " %");
                        _house.getThingById(thingId).setOnStateChangeListener(new Thing.stateChangeListener() {
                            @Override
                            public void onStateValueChange(State state) {
                                if (state.name.toUpperCase().equals("HUMIDITY")) {
                                    tvRoomHumidity.setText(state.value + " %");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG + "/humidity", e.getMessage());
                }
                try {
                    if (_house.getThingById(thingId).classDisplayName.toUpperCase().contains("CO2")) {
                        imRoomCO.setVisibility(View.VISIBLE);
                        tvRoomCo.setVisibility(View.VISIBLE);
                        tvRoomCo.setText(_house.getThingById(thingId).stateByName("co2").value + " ppm");
                        _house.getThingById(thingId).setOnStateChangeListener(new Thing.stateChangeListener() {
                            @Override
                            public void onStateValueChange(State state) {
                                if (state.name.toUpperCase().contains("CO2")) {
                                    tvRoomCo.setText(state.value + " ppm");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG + "co2", e.getMessage());
                }
                try {
                    if (_house.getThingById(thingId).classDisplayName.toUpperCase().contains("CO")) {
                        imRoomTVOC.setVisibility(View.VISIBLE);
                        tvRoomTVOC.setVisibility(View.VISIBLE);
                        int tvoc = Integer.parseInt(_house.getThingById(thingId).stateByName("co").value);
                        if (tvoc <= 300) {
                            tvRoomTVOC.setText("Good");
                            imRoomTVOC.clearAnimation();
                        }
                        if ((tvoc >= 301) && (tvoc <= 500)) {
                            tvRoomTVOC.setText("Ok");
                            imRoomTVOC.clearAnimation();
                        }
                        if ((tvoc >= 501) && (tvoc <= 1000)) {
                            tvRoomTVOC.setText("Marginal");
                            Animation animation = new AlphaAnimation((float) 1, 0);
                            animation.setDuration(800);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(Animation.REVERSE);
                            imRoomTVOC.startAnimation(animation);
                        }
                        if (tvoc >= 1001) {
                            tvRoomTVOC.setText("Bad");
                            Animation animation = new AlphaAnimation((float) 1, 0);
                            animation.setDuration(800);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(Animation.REVERSE);
                            imRoomTVOC.startAnimation(animation);
                        }

                        _house.getThingById(thingId).setOnStateChangeListener(new Thing.stateChangeListener() {
                            @Override
                            public void onStateValueChange(State state) {
                                try {
                                    if (state.name.toUpperCase().contains("CO")) {
                                        int tvoc = Integer.parseInt(state.value);
                                        if (tvoc <= 300) {
                                            tvRoomTVOC.setText("Good");
                                            imRoomTVOC.clearAnimation();
                                        }
                                        if ((tvoc >= 301) && (tvoc <= 500)) {
                                            tvRoomTVOC.setText("Ok");
                                            imRoomTVOC.clearAnimation();
                                        }
                                        if ((tvoc >= 501) && (tvoc <= 1000)) {
                                            tvRoomTVOC.setText("Marginal");
                                            Animation animation = new AlphaAnimation((float) 1, 0);
                                            animation.setDuration(800);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            imRoomTVOC.startAnimation(animation);
                                        }
                                        if (tvoc >= 1001) {
                                            tvRoomTVOC.setText("Bad");
                                            Animation animation = new AlphaAnimation((float) 1, 0);
                                            animation.setDuration(800);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            imRoomTVOC.startAnimation(animation);
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG+"/tvocstate",e.getMessage());
                                }
                            }

                        });


                    }
                } catch (Exception e) {
                    Log.e(TAG + "/tvoc", e.getMessage());
                }
                try {


                    if (_house.getThingById(thingId).classDisplayName.toUpperCase().contains("GENERIC LIGHT SENSOR")) {
                        try {
                            _house.getThingById(thingId).setOnStateChangeListener(new Thing.stateChangeListener() {
                                @Override
                                public void onStateValueChange(State state) {
                                    String sCurrentLux = state.getValue();
                                    try {
                                        final float currentLux = (float) ((0.834 * Float.parseFloat(sCurrentLux)) / 100);


                                        int brightnessMode = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                                        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                                            Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                        }
                                        int currentBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);


                                        String cleanLux = sCurrentLux.substring(0, sCurrentLux.indexOf("."));

                                        int convertLux = Integer.parseInt(cleanLux);
                                        if (convertLux >= 255) {
                                            convertLux = 255;
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (Settings.System.canWrite(getContext())) {
                                                Log.d(TAG + "/permission ", "Can write Settings");
                                                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, convertLux);
                                            } else {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                                    getContext().startActivity(intent);
                                                }
                                            }
                                        }


                                        try {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();

                                                        layoutParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                                        layoutParams.screenBrightness = currentLux;
                                                        getActivity().getWindow().setAttributes(layoutParams);
                                                        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                                                        getActivity().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                                                    } catch (Exception e) {
                                                        Log.e(TAG + "/windowsParams", e.getMessage());
                                                    }
                                                }
                                            });

                                        } catch (Exception e) {
                                            Log.e(TAG + "/setScreenBrightness", e.getMessage());
                                        }

                                    } catch (Exception e) {
                                        Log.e(TAG + "/lightstateChange", e.getMessage());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG + "/lightSensor", e.getMessage());
                        }

                    }
                }
                catch (Exception ex)
                {
                    Log.d(TAG, "onViewCreated: "+ ex.getMessage());
                }
            }
        }
    }


}