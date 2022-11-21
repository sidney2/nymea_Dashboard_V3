package com.example.nymea_dashboard_v3;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class pwrFragment extends Fragment {

static List<UUID> things = new ArrayList<>();
static final String TAG = pwrFragment.class.getSimpleName();
static House _house = House.getInstance();
static SharedPreferences prefs;

RecyclerView rcPwrItems;
pwrAdapter mPwrAdapter;

    public pwrFragment() {
        // Required empty public constructor
    }


    public static pwrFragment newInstance(String param1, String param2) {
        pwrFragment fragment = new pwrFragment();

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
        return inflater.inflate(R.layout.fragment_pwr, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcPwrItems = view.findViewById(R.id.rcSockets);
        rcPwrItems.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager;

            gridLayoutManager = new GridLayoutManager(getContext(),things.size());



        rcPwrItems.setLayoutManager(gridLayoutManager);
        mPwrAdapter = new pwrAdapter();
        rcPwrItems.setAdapter(mPwrAdapter);

    }

    class pwrAdapter extends RecyclerView.Adapter<pwrAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pwr_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                try {
                    if(_house.getThingById(things.get(position)).classDisplayName.toUpperCase().equals("TUYA SWITCH")){
                        if(_house.getThingById(things.get(position)).name.toUpperCase().contains("LIGHT")){

                            holder.btnIcon.setImageResource(R.drawable.select_none);
                        }
                        else
                        {
                            holder.btnIcon.setImageResource(R.drawable.power);

                        }
                        if(_house.getThingById(things.get(position)).stateByName("connected").value.equals("true"))
                        {
                            holder.connectedIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.connected));
                            holder.connectedIcon.clearAnimation();
                        }
                        else
                        {
                            holder.connectedIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                            Animation animation =new AlphaAnimation((float) 1,0);
                            animation.setDuration(500);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(Animation.INFINITE);
                            animation.setRepeatMode(Animation.REVERSE);
                            holder.connectedIcon.startAnimation(animation);
                        }


                    }
                    holder.tvBtnText.setText(_house.getThingById(things.get(position)).name.replace("_"," "));
                    if(_house.getThingById(things.get(position)).classDisplayName.toUpperCase().equals("GENERIC POWER SOCKET")){
                        holder.connectedIcon.setVisibility(View.GONE);
                    }

                    if(_house.getThingById(things.get(position)).classDisplayName.toUpperCase().equals("GENERIC LIGHT")){
                        holder.connectedIcon.setVisibility(View.GONE);
                    }

                    if(_house.getThingById(things.get(position)).stateByName("power").value.equals("true")){
                        holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                        holder.pwrBtn.setBackgroundResource(R.drawable.btn_onbkgrd);
                    }
                    else
                    {
                        holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.blue));
                        holder.pwrBtn.setBackgroundResource(R.drawable.btn_backgrd);
                    }

                    holder.pwrBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                State _currState = _house.getThingById(things.get(position)).stateByName("power");
                                String actionId = _house.getThingById(things.get(position)).actionByName("power").typeId.toString();
                                JSONObject jParams = new JSONObject();
                                jParams.put("actionTypeId", actionId);
                                JSONArray jActionParams = new JSONArray();
                                JSONObject jInnerActionParmas = new JSONObject();
                                jInnerActionParmas.put("paramTypeId", actionId);

                                if (_currState.value.equals("true")) {
                                    jInnerActionParmas.put("value", false);
                                    holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                                    holder.pwrBtn.setBackgroundResource(R.drawable.btn_backgrd);
                                } else {
                                    holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.on));
                                    holder.pwrBtn.setBackgroundResource(R.drawable.btn_onbkgrd);
                                    jInnerActionParmas.put("value", true);
                                }


                                jActionParams.put(jInnerActionParmas);
                                jParams.put("params", jActionParams);
                                jParams.put("thingId", things.get(position));
                                String sendAction = JsonHandler.createCommandObj(80, "Integrations.ExecuteAction", jParams, _house.token);
                                String _address = _house.hostAddress;

                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(_address).build();

                                WebSocketListener wsListener = new WebSocketListener() {
                                    @Override
                                    public void onOpen(WebSocket webSocket, Response response) {
                                        super.onOpen(webSocket, response);
                                        String[] locale = {"locale:en-GB"};
                                        String handshake = JsonHandler.createCommand(1, "JSONRPC.Hello", locale,_house.token);
                                        webSocket.send(handshake);

                                    }

                                    @Override
                                    public void onMessage(WebSocket webSocket, String text) {
                                        super.onMessage(webSocket, text);
                                        webSocket.send(sendAction);
                                        webSocket.close(1001, "I'm done");

                                    }

                                };

                            client.newWebSocket(request, wsListener);
                            client.dispatcher().executorService().shutdown();

                            }
                            catch (Exception e)
                            {
                                Log.e(TAG +"/pwrClick",e.getMessage());
                            }
                        }
                    });

                    _house.getThingById(things.get(position)).setOnStateChangeListener(new Thing.stateChangeListener() {
                        @Override
                        public void onStateValueChange(State state) {
                            if(state.name.equals("power")){
                                if(state.value.equals("true"))
                                {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                                            holder.pwrBtn.setBackgroundResource(R.drawable.btn_onbkgrd);
                                        }
                                    });

                                }
                                else
                                {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.btnIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.blue));
                                            holder.pwrBtn.setBackgroundResource(R.drawable.btn_backgrd);
                                        }
                                    });

                                }
                            }

                            if(state.name.equals("connected")){
                                if(state.value.equals("true")){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.connectedIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.connected));
                                            holder.connectedIcon.clearAnimation();
                                        }
                                    });

                                }
                                else
                                {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.connectedIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.on));
                                            Animation animation =new AlphaAnimation((float) 1,0);
                                            animation.setDuration(500);
                                            animation.setInterpolator(new LinearInterpolator());
                                            animation.setRepeatCount(Animation.INFINITE);
                                            animation.setRepeatMode(Animation.REVERSE);
                                            holder.connectedIcon.startAnimation(animation);
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
                catch (Exception e){
                    Log.e(TAG+"bind",e.getMessage());
                }
        }

        @Override
        public int getItemCount() {
            return things.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
        {
            final ConstraintLayout socketLayout;

            final RelativeLayout pwrBtn;
            final ImageView btnIcon;
            final ImageView connectedIcon;
            final TextView tvBtnText;
            ViewHolder(View v){
                super(v);
                socketLayout = v.findViewById(R.id.socketsLayout);

                pwrBtn = v.findViewById(R.id.pwrItemLayout);
                btnIcon = v.findViewById(R.id.imBtnIcon);
                connectedIcon = v.findViewById(R.id.imNConnected);
                tvBtnText = v.findViewById(R.id.tvButtonText);
            }
        }
    }
}