package com.example.nymea_dashboard_v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.nymea_dashboard_v3.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinWorkerThread;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {


    static Activity currentActivity = null;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = MainActivity.class.getSimpleName();
    static SharedPreferences prefs;
    static Context mContext;
    @SuppressLint("InvalidWakeLockTag")
    PowerManager.WakeLock wakeLock = null;

    static House _house = House.getInstance();


    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setLogo(R.drawable.icon_small);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        PowerManager pwrManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pwrManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "my Lock");
        wakeLock.acquire();

        mContext = this;
        currentActivity = this;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        cm.registerNetworkCallback(builder.build(),
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Toast.makeText(mContext,"cm Wifi connected",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLost(Network network){
                        finishAndRemoveTask();
                    }
                });

        if(connectWifi()){
            prefs = getSharedPreferences("nymeaDashboard",0);
            String wsURL = prefs.getString("wsurl","");
            if(wsURL == ""){
                NavController navController1 = Navigation.findNavController(this,R.id.nav_host_fragment_content_main);
                navController1.navigateUp();
                navController1.navigate(R.id.settingsFragment);

            }
            else
            {

                connect();
            }
        }
        else
        {
            this.finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Navigation.findNavController(this,R.id.nav_host_fragment_content_main).navigate(R.id.action_houseFrag_to_settingsFragment);
        }

        if(id==R.id.action_clear_prefs)
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(this,"Prefs cleared",Toast.LENGTH_LONG).show();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public boolean connectWifi() {
        try {
            Toast.makeText(this,"Checking Wifi",Toast.LENGTH_SHORT).show();
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()) {

                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo.getNetworkId() ==-1) {


                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    wifiConfiguration.SSID = "\"\"";
                    wifiConfiguration.preSharedKey = "\"\"";

                    wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);


                    wifiManager.addNetwork(wifiConfiguration);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration conf : networks) {
                        if (conf.SSID != null && conf.SSID.equals("\"\"")) {
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(conf.networkId, true);
                            wifiManager.reconnect();
                            Log.d(TAG + "/wificonnect", "Wifi:" + conf.SSID + " connected");
                            Thread.sleep(2000);
                        }
                    }
                }


                return true;
            }
            else
            {
                return true;
            }

        }
        catch (Exception e){
            Log.e(TAG + "/connectWifi",e.getMessage());
            return false;
        }
    }

    public static void connect(){

       _house.hostAddress= prefs.getString("wsurl","");
        _house.token = prefs.getString("token","");
        _house.setHostId(prefs.getString("hostid",""));
        _house.setHostName(prefs.getString("hostName",""));
        _house.defaultRoom =prefs.getString("defaultroom","");
        if(_house.hostAddress !="") {
            Toast.makeText(mContext, "Connecting to " + _house.hostName, Toast.LENGTH_LONG).show();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(_house.hostAddress).build();

            WebSocketListener webSocketListener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    String[] locale = {"locale:en-GB"};
                    String handshake = JsonHandler.createCommand(1, "JSONRPC.Hello", locale,_house.token);
                    webSocket.send(handshake);

                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    try{
                        JSONObject jResponse = JsonHandler.processJsonResponse(text);
                        if(jResponse.has("status")){
                            String _status = jResponse.getString("status");
                            if(_status.contains("success"))
                            {
                                int iCMD = jResponse.getInt("id");
                                JSONObject jParams = jResponse.getJSONObject("params");
                                switch(iCMD){
                                    case 1:
                                        JSONObject jAuthObj = JsonHandler.getParamValues(jParams,"authenticated");
                                        if(jAuthObj != null){
                                            boolean isAuthenticated = jAuthObj.getBoolean("authenticated");
                                            if(isAuthenticated){
                                                Looper.prepare();
                                                Toast.makeText(mContext, "Authenticated", Toast.LENGTH_SHORT).show();
                                                if(_house.Things.size()==0) {
                                                    String _getAllThingsRequest = "{\"id\":8,\"method\":\"Integrations.GetThings\",\"token\":\"" + _house.token + "\"}";
                                                    webSocket.send(_getAllThingsRequest);
                                                }
                                            }
                                        }
                                        break;

                                    case 5:
                                        try {
                                            JSONObject jDashBoardObj = JsonHandler.getParamValues(jParams, "value");
                                            String sDashBoards = JsonHandler.getParamValue(jDashBoardObj, "value");
                                            JSONArray jDashBoards = new JSONArray(sDashBoards);
                                            for (int r = 0; r < jDashBoards.length(); r++) {
                                                JSONObject jObj = jDashBoards.getJSONObject(r);
                                                String objType = jObj.getString("type");
                                                if (objType.toUpperCase().equals("FOLDER")) {
                                                    String roomName = jObj.getString("name");
                                                    String roomIcon = jObj.getString("icon");
                                                    Room newRoom = new Room();
                                                    newRoom.setName(roomName);
                                                    newRoom.setID(r);
                                                    newRoom.setIcon(roomIcon);
                                                    switch (roomIcon.toUpperCase()) {
                                                        case "DASHBOARD":
                                                            newRoom.setRoomType(0);
                                                            break;

                                                        case "POWER":
                                                            newRoom.setRoomType(1);
                                                            break;

                                                        case "LIGHT":
                                                            newRoom.setRoomType(2);
                                                            break;

                                                        case "UNCATEGORIZED":
                                                            newRoom.setRoomType(3);
                                                            break;

                                                    }

                                                    String sThingsinRoom = JsonHandler.getParamValue(jObj, "model");
                                                    JSONArray jThingsInRoom = new JSONArray(sThingsinRoom);
                                                    for (int t = 0; t < jThingsInRoom.length(); t++) {
                                                        JSONObject jthing = jThingsInRoom.getJSONObject(t);
                                                        String thingtype = jthing.getString("type");
                                                        if (thingtype.contains("webview")) {

                                                            Thing webviewThing = new Thing();
                                                            webviewThing.name = jthing.getString("url");
                                                            webviewThing.className = "webview";

                                                            webviewThing.id = UUID.randomUUID();
                                                            webviewThing.classDisplayName = "WebView";
                                                            webviewThing.classId = UUID.fromString("5b577986-ebd2-11ec-8ea0-0242ac120002");
                                                            _house.Things.add(webviewThing);
                                                            newRoom.ThingIds.add(webviewThing.id);
                                                        } else {
                                                            UUID thingId = UUID.fromString(jthing.getString("thingId"));
                                                            newRoom.ThingIds.add(thingId);
                                                        }

                                                    }
                                                    _house.rooms.add(newRoom);
                                                }
                                                else if (objType.toUpperCase().equals("THING")) {
                                                        UUID houseThingId = UUID.fromString(jObj.getString("thingId"));
                                                        _house.HouseThings.add(houseThingId);
                                                }
                                            }

                                            String[] NotifcationNamespaces = {"JSONRPC", "System", "Integrations", "AppData"};
                                            JSONArray NotifcationArray = new JSONArray(NotifcationNamespaces);
                                            JSONObject NamespaceObj = new JSONObject();
                                            NamespaceObj.put("namespaces", NotifcationArray);
                                            String SetNotify = JsonHandler.createCommandObj(3, "JSONRPC.SetNotificationStatus", NamespaceObj, _house.token);
                                            webSocket.send(SetNotify);
                                            MainActivity.currentActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    houseFrag.buildDashBoard();
                                                }
                                            });

                                        }
                                        catch (Exception e)
                                        {
                                            Log.e(TAG +"/5:loadDashboards",e.getMessage());
                                        }
                                        break;

                                    case 8:
                                        Toast.makeText(mContext,"Loading new things, this may take some time",Toast.LENGTH_LONG).show();
                                        String sAllTheThings = JsonHandler.getParamValue(jParams,"things");
                                        JSONArray jThings = new JSONArray(sAllTheThings);
                                       for(int i=0;i< jThings.length();i++){
                                           try {
                                               JSONObject jThing = jThings.getJSONObject(i);
                                               Thing newThing = new Thing();
                                               newThing.id = UUID.fromString(jThing.getString("id"));
                                               newThing.classId = UUID.fromString(jThing.getString("thingClassId"));
                                               newThing.name = jThing.getString("name");

                                               String strStates = JsonHandler.getParamValue(jThing, "states");
                                               JSONArray jStates = new JSONArray(strStates);
                                               for (int s = 0; s < jStates.length(); s++) {
                                                   JSONObject jState = jStates.getJSONObject(s);
                                                   State newState = new State();
                                                   newState.typeId = UUID.fromString(jState.getString("stateTypeId"));
                                                   newState.value = jState.getString("value");
                                                   newThing.states.add(newState);
                                               }
                                               _house.Things.add(newThing);
                                               JSONArray jClassIds = new JSONArray();
                                               jClassIds.put(UUID.fromString(jThing.getString("thingClassId")));

                                               JSONObject jClassObj = new JSONObject();
                                               jClassObj.put("thingClassIds", jClassIds);

                                               String getThingClass = JsonHandler.createCommandObj(21, "Integrations.GetThingClasses", jClassObj, _house.token);
                                               webSocket.send(getThingClass);

                                           }
                                           catch (Exception e){
                                               Log.e(TAG+"/8:thingAdd",e.getMessage());
                                           }
                                       }
                                        String[] Appload = {"appId:nymea-app", "group:dashboard-1", "key:dashboardConfig"};
                                       String _dashBoardRequest = JsonHandler.createCommand(5, "AppData.Load", Appload, _house.token);
                                       webSocket.send(_dashBoardRequest);
                                        break;

                                    case 21:
                                        String sThingClasses = JsonHandler.getParamValue(jParams,"thingClasses");
                                        JSONArray jClasses = new JSONArray(sThingClasses);
                                        for(int i =0;i<jClasses.length();i++){
                                            JSONObject jClassObj = jClasses.getJSONObject(i);
                                            String classid = jClassObj.getString("id");
                                            for(int t=0;t<_house.Things.size();t++) {
                                                if(_house.Things.get(t).classId.equals(UUID.fromString(classid))) {
                                                    _house.Things.get(t).classDisplayName = jClassObj.getString("displayName");
                                                    _house.Things.get(t).className = jClassObj.getString("name");


                                                    String strActions = JsonHandler.getParamValue(jClassObj, "actionTypes");
                                                    JSONArray jActionArr = new JSONArray(strActions);
                                                    for (int a = 0; a < jActionArr.length(); a++) {
                                                        JSONObject jAction = jActionArr.getJSONObject(a);
                                                        Action _currAction = new Action();

                                                        _currAction.displayName = jAction.getString("displayName");
                                                        _currAction.name = jAction.getString("name");
                                                        _currAction.typeId = UUID.fromString(jAction.getString("id"));
                                                        _house.Things.get(t).actions.add(_currAction);
                                                    }

                                                    String strStates = JsonHandler.getParamValue(jClassObj, "stateTypes");
                                                    JSONArray jStatesArr = new JSONArray(strStates);
                                                    for (int s = 0; s < jStatesArr.length(); s++) {
                                                        JSONObject jState = jStatesArr.getJSONObject(s);
                                                        UUID stateId = UUID.fromString(jState.getString("id"));
                                                        _house.Things.get(t).stateById(stateId).displayName =jState.getString("displayName");
                                                        _house.Things.get(t).stateById(stateId).name = jState.getString("name");
                                                    }
                                                }
                                            }

                                        }
                                        break;

                                }
                            }
                        }
                        else if(jResponse.has("notification")){
                            try {
                                String sNotifcation = jResponse.getString("notification");
                                if (sNotifcation.equals("Integrations.StateChanged")) {
                                    JSONObject jParams = jResponse.getJSONObject( "params");
                                    UUID thingID = UUID.fromString(jParams.getString("thingId"));
                                    UUID stateTypeID = UUID.fromString(jParams.getString( "stateTypeId"));
                                    String _value = jParams.getString("value");
                                    _house.getThingById(thingID).stateById(stateTypeID).setValue(_value);
                                    if(_house.getThingById(thingID).listener !=null)
                                    {
                                        _house.getThingById(thingID).listener.onStateValueChange(
                                                _house.getThingById(thingID).stateById(stateTypeID)
                                        );
                                    }
                                    Log.d(TAG+"/StateChange",_house.getThingById(thingID).name + " State: "+
                                            _house.getThingById(thingID).stateById(stateTypeID).displayName + " value:"+
                                            _value);
                                }
                            }
                            catch (Exception e){
                                Log.e(TAG+"/Notifcation",e.getMessage());
                            }
                        }
                        else if(jResponse.has("event"))
                        {

                        }
                    }
                    catch (Exception e){
                        Log.e(TAG+"/WebSocketMsg",e.getMessage());
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                }
            };
            client.newWebSocket(request,webSocketListener);
            client.dispatcher().executorService().shutdown();
        }
    }




}