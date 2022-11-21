package com.example.nymea_dashboard_v3;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class roomTemperatureFragment extends Fragment {

  static final String TAG = roomTemperatureFragment.class.getSimpleName();
  static House _house = House.getInstance();
  static String currRoom;

  RecyclerView.Adapter mRoomTemperatureAdapter;
  RecyclerView rcRoomTemperatures;

  List<UUID> thingIds = new ArrayList<>();

    public roomTemperatureFragment() {
        // Required empty public constructor
    }


    public static roomTemperatureFragment newInstance(String param1, String param2) {
        roomTemperatureFragment fragment = new roomTemperatureFragment();

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
        return inflater.inflate(R.layout.fragment_room_temperature, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcRoomTemperatures = view.findViewById(R.id.rcRoomTemperatures);
        rcRoomTemperatures.setHasFixedSize(false);

        rcRoomTemperatures.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRoomTemperatureAdapter = new roomTemperatureAdapter();
        rcRoomTemperatures.setAdapter(mRoomTemperatureAdapter);
        for(int t=0;t<_house.getListOfThingsByClassDisplayName("generic temperature sensor").size();t++) {
            if (!_house.getThingById(_house.getListOfThingsByClassDisplayName("generic temperature sensor").get(t)).name.contains(currRoom)) {
                if (!_house.getThingById(_house.getListOfThingsByClassDisplayName("generic temperature sensor").get(t)).name.contains("House")) {
                    thingIds.add(_house.getListOfThingsByClassDisplayName("generic temperature sensor").get(t));
                }
            }
        }
    }

    class roomTemperatureAdapter extends RecyclerView.Adapter<roomTemperatureAdapter.ViewHolder>
    {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_temp_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

                   String _temperature = _house.getThingById(thingIds.get(position)).stateByName("temperature").value;
                   String rName = _house.getThingById(thingIds.get(position)).name;
                   rName = rName.substring(0,rName.indexOf("T")-1);
                   holder.tvRoomName.setText(rName);
                   holder.tvRTemp.setText(_temperature + " °C");

                     int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                    if(screenSz == Configuration.SCREENLAYOUT_SIZE_XLARGE){
                        holder.tvRTemp.setTextSize(20);
                    }
                   float iTemperature = Float.parseFloat(_temperature);
                   if (iTemperature <= 18.9) {
                       holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                       holder.imRTemp.clearAnimation();
                   } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                       holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                       holder.imRTemp.clearAnimation();
                   } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                       holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                       holder.imRTemp.clearAnimation();
                   } else if (iTemperature >= 26) {
                       holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                       Animation animation =new AlphaAnimation((float) 1,0);
                       animation.setDuration(800);
                       animation.setInterpolator(new LinearInterpolator());
                       animation.setRepeatCount(Animation.INFINITE);
                       animation.setRepeatMode(Animation.REVERSE);
                       holder.imRTemp.startAnimation(animation);
                   }
                   _house.getThingById(thingIds.get(position)).setOnStateChangeListener(new Thing.stateChangeListener() {
                       @Override
                       public void onStateValueChange(State state) {
                           getActivity().runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   try {


                                       if (state.name.equals("temperature")) {
                                           String _temperature = state.value;
                                           holder.tvRTemp.setText(_temperature + " °C");
                                           float iTemperature = Float.parseFloat(_temperature);
                                           if (iTemperature <= 18.9) {
                                               holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                               holder.imRTemp.clearAnimation();
                                           } else if (iTemperature >= 19.0 && iTemperature <= 21.9) {
                                               holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.connected));
                                               holder.imRTemp.clearAnimation();
                                           } else if (iTemperature >= 22.0 && iTemperature <= 25.9) {
                                               holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.YELLOW));
                                               holder.imRTemp.clearAnimation();
                                           } else if (iTemperature >= 26) {
                                               holder.imRTemp.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                               Animation animation = new AlphaAnimation((float) 1, 0);
                                               animation.setDuration(800);
                                               animation.setInterpolator(new LinearInterpolator());
                                               animation.setRepeatCount(Animation.INFINITE);
                                               animation.setRepeatMode(Animation.REVERSE);
                                               holder.imRTemp.startAnimation(animation);
                                           }
                                       }
                                   }
                                   catch (Exception e){
                                       Log.e(TAG+"/roomtemp",e.getMessage());
                                   }
                               }
                           });
                       }
                   });




        }

        @Override
        public int getItemCount() {
            return thingIds.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final TextView tvRoomName;
            final TextView tvRTemp;
            final ImageView imRTemp;
            final ConstraintLayout roomtempItem;
            ViewHolder(View v)
            {
                super(v);
                tvRoomName = v.findViewById(R.id.tvRoomName);
                tvRTemp = v.findViewById(R.id.tvRTemp);
                imRTemp = v.findViewById(R.id.imRTemp);
                roomtempItem = v.findViewById(R.id.roomtempItemLayout);
            }
        }
    }

}