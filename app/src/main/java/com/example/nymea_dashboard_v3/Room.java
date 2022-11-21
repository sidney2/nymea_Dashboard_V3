package com.example.nymea_dashboard_v3;

import com.example.nymea_dashboard_v3.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
    public int mID;
    public String mhostId;
    public String mName;
    public String mIcon;
    public int mRoomType = 0;//0= normal room 1 =power sockets only 2= light switches only 3=mirror

    public List<UUID> ThingIds = new ArrayList<>();

    public int getID(){return mID;}
    public String getHostId(){return mhostId;}
    public String getName(){return mName;}
    public String getIcon(){return mIcon;}
    public int getRoomType(){return mRoomType;}


    public void setID(int id){this.mID =id;}
    public void setHostId(String hostId){this.mhostId =hostId;}
    public void setName(String name){this.mName = name;}
    public void setIcon(String icon){this.mIcon = icon;}
    public void setRoomType(int RoomType){this.mRoomType =RoomType;}



    private static Room instance =new Room();

    public static Room getInstance(){
        return instance;
    }

    public static Room setInstance(Room room){
       Room.instance = room;
       return Room.instance;
    }


    /*public Thing getThingById(UUID thingId)
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


    public Thing getThingByClassId(UUID classId)
    {
        for(int t=0; t< Things.size() ; t++)
        {
            if(Things.get(t).classId.equals(classId))
            {
                return Things.get(t);
            }
        }
        return null;
    }

    public Thing getDirtyThingByClassId(UUID classId){
        for(int t=0;t<Things.size();t++){
            if(Things.get(t).classId.equals(classId) && Things.get(t).isDirty){
                return Things.get(t);
            }
        }
        return null;
    }

    public boolean thingInRoom(UUID thingId){
        for(int t=0;t<Things.size();t++){
            if(Things.get(t).id.equals(thingId)){
                return true;
            }
        }
        return false;
    }

    public Thing getThingByStateId(UUID stateTYpeID)
    {
        for(int t=0; t < Things.size(); t++)
        {
            for(int s=0; s< Things.get(t).states.size(); s++) {
                boolean isMatch = Things.get(t).states.get(s).typeId.equals(stateTYpeID);

                if (isMatch) {
                    return Things.get(t);
                }
            }
        }
        return null;
    }

public Thing getThingByName(String _name){
        for(int t=0;t<Things.size();t++){
            if(Things.get(t).classDisplayName.toUpperCase().equals(_name.toUpperCase())){
                return Things.get(t);
            }
        }
        return null;
}
public Boolean hasThing(String _classdisplayname){
    for(int t=0;t<Things.size();t++){
        if(Things.get(t).classDisplayName.toUpperCase().equals(_classdisplayname.toUpperCase())){
            return true;
        }
    }
    return false;
}
    public State getStateById(UUID typeId)
    {
        for(int t =0;t<Things.size(); t++)
        {

          for(int s=0; s< Things.get(t).states.size(); s++) {
            boolean isMatch = Things.get(t).states.get(s).typeId.equals(typeId);
            if (isMatch) {
                return Things.get(t).states.get(s);
            }
          }
        }
        return null;
    }

    public void updateStateById(UUID typeId,String value)
    {
        for( Thing _currThing:Things){
            for(State _currState : _currThing.states){
                if(_currState.typeId.equals(typeId)&& !_currState.update)
                {
                    _currState.setValue(value);
                    return;
                }
            }
        }

    }


    public State getStateByName(String name)
    {
        for( int t =0; t< Things.size(); t++)
        {

            for(int s=0; s< Things.get(t).states.size(); s++) {
                boolean isMatch = Things.get(t).states.get(s).name.toUpperCase().equals(name.toUpperCase());
                if (isMatch) {
                    return Things.get(t).states.get(s);
                }
            }
        }
        return  null;
    }

    public Action getActionById(UUID typeId)
    {
        for (int t=0; t<Things.size(); t++)
        {

            return Things.get(t).actionById(typeId);
        }
        return null;
    }

    public Action getActionByName(String name)
    {
        for(int t=0; t< Things.size(); t++)
        {
            return Things.get(t).actionByName(name);
        }
        return null;
    }*/
}
