package com.example.nymea_dashboard_v3;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonHandler {
    private static final String TAG = JsonHandler.class.getSimpleName();

    public static JSONObject processJsonResponse(String s) throws JSONException
    {
        JSONObject jsonobj = null;
        try
        {
            jsonobj = new JSONObject(s);
        }
        catch (JSONException e)
        {
            Log.e(TAG,e.getLocalizedMessage());
        }
        return jsonobj;
    }

    public static JSONObject getParamValues(JSONObject jObj,String ParamToFind){
        JSONObject foundObj = null;
        try
        {
            Iterator<?> Keys = jObj.keys();
            while(Keys.hasNext()){
                String _curr = Keys.next().toString();
                if(_curr.toUpperCase().contains(ParamToFind.toUpperCase())){
                    foundObj = new JSONObject();
                    foundObj.put(_curr,jObj.get(_curr));
                    break;
                }
            }
        }
        catch (Exception e){Log.e(TAG,e.getLocalizedMessage());}
        return foundObj;
    }

    public static String getParamValue(JSONObject jObj,String ParamsToFind)
    {
        String keyValue ="";
        try
        {
            Iterator<?> Keys = jObj.keys();
            while(Keys.hasNext()){
                String key = Keys.next().toString();
                if(key.toUpperCase().contains(ParamsToFind.toUpperCase().toString())){
                    keyValue = jObj.getString(key);
                    break;
                }
            }
        }
        catch (Exception e){Log.e(TAG,e.getMessage());}
        return keyValue;
    }
    public static String createCommand(int id,String method,String[] params, String token){
        String _retval ="";
        try
        {
            JSONObject jObj = new JSONObject();
            jObj.put("id",id);
            jObj.put("method",method);
            JSONObject paramObj = new JSONObject();
            for(String s: params){
                String[] key = s.split(":");
                if(key.length ==2){
                    paramObj.put(key[0],key[1]);
                }
            }
            jObj.put("params",paramObj);
            if(token !=""){jObj.put("token",token);}
            _retval = jObj.toString() ;
        }
        catch (Exception e){Log.e(TAG,e.getMessage());}
        return _retval;
    }

    public static String createCommandObj(int id,String method,JSONObject params,String token){
        String _retval ="";
        try{
                JSONObject jObj = new JSONObject();
                jObj.put("id",id);
                jObj.put("method",method);
                jObj.put("params",params);
                jObj.put("token",token);
                _retval = jObj.toString() + "\n";
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        return _retval;
    }
}
