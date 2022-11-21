package com.example.nymea_dashboard_v3;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.UUID;


public class weatherFragment extends Fragment {

    static final String TAG =  weatherFragment.class.getSimpleName();
    ImageView imWeatherIcon;
    ImageView imWeatherTemperature;
    ImageView imWUV;
    ImageView imWindSpeed;
    TextView tvWeatherTitle;
    TextView tvWeatherDesc;
    TextView tvTemperature;
    TextView tvHumidity;
    TextView tvPressure;
    TextView tvWindSpeed;
    TextView tvWindDir;
    TextView tvAirQuality;
    TextView tvUVIndex;
    TextView tvUVNotes;
    TextView tvExposure;

    public weatherFragment() {
        // Required empty public constructor
    }


    public static weatherFragment newInstance() {
        weatherFragment fragment = new weatherFragment();

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
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvWeatherTitle = view.findViewById(R.id.tvWeatherTitle);
        imWeatherIcon = view.findViewById(R.id.imWeatherIcon);
        tvWeatherDesc = view.findViewById(R.id.tvWeatherDesc);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        imWeatherTemperature = view.findViewById(R.id.imWeatherTempIcon);
        tvHumidity = view.findViewById(R.id.tvHumidity);
        tvWindSpeed =view.findViewById(R.id.tvWindSpeed);
        tvWindDir = view.findViewById(R.id.tvWindDir);
        tvPressure = view.findViewById(R.id.tvPressure);
        tvAirQuality = view.findViewById(R.id.tvAirQuality);
        tvUVIndex = view.findViewById(R.id.tvUV);
        tvUVNotes = view.findViewById(R.id.tvUVNote);
        tvExposure = view.findViewById(R.id.tvExposureTime);
        imWUV = view.findViewById(R.id.imWUV);
        imWindSpeed = view.findViewById(R.id.imWind);

        int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            tvTemperature.setTextSize(20);
            tvHumidity.setTextSize(20);
            tvWindSpeed.setTextSize(20);
            tvWindDir.setTextSize(20);
            tvPressure.setTextSize(20);
            tvAirQuality.setTextSize(20);
            tvUVIndex.setTextSize(20);
            tvWindSpeed.getLayoutParams().width =100;
            tvWindSpeed.requestLayout();
            tvWindDir.getLayoutParams().width =70;
            tvWindDir.requestLayout();
            tvAirQuality.getLayoutParams().width=100;
            tvAirQuality.requestLayout();

        }
        else
        {
            ViewGroup.LayoutParams layoutParams = tvTemperature.getLayoutParams();
            layoutParams.width=65;
            tvTemperature.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams hLayoutParams = tvHumidity.getLayoutParams();
            hLayoutParams.width =65;
            tvHumidity.setLayoutParams(hLayoutParams);
            ViewGroup.LayoutParams pLayoutParams = tvPressure.getLayoutParams();
            pLayoutParams.width=65;
            tvPressure.setLayoutParams(pLayoutParams);
            ViewGroup.LayoutParams wsLayoutParams = tvWindSpeed.getLayoutParams();
            wsLayoutParams.width =58;
            tvWindSpeed.setLayoutParams(wsLayoutParams);
            ViewGroup.LayoutParams wdLayoutParams = tvWindDir.getLayoutParams();
            wdLayoutParams.width =40;
            tvWindDir.setLayoutParams(wdLayoutParams);
            ViewGroup.LayoutParams aLayoutParams = tvAirQuality.getLayoutParams();
            aLayoutParams.width =65;
            tvAirQuality.setLayoutParams(aLayoutParams);
            ViewGroup.LayoutParams uLayoutParams = tvUVIndex.getLayoutParams();
            uLayoutParams.width =65;
            tvUVIndex.setLayoutParams(uLayoutParams);
        }
        House _house = House.getInstance();
        try {
            for (int t = 0; t < _house.Things.size(); t++) {
                if (_house.Things.get(t).classDisplayName.toUpperCase().equals("WEATHER")) {
                    tvWeatherTitle.setText(_house.Things.get(t).name);
                    _house.Things.get(t).setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (state.displayName.toUpperCase()) {


                                        case "TEMPERATURE":
                                            String _formatValue = state.getValue();
                                            tvTemperature.setText(_formatValue.substring(0, _formatValue.length() - 1) + " °C");
                                            float iTemperature = Float.parseFloat(_formatValue);
                                            if (iTemperature <= 18.99) {
                                                imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                                imWeatherTemperature.clearAnimation();
                                            } else if (iTemperature >= 19.00 && iTemperature <= 21.99) {
                                                imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                                                imWeatherTemperature.clearAnimation();
                                            } else if (iTemperature >= 22.00 && iTemperature <= 25.99) {
                                                imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                                                imWeatherTemperature.clearAnimation();
                                            } else if (iTemperature >= 26.00) {
                                                imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                                Animation animation = new AlphaAnimation((float) 1, 0);
                                                animation.setDuration(800);
                                                animation.setInterpolator(new LinearInterpolator());
                                                animation.setRepeatCount(Animation.INFINITE);
                                                animation.setRepeatMode(Animation.REVERSE);
                                                imWeatherTemperature.startAnimation(animation);
                                            }
                                            break;

                                        case "HUMIDITY":
                                            String _humidity = state.getValue();
                                            tvHumidity.setText(_humidity + " %");
                                            break;

                                        case "PRESSURE":
                                            String _pressure = state.getValue();
                                            tvPressure.setText(_pressure + " hPa");
                                            break;

                                        case "WIND SPEED":
                                            String _windSpeed = state.getValue();
                                            double windspeedMPH = Double.parseDouble(_windSpeed) * 2.237;
                                            tvWindSpeed.setText(String.format("%.1f", windspeedMPH) + " mph ");
                                            if (windspeedMPH > 25) {
                                                imWindSpeed.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                                Animation animation = new AlphaAnimation((float) 1, 0);
                                                animation.setDuration(500);
                                                animation.setInterpolator(new LinearInterpolator());
                                                animation.setRepeatCount(Animation.INFINITE);
                                                animation.setRepeatMode(Animation.REVERSE);
                                                imWindSpeed.startAnimation(animation);
                                            } else {
                                                imWindSpeed.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                                imWindSpeed.clearAnimation();
                                            }

                                            break;

                                        case "WIND DIRECTION":
                                            String _windDirection = getWindDirection(Integer.parseInt(state.getValue())).toUpperCase();
                                            tvWindDir.setText(_windDirection);
                                            break;

                                        case "WEATHER DESCRIPTION":
                                            String _rawDesc = state.getValue();
                                            String _description = _rawDesc.substring(0, 1).toUpperCase() + _rawDesc.substring(1);
                                            tvWeatherDesc.setText(_description);

                                            break;

                                        case "WEATHER CONDITION":
                                            String _condition = state.getValue();
                                            if (_condition.contains("-")) {
                                                _condition = _condition.replace("-", "_");
                                            }
                                            if (getContext() != null) {
                                                int img = getResources().getIdentifier("weather_" + _condition, "drawable", requireActivity().getBaseContext().getPackageName());
                                                imWeatherIcon.setImageResource(img);
                                            }
                                            break;
                                    }
                                }
                            });
                        }

                    });

                    for (State state : _house.Things.get(t).states) {
                        ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                switch (state.displayName.toUpperCase()) {
                                    case "TEMPERATURE":
                                        String _formatValue = state.getValue();
                                        tvTemperature.setText(_formatValue.substring(0, _formatValue.length() - 1) + " °C");
                                        float iTemperature = Float.parseFloat(_formatValue);
                                        if (iTemperature <= 18.99) {
                                            imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                            imWeatherTemperature.clearAnimation();
                                        } else if (iTemperature >= 19.00 && iTemperature <= 21.99) {
                                            imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                                            imWeatherTemperature.clearAnimation();
                                        } else if (iTemperature >= 22.00 && iTemperature <= 25.99) {
                                            imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                                            imWeatherTemperature.clearAnimation();
                                        } else if (iTemperature >= 26.00) {
                                            imWeatherTemperature.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                            Animation animation = new AlphaAnimation((float) 1, 0);
                                            animation.setDuration(800);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            imWeatherTemperature.startAnimation(animation);
                                        }
                                        break;

                                    case "HUMIDITY":
                                        String _humidity = state.getValue();
                                        tvHumidity.setText(_humidity + " %");
                                        break;

                                    case "PRESSURE":
                                        String _pressure = state.getValue();
                                        tvPressure.setText(_pressure + " hPa");
                                        break;

                                    case "WIND SPEED":
                                        String _windSpeed = state.getValue();
                                        double windspeedMPH = Double.parseDouble(_windSpeed) * 2.237;
                                        tvWindSpeed.setText(String.format("%.1f", windspeedMPH) + " mph ");
                                        if (windspeedMPH > 25) {
                                            imWindSpeed.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                            Animation animation = new AlphaAnimation((float) 1, 0);
                                            animation.setDuration(500);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            imWindSpeed.startAnimation(animation);
                                        } else {
                                            imWindSpeed.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                            imWindSpeed.clearAnimation();
                                        }
                                        break;

                                    case "WIND DIRECTION":
                                        String _windDirection = getWindDirection(Integer.parseInt(state.getValue())).toUpperCase();
                                        tvWindDir.setText(_windDirection);
                                        break;

                                    case "WEATHER DESCRIPTION":
                                        String _rawDesc = state.getValue();
                                        String _description = _rawDesc.substring(0, 1).toUpperCase() + _rawDesc.substring(1);
                                        tvWeatherDesc.setText(_description);

                                        break;

                                    case "WEATHER CONDITION":
                                        String _condition = state.getValue();
                                        if (_condition.contains("-")) {
                                            _condition = _condition.replace("-", "_");
                                        }
                                        if (getContext() != null) {
                                            int img = getResources().getIdentifier("weather_" + _condition, "drawable", requireActivity().getBaseContext().getPackageName());
                                            imWeatherIcon.setImageResource(img);
                                        }
                                        break;
                                }
                            }
                        });
                    }
                    continue;

                }

                if (_house.Things.get(t).classDisplayName.toUpperCase().equals("UV INDEX")) {
                    _house.Things.get(t).setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if (state.displayName.toUpperCase().equals("UV INDEX")) {
                                        int uvIndex = Integer.parseInt(state.getValue());
                                        if (uvIndex > 5) {
                                            Animation animation = new AlphaAnimation((float) 1, 0);
                                            animation.setDuration(600);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            imWUV.startAnimation(animation);
                                        } else {
                                            imWUV.clearAnimation();
                                        }
                                        tvUVIndex.setText(state.getValue());
                                    }
                                    if (state.typeId.equals(UUID.fromString("0a60b028-97c9-4231-90a4-95ace8c6cf28"))) {

                                        int iexposure = Integer.parseInt(state.getValue());
                                        if (iexposure > 60) {
                                            int hours = iexposure / 60;
                                            int mins = iexposure % 60;
                                            tvExposure.setText(String.valueOf(hours) + " h " + String.valueOf(mins) + " m");
                                        } else {
                                            tvExposure.setText(state.getValue() + " m");
                                        }
                                    }
                                }
                            });
                        }
                    });

                    for (State state : _house.Things.get(t).states) {
                        if (state.displayName.toUpperCase().equals("UV INDEX")) {
                            tvUVIndex.setText(state.getValue());
                            int uvIndex = Integer.parseInt(state.getValue());
                            if (uvIndex > 5) {
                                Animation animation = new AlphaAnimation((float) 1, 0);
                                animation.setDuration(600);
                                animation.setInterpolator(new LinearInterpolator());
                                animation.setRepeatCount(Animation.INFINITE);
                                animation.setRepeatMode(Animation.REVERSE);
                                imWUV.startAnimation(animation);
                            } else {
                                imWUV.clearAnimation();
                            }

                        }
                        if (state.typeId.equals(UUID.fromString("0a60b028-97c9-4231-90a4-95ace8c6cf28"))) {
                            int iexposure = Integer.parseInt(state.getValue());
                            if (iexposure > 60) {
                                int hours = iexposure / 60;
                                int mins = iexposure % 60;
                                tvExposure.setText(String.valueOf(hours) + " h " + String.valueOf(mins) + " m");
                            } else {
                                tvExposure.setText(state.getValue() + " m");
                            }
                        }
                    }
                    continue;
                }

                if (_house.Things.get(t).classDisplayName.toUpperCase().equals("AIR QUALITY INDEX")) {
                    _house.Things.get(t).setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if (state.displayName.toUpperCase().equals("AIR QUALITY")) {

                                        String sAirQuality;
                                        if (state.value.contains(" ")) {
                                            sAirQuality = state.value.substring(0, state.value.indexOf(" "));
                                        } else {
                                            sAirQuality = state.value;
                                        }
                                        tvAirQuality.setText(sAirQuality);
                                    }

                                    if (state.displayName.toUpperCase().equals("CAUTIONARY STATEMENT")) {
                                        if (state.value.equals("None")) {
                                            tvUVNotes.setText("");
                                        } else {
                                            tvUVNotes.setText(state.value);
                                        }
                                    }
                                }
                            });
                        }
                    });

                    for (State state : _house.Things.get(t).states) {
                        if (state.displayName.toUpperCase().equals("AIR QUALITY")) {
                            tvAirQuality.setText(state.getValue());

                        }
                        if (state.displayName.toUpperCase().equals("CAUTIONARY STATEMENT")) {
                            if (state.value.equals("None")) {
                                tvUVNotes.setText("");
                            } else {
                                tvUVNotes.setText(state.value);
                            }

                        }

                    }
                    continue;
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage());
        }
    }


    private int map_direction(int deg){
        int val = deg / 22;

        int remainder = deg - (val * 22);
        if (remainder >= 32)
        {
            val++;
        }

        if (val >= 16)
        {
            val = 0;
        }

        return val;
    }

    String getWindDirection(int _direction)
    {
        String windDirection ="";
        int Quadrant = map_direction(_direction);

        switch (Quadrant)
        {
            case 0: { windDirection = "N"; }break;
            case 1: { windDirection = "NNE"; }break;
            case 2: {windDirection = "NE"; }break;
            case 3: {windDirection = "ENE"; }break;
            case 4: {windDirection = "E"; }break;
            case 5: {windDirection = "ESE"; }break;
            case 6: {windDirection = "SE"; }break;
            case 7: {windDirection = "SSE"; }break;
            case 8: {windDirection = "S"; }break;
            case 9: {windDirection = "SSW"; }break;
            case 10: {windDirection = "SW"; }break;
            case 11: {windDirection = "WSW"; }break;
            case 12: {windDirection = "W"; }break;
            case 13: {windDirection = "WNW"; }break;
            case 14: {windDirection = "NW"; }break;
            case 15: {windDirection = "NNW"; }break;

        }
        return windDirection;
    }
}