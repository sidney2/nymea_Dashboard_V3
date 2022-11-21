package com.example.nymea_dashboard_v3;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class settingsFragment extends Fragment {

private static final String TAG = settingsFragment.class.getSimpleName();
EditText edURL;
EditText edUsr;
EditText edPWD;
Button btnLogin;
SharedPreferences prefs;
    public settingsFragment() {
        // Required empty public constructor
    }


    public static settingsFragment newInstance(String param1, String param2) {
        settingsFragment fragment = new settingsFragment();

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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Settings");

        edURL = view.findViewById(R.id.edHostURL);
        edUsr = view.findViewById(R.id.edUsr);
        edPWD = view.findViewById(R.id.edpwd);
        btnLogin = view.findViewById(R.id.btnLogin);
        prefs = ((MainActivity) getActivity()).prefs;
        String sPrefUrl = prefs.getString("wsurl","");
        String sPrefsUsr=prefs.getString("usr","");
        String sPrefsPwd = prefs.getString("pwd","");
        if(sPrefUrl !=""){
            edURL.setText(sPrefUrl);
            edUsr.setText(sPrefsUsr);
            edPWD.setText(sPrefsPwd);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String wsulrl = "ws://" + edURL.getText().toString() +":4445";
                Log.d(TAG + "/loginUrl",wsulrl);
                String usr = edUsr.getText().toString();
                String pwd = edPWD.getText().toString();
                try {
                    final JSONObject hand = new JSONObject();
                    hand.put("id", 1);
                    hand.put("method", "JSONRPC.Hello");
                    JSONObject localeObj = new JSONObject();
                    localeObj.put("locale", "en-GB");
                    hand.put("params", localeObj);


                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(wsulrl).build();

                    WebSocketListener wsListener = new WebSocketListener() {
                        @Override
                        public void onOpen(WebSocket webSocket, Response response) {
                            webSocket.send(hand.toString());
                        }

                        @Override
                        public void onMessage(WebSocket webSocket, String text) {
                            Log.d(TAG,"Response: "+ text );
                            try {
                                JSONObject jResponse = JsonHandler.processJsonResponse(text);
                                String status = jResponse.getString("status");
                                if(status.contains("success")) {
                                    int cmdId = jResponse.getInt("id");
                                    switch(cmdId)
                                    {
                                        case 1:


                                            JSONObject params = jResponse.getJSONObject("params");
                                            boolean _authRequired = JsonHandler.getParamValues(params,"authenticationRequired").getBoolean("authenticationRequired");
                                            String _hostName = JsonHandler.getParamValues(params,"name").getString("name");
                                           String _nymeaID = JsonHandler.getParamValues(params,"uuid").getString("uuid");

                                            if(_nymeaID !=""){

                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("hostid",_nymeaID);
                                                editor.putString("wsurl",wsulrl);
                                                editor.apply();
                                                editor.commit();
                                            }
                                            if(_hostName !="")
                                            {
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putString("hostName",_hostName);
                                                editor.apply();
                                                editor.commit();
                                            }

                                            if(_authRequired)
                                            {

                                                String[] authParams ={"deviceName:nymea Dashboard V3 app on "+ Build.MODEL,"username:"+edUsr.getText().toString(),
                                                        "password:" + edPWD.getText().toString()};

                                                String _authResponse = JsonHandler.createCommand(2,"JSONRPC.Authenticate",authParams,"");
                                                webSocket.send(_authResponse);
                                            }
                                            break;

                                        case 2:
                                            JSONObject authParams = jResponse.getJSONObject("params");
                                            boolean _authenicated = JsonHandler.getParamValues(authParams,"success").getBoolean("success");
                                            if(_authenicated){
                                                String _authToken = JsonHandler.getParamValues(authParams,"token").getString("token");
                                                if(_authToken !="")
                                                {
                                                    SharedPreferences.Editor editor =prefs.edit();
                                                    editor.putString("token",_authToken);
                                                    editor.apply();
                                                    editor.commit();
                                                    webSocket.close(1001,"im done");
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MainActivity.connect();
                                                            NavHostFragment.findNavController(settingsFragment.this)
                                                                    .navigate(R.id.action_settingsFragment_to_houseFrag);
                                                        }
                                                    });

                                                }
                                            }
                                            else
                                            {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(),"Error Authenticating user",Toast.LENGTH_LONG);
                                                    }
                                                });
                                            }
                                            break;
                                    }
                                }
                            }
                            catch (Exception e){
                                Log.e(TAG,e.getMessage());
                            }
                        }

                        @Override
                        public void onClosing(WebSocket webSocket, int code, String reason) {
                            webSocket.close(1000,null);
                            webSocket.cancel();
                        }

                        @Override
                        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Failed to connect to "+wsulrl, Toast.LENGTH_LONG).show();
                                }
                            });
                            Log.e(TAG,"Failed to Connect");
                        }
                    };

                    client.newWebSocket(request,wsListener);
                    client.dispatcher().executorService().shutdown();
                }
                catch (JSONException e){
                    Log.e(TAG,e.getLocalizedMessage());
                }
            }
        });
    }

}