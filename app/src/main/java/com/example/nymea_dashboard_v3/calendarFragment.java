package com.example.nymea_dashboard_v3;

import android.content.ContentUris;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class calendarFragment extends Fragment {

   ArrayList<eventItem> calItems = new ArrayList<>();
    static final String TAG = calendarFragment.class.getSimpleName();

   static RecyclerView.Adapter mCalAdapter;

    public calendarFragment() {
        // Required empty public constructor
    }


    public static calendarFragment newInstance() {
        calendarFragment fragment = new calendarFragment();

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

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rcCalendar = view.findViewById(R.id.rcCalendar);
        rcCalendar.setHasFixedSize(false);
        LinearLayoutManager calLinearLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rcCalendar.setLayoutManager(calLinearLayout);
        mCalAdapter = new calendarAdapter();
        rcCalendar.setAdapter(mCalAdapter);
        initialiseCalendar();
    }

    public void initialiseCalendar(){

        try {
             calItems.clear();
            Calendar calInstances = Calendar.getInstance();
            Calendar startDay = Calendar.getInstance();
            startDay.set(calInstances.get(Calendar.YEAR),calInstances.get(Calendar.MONTH),calInstances.get(Calendar.DATE),01,01);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
            Log.d(TAG+"/CalStartDate",sdf.format(startDay.getTime()));
            Calendar endDay = Calendar.getInstance();
            endDay.set(calInstances.get(Calendar.YEAR),calInstances.get(Calendar.MONTH),calInstances.get(Calendar.DATE),23,59);
            endDay.add(Calendar.DAY_OF_WEEK,6);
            Log.d(TAG+"/CalEndDate",sdf.format(endDay.getTime()));

            String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,
                    CalendarContract.Events.RRULE, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DISPLAY_COLOR,CalendarContract.Instances.BEGIN};

            Uri.Builder eventsBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            ContentUris.appendId(eventsBuilder, startDay.getTimeInMillis());
            ContentUris.appendId(eventsBuilder,endDay.getTimeInMillis());


            Cursor mCursor = getContext().getContentResolver().query(eventsBuilder.build(), projection, null, null, CalendarContract.Instances.BEGIN + " ASC");
            if (mCursor != null && mCursor.getCount() > 0 && mCursor.moveToFirst()) {
                do {
                    eventItem evtItem = new eventItem();
                    evtItem.setCalId(mCursor.getString(0));
                    evtItem.setEventId(mCursor.getString(1));
                    evtItem.setEventTitle(mCursor.getString(2));
                    evtItem.setEventStart(mCursor.getLong(3));
                    evtItem.setEventEnd(mCursor.getLong(4));
                    evtItem.setEventReccuring(mCursor.getString(5));
                    evtItem.setEventAllDay(mCursor.getString(6));
                    evtItem.setEventColour(mCursor.getString(7));
                    evtItem.setEventRDate(mCursor.getString(8));
                    calItems.add(evtItem);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCalAdapter.notifyDataSetChanged();
                        }
                    });

                } while (mCursor.moveToNext());


            }mCursor.close();

            int interval = 300000;
            Handler handler =new Handler();
            Runnable runnable =new Runnable() {
                @Override
                public void run() {
                    initialiseCalendar();
                }
            };
            handler.postAtTime(runnable,System.currentTimeMillis()+interval);
            handler.postDelayed(runnable,interval);
        }
        catch (Exception e){
            Log.e(TAG+"/initialiseCalendar",e.getMessage());
        }
    }


 public class calendarAdapter extends RecyclerView.Adapter<calendarAdapter.ViewHolder>{


     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View v = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.cal_item,parent,false);
         return new ViewHolder(v);
     }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            eventItem calItem = calItems.get(position);
            Log.d(TAG+"/calBinder",String.valueOf(position) + " "+ calItem.getEventTitle());

         int screenSz = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
         if (screenSz >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
             ViewGroup.LayoutParams vLayoutParams = holder.calItemLayout.getLayoutParams();
             vLayoutParams.width=500;

            holder.calItemLayout.setLayoutParams(vLayoutParams);
         }
         else
         {
            ViewGroup.LayoutParams vLayoutParams = holder.calItemLayout.getLayoutParams();
             vLayoutParams.width=300;

             holder.calItemLayout.setLayoutParams(vLayoutParams);
         }
            if(calItem.getEventAllDay().equals("1")) {
                holder.tvCalText.setText(calItem.getEventTitle());
                holder.tvCalDate.setText(getDayDate(calItem.getEventStartDate()));
            }
            else
            {
                if(calItem.getEventReccuring() == null){
                    StringBuilder sb = new StringBuilder();
                    String s = getTime(calItem.getEventStartDate());
                    String ed = getTime(calItem.getEventEndDate());
                    if(!s.equals("1:00 am")) {
                        sb.append(s);
                    }
                    if(!ed.equals("1:00 am"))
                    {
                        sb.append( " - " +ed);
                    }
                    sb.append(" "+ calItem.getEventTitle());
                    holder.tvCalText.setText(sb.toString());
                    holder.tvCalDate.setText(getDayDate(calItem.getEventStartDate()));
                }
                else
                {
                    holder.tvCalDate.setText(calItem.getEventRDate());
                    holder.tvCalText.setText(calItem.getEventTitle());
                }

            }
         Drawable calEntryBackgound = holder.calItemLayout.getBackground();
         ((GradientDrawable)calEntryBackgound).setColor(CalendarUtils.getDisplayColor(Integer.parseInt(calItem.getEventColour())));

     }

     @Override
     public int getItemCount() {
         return calItems.size();
     }

     class ViewHolder extends RecyclerView.ViewHolder{

            final TextView tvCalDate;
            final TextView tvCalText;
            final ConstraintLayout calItemLayout;
            ViewHolder(View v){
                super(v);
                tvCalDate = (TextView) v.findViewById(R.id.tvCalDate);
                tvCalText =(TextView) v.findViewById(R.id.tvCalEvent);
                calItemLayout= (ConstraintLayout)v.findViewById(R.id.cal_itemLayout);
            }
        }
 }



    private String getTime(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(date).toLowerCase();
    }

    private Date getDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyy");
            return sdf.parse(sdf.format(date));
        } catch (Exception e) {
            Log.e(TAG + ":dateformat", e.getMessage());
        }
        return null;
    }

    private String getDayDate(Date date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd");
            return sdf.format(date);
        }
        catch (Exception e)
        {
            Log.e(TAG +"/dayDate",e.getMessage());
        }
        return "";
    }
}