package com.example.nymea_dashboard_v3;

import android.util.Log;

import com.example.nymea_dashboard_v3.JsonHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Thing {
    static final public String TAG = "nymea-app: Thing";
    public UUID id;
    public String name;
    public String className;
    public String classDisplayName;
    public boolean thingFound =false;
    public List interfaces = new ArrayList<State>();
    public boolean isDirty =true;
    public UUID classId;
    public boolean isPresent = false;
    public boolean stateUpdate = false;

    public ArrayList<State> states = new ArrayList<State>();
    public ArrayList<Action> actions = new ArrayList<Action>();

   public stateChangeListener listener;

    public Thing(){
        this.listener = null;
    }


    public void setOnStateChangeListener(stateChangeListener listener){
        this.listener = listener;
    }
    public static List<State> getStatesFromJSON(JSONObject ting)
    {
        List<State> states = new ArrayList<>();
        try
        {
            String strStates = JsonHandler.getParamValue(ting,"states");
            JSONArray statarr = new JSONArray(strStates);
            for(int s=0; s< statarr.length(); s++)
            {
                JSONObject jState = statarr.getJSONObject(s);
                State newState = new State();
                newState.typeId = UUID.fromString(jState.getString("stateTypeId"));

                newState.value = jState.getString("value");
                states.add(newState);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
        }

        return states;
    }

    public State stateByName(String name) {
        for (int i = 0; i < states.size(); i++) {

                if (states.get(i).name.toUpperCase().equals(name.toUpperCase())) {
                    return states.get(i);
                }

        }
        return null;
    }

    public void updateState(UUID stateTypeId, String stateValue)
    {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).typeId.equals(stateTypeId)) {
                 states.get(i).setValue(stateValue);
                 break;
            }
        }
    }
    public State stateById(UUID stateTypeId) {
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).typeId.equals(stateTypeId)) {
                return states.get(i);
            }
        }
        return null;
    }

    public Action actionByName(String name) {
        for (int i = 0; i < actions.size(); i++) {
            Log.d(TAG, "Thing has action: " + actions.get(i).name);
            if (actions.get(i).name.equals(name)) {
                return actions.get(i);
            }
        }
        return null;
    }

    public Action actionById(UUID actionTypeId) {
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).typeId.equals(actionTypeId)) {
                return actions.get(i);
            }
        }
        return null;
    }

    public interface stateChangeListener{

        public void onStateValueChange(State state);
    }
}

