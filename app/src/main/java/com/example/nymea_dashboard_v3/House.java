package com.example.nymea_dashboard_v3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class House {

    private static volatile House instance =null;
    public House(){}
    public static House getInstance(){
        if(instance ==null){
            instance = new House();
        }
        return instance;
    }

    String hostAddress;
    String hostId;
    String token;
    String hostName;
    String defaultRoom;

    boolean isConnected =false;

    public List<Room> rooms = new ArrayList<>();
    public List<Thing>Things = new ArrayList<>();

    public List<UUID> HouseThings= new ArrayList<>();

    public String getHostId() {
        return hostId;
    }
    public String getToken() {
        return token;
    }
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public boolean getisConnected(){return isConnected;}
    public void setIsConnected(boolean _isConnected){this.isConnected = _isConnected;}

    public String getHostname(){return hostName;}
    public void setHostName(String hostName){this.hostName = hostName;}

    public Thing getThingById(UUID thingId)
    {
        for(int t=0; t< Things.size() ; t++)
        {
            if(Things.get(t).id.equals(thingId))
            {
                return Things.get(t);
            }
        }
        return null;
    }

   public List<UUID>getListOfThingsByClassDisplayName(String classdisplayname){
        List<UUID> foundThings =new ArrayList<>();
        for(Thing thing:Things)
        {
            if(thing.classDisplayName !=null) {
                if (thing.classDisplayName.toUpperCase().equals(classdisplayname.toUpperCase())) {
                    foundThings.add(thing.id);
                }
            }
        }
        return foundThings;
   }

   public Thing getThingByName(String name)
   {
       for(Thing thing:Things){
           if(thing.name.toUpperCase().equals(name.toUpperCase())){
               return thing;
           }
       }
       return null;
   }
}
