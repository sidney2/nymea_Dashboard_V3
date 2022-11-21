package com.example.nymea_dashboard_v3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nymea_dashboard_v3.databinding.FragmentHouseBinding;

import org.w3c.dom.Text;

import java.util.UUID;

public class houseFrag extends Fragment {

    private FragmentHouseBinding binding;

    SharedPreferences prefs;
    static House _house = House.getInstance();
    static final String TAG = houseFrag.class.getSimpleName();
    static RecyclerView.Adapter mRoomAdapter;
    static Context mContext;
    static Activity mActivity;
    static boolean returning = false;

    private static FragmentManager FragManager;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        mContext = getContext();
        mActivity = getActivity();
        FragManager = getChildFragmentManager();
        binding = FragmentHouseBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rcRooms = view.findViewById(R.id.rcRooms);
        rcRooms.setHasFixedSize(true);
        rcRooms.setLayoutManager(new GridLayoutManager(getContext(),6));
        mRoomAdapter = new roomAdapter();
        rcRooms.setAdapter(mRoomAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs =((MainActivity)getActivity()).prefs;
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(_house.getHostname());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if( _house.getThingByName("house light level (avg)") !=null) {
           // _house.getThingByName("house light level (avg)").listener = null;
        }


    }


    public static void buildDashBoard()
    {
        for(UUID houseThingId : _house.HouseThings){
            switch(_house.getThingById(houseThingId).classDisplayName.toUpperCase())
            {
                case "GENERIC THING":
                    switch (_house.getThingById(houseThingId).name.toUpperCase()) {
                        case "ANALOG CLOCK":
                        analogClockFragment mClockFrag = new analogClockFragment();
                        FragmentTransaction mcFragmentTransaction = FragManager.beginTransaction();
                        mcFragmentTransaction.add(R.id.mainClockFragHolder, mClockFrag);
                        mcFragmentTransaction.commit();
                        break;

                        case "DAY CALENDAR":
                            calendarFragment mCalFrag = new calendarFragment();
                            FragmentTransaction calFragmentTransaction = FragManager.beginTransaction();
                            calFragmentTransaction.add(R.id.mainCalendarFragHolder,mCalFrag);
                            calFragmentTransaction.commit();
                            break;
                    }

                    break;

                case "WEATHER":
                {
                    weatherFragment mWeatherFrag = new weatherFragment();
                    FragmentTransaction mwFragmentTransaction = FragManager.beginTransaction();
                    mwFragmentTransaction.add(R.id.mainWeatherFragHolder,mWeatherFrag);
                   mwFragmentTransaction.commit();
                }
            }
        }

        for(Room _room:_house.rooms){

            mRoomAdapter.notifyDataSetChanged();
        }
try {
    _house.getThingByName("house light level (avg)").setOnStateChangeListener(new Thing.stateChangeListener() {
        @Override
        public void onStateValueChange(State state) {
            String sCurrentLux = state.getValue();
            try {
                final float currentLux = (float) ((0.834 * Float.parseFloat(sCurrentLux)) / 100);


                int brightnessMode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                }
                int currentBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);


                String cleanLux = sCurrentLux.substring(0, sCurrentLux.indexOf("."));

                int convertLux = Integer.parseInt(cleanLux);
                if (convertLux >= 255) {
                    convertLux = 255;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(mContext)) {
                        Log.d(TAG + "/permission ", "Can write Settings");
                        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, convertLux);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                            mContext.startActivity(intent);
                        }
                    }
                }


                        try {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    WindowManager.LayoutParams layoutParams = mActivity.getWindow().getAttributes();

                                    layoutParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                    layoutParams.screenBrightness = currentLux;
                                    mActivity.getWindow().setAttributes(layoutParams);
                                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                                    mActivity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                                }
                            });

                        } catch (Exception e) {
                            Log.e(TAG + "/setScreenBrightness", e.getMessage());
                        }


            }
            catch (Exception e){
                Log.e(TAG+"/houseLightSensor",e.getMessage());
            }
        }

    });
}
catch (Exception e){
    Log.e(TAG+"houseLightLevel",e.getMessage());
}
    }

    public class roomAdapter extends RecyclerView.Adapter<roomAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                try {
                    if(!returning) {
                        if (_house.rooms.get(position).mName.equals(_house.defaultRoom)) {
                            int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                            switch (screenSz) {
                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                    largeFrag.roomId = position;
                                    NavHostFragment.findNavController(houseFrag.this)
                                            .navigate(R.id.action_houseFrag_to_largeFrag);
                                    break;

                                default:
                                    smallFragment.roomId = position;
                                    NavHostFragment.findNavController(houseFrag.this)
                                            .navigate(R.id.action_houseFrag_to_smallFragment);
                                    break;
                            }
                        }
                    }
                    holder.mRoomLabel.setText(_house.rooms.get(position).getName());
                    switch(_house.rooms.get(position).mRoomType){
                        case 0:
                            holder.imRoomIcon.setImageResource(R.drawable.dashboard);
                            break;
                        case 1:
                            holder.imRoomIcon.setImageResource(R.drawable.power);
                            break;

                        case 2:
                            holder.imRoomIcon.setImageResource(R.drawable.light_off);
                            break;

                        case 3:
                            holder.imRoomIcon.setImageResource(R.drawable.select_none);
                            break;
                    }

                    holder.mbackground.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                            switch( screenSz) {
                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                    largeFrag.roomId = position;
                                    NavHostFragment.findNavController(houseFrag.this)
                                            .navigate(R.id.action_houseFrag_to_largeFrag);
                                    break;

                                default:
                                        smallFragment.roomId = position;
                                        NavHostFragment.findNavController(houseFrag.this)
                                                .navigate(R.id.action_houseFrag_to_smallFragment);
                                    break;
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e(TAG+"/roomBinder",e.getMessage());
                }
        }

        @Override
        public int getItemCount() {
            return _house.rooms.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            final RelativeLayout mbackground;
            final TextView mRoomLabel;
            final ImageView imRoomIcon;
             ViewHolder(View v){
                 super(v);
                 mbackground = v.findViewById(R.id.roomitemLayout);
                 mRoomLabel = v.findViewById(R.id.tvRoomLabel);
                 imRoomIcon = v.findViewById(R.id.imRoomIcon);
             }
        }
    }


}