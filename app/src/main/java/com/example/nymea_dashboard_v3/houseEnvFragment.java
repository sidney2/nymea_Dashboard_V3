package com.example.nymea_dashboard_v3;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


public class houseEnvFragment extends Fragment {

   static House _house = House.getInstance();

   ImageView imHouseTemp;
   TextView tvHouseTemp;
   TextView tvHouseHumidity;
   TextView tvHouseLux;

    public houseEnvFragment() {
        // Required empty public constructor
    }


    public static houseEnvFragment newInstance() {
        houseEnvFragment fragment = new houseEnvFragment();


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
        return inflater.inflate(R.layout.fragment_house_env, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imHouseTemp =view.findViewById(R.id.imHouseTemp);
        tvHouseTemp = view.findViewById(R.id.tvHouseTemp);
        tvHouseHumidity = view.findViewById(R.id.tvHouseHumidity);
        tvHouseLux = view.findViewById(R.id.tvHouseLux);

        int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            tvHouseLux.setTextSize(20);
            tvHouseHumidity.setTextSize(20);
            tvHouseTemp.setTextSize(20);
        }
        else
        {
            ViewGroup.LayoutParams tLayoutParams = tvHouseTemp.getLayoutParams();
            tLayoutParams.width=50;
            tvHouseTemp.setLayoutParams(tLayoutParams);

            ViewGroup.LayoutParams hLayoutParams = tvHouseHumidity.getLayoutParams();
            hLayoutParams.width=50;
            tvHouseHumidity.setLayoutParams(hLayoutParams);

            ViewGroup.LayoutParams lLayoutParams = tvHouseLux.getLayoutParams();
            lLayoutParams.width=50;
            tvHouseLux.setLayoutParams(lLayoutParams);
        }

                    String _Temp = _house.getThingByName("HOUSE TEMPERATURE (AVG)").stateByName("temperature").value;
                    String _temperature = _Temp;
                    float iTemperature = Float.parseFloat(_temperature);
                    if (iTemperature <= 18.9) {
                        imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                        imHouseTemp.clearAnimation();
                    } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                        imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                        imHouseTemp.clearAnimation();
                    } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                        imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                        imHouseTemp.clearAnimation();
                    } else if (iTemperature >= 26) {
                        imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));

                            Animation animation =new AlphaAnimation((float) 1,0);
                            animation.setDuration(800);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(Animation.REVERSE);
                            imHouseTemp.startAnimation(animation);

                    }

                    tvHouseTemp.setText(_temperature + " °C");
                    _house.getThingByName("HOUSE TEMPERATURE (AVG)").setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                            if(state.name.toUpperCase().equals("TEMPERATURE")) {
                                String _Temp = state.value;
                                String _temperature = _Temp;
                                float iTemperature = Float.parseFloat(_temperature);
                                if (iTemperature <= 18.9) {
                                    imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                    imHouseTemp.clearAnimation();
                                } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                                    imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                                    imHouseTemp.clearAnimation();
                                } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                                    imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                                    imHouseTemp.clearAnimation();
                                } else if (iTemperature >= 26) {
                                    imHouseTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                    Animation animation =new AlphaAnimation((float) 1,0);
                                    animation.setDuration(800);
                                    animation.setInterpolator(new LinearInterpolator());
                                    animation.setRepeatCount(Animation.INFINITE);
                                    animation.setRepeatMode(Animation.REVERSE);
                                    imHouseTemp.startAnimation(animation);
                                }

                                tvHouseTemp.setText(_temperature + " °C");
                            }

                                }
                            });
                        }

                    });

                    tvHouseHumidity.setText(_house.getThingByName("house humidity (avg)").stateByName("humidity").getValue() + " %");
                    _house.getThingByName("house humidity (avg)").setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            if(state.name.toUpperCase().equals("HUMIDITY")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvHouseHumidity.setText(state.value + " %");
                                    }
                                });

                            }
                        }
                    });

                    tvHouseLux.setText(_house.getThingByName("House light level (avg)").stateByName("Lightintensity").value);
                    _house.getThingByName("house light level (avg)").setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            if(state.name.toUpperCase().equals("LIGHTINTENSITY")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvHouseLux.setText(state.value);
                                    }
                                });

                            }
                        }
                    });

    }
}