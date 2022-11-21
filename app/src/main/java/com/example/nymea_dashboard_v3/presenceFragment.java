package com.example.nymea_dashboard_v3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class presenceFragment extends Fragment {

    static final String TAG = presenceFragment.class.getSimpleName();
    static House _house = House.getInstance();
    List<UUID> thingIds = new ArrayList<>();


    RecyclerView rcPresence;
    RecyclerView.Adapter mPresenceAdapter;

    public presenceFragment() {
        // Required empty public constructor
    }


    public static presenceFragment newInstance(String param1, String param2) {
        presenceFragment fragment = new presenceFragment();

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
        return inflater.inflate(R.layout.fragment_presence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for(UUID thingid:_house.getListOfThingsByClassDisplayName("Network device"))
        {
            if(_house.getThingById(thingid).name.contains("Simon")){
                thingIds.add(thingid);
            }
            if(_house.getThingById(thingid).name.contains("Belinda")){
                thingIds.add(thingid);
            }
        }

        rcPresence = view.findViewById(R.id.rcPresence);
        rcPresence.setHasFixedSize(false);
        LinearLayoutManager pLinearLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rcPresence.setLayoutManager(pLinearLayout);
        mPresenceAdapter = new presencesAdapter();
        rcPresence.setAdapter(mPresenceAdapter);

    }

    class presencesAdapter extends RecyclerView.Adapter<presencesAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.presence_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                String name = _house.getThingById(thingIds.get(position)).name;
                name = name.substring(0,name.indexOf("-"));
                if(_house.getThingById(thingIds.get(position)).stateByName("isPresent").value.equals("true")){
                    holder.tvPresenceText.setText(name + " is home");
                    holder.imPresenceIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.connected));
                }
                else
                {
                    holder.tvPresenceText.setText(name + " is away");
                    holder.imPresenceIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                }
                _house.getThingById(thingIds.get(position)).setOnStateChangeListener(new Thing.stateChangeListener() {
                    @Override
                    public void onStateValueChange(State state) {
                        if(state.name.contains("isPresent")){
                            if(state.value.equals("true")){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String name = _house.getThingById(thingIds.get(position)).name;
                                        name = name.substring(0,name.indexOf("-"));
                                        holder.tvPresenceText.setText(name + " is home");
                                        holder.imPresenceIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.connected));
                                    }
                                });
                            }
                            else
                            {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String name = _house.getThingById(thingIds.get(position)).name;
                                        name = name.substring(0,name.indexOf("-"));
                                        holder.tvPresenceText.setText(name + " is away");
                                        holder.imPresenceIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                                    }
                                });


                            }
                        }
                    }
                });
            }
            catch (Exception e){
                Log.e(TAG+"/presenceBind",e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return  thingIds.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final ImageView imPresenceIcon;
            final TextView tvPresenceText;
            ViewHolder(View v){
                super(v);
                imPresenceIcon = v.findViewById(R.id.imPresenceIcon);
                tvPresenceText = v.findViewById(R.id.tvPresenceText);
            }
        }
    }
}