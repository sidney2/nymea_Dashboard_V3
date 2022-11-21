package com.example.nymea_dashboard_v3;

import java.text.SimpleDateFormat;
import java.util.Date;

public class eventItem {
    private String calId;
    private String eventId;
    private String eventTitle;
    private Date eventStartDate;
    private Long eventStart;
    private Date eventEndDate;
    private Long eventEnd;
    private String eventReccuring;
    private String eventAllDay;
    private String eventColour;
    private String eventRDate;

    public String getCalId() {
        return calId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public Long getEventStart() {
        return eventStart;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public Long getEventEnd() {
        return eventEnd;
    }

    public String getEventReccuring() {
        return eventReccuring;
    }

    public String getEventAllDay() {
        return eventAllDay;
    }

    public String getEventColour() {
        return eventColour;
    }

    public String getEventRDate(){return  eventRDate;}

    public void setCalId(String calId) {
        this.calId = calId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventStart(Long eventStart) {
        this.eventStart = eventStart;
        this.eventStartDate =new Date();
        this.eventStartDate.setTime(eventStart);
    }

    public void setEventEnd(Long eventEnd) {
        this.eventEnd = eventEnd;
        this.eventEndDate = new Date();
        this.eventEndDate.setTime(eventEnd);
    }

    public void setEventReccuring(String eventReccuring) {
        this.eventReccuring = eventReccuring;
    }

    public void setEventAllDay(String eventAllDay) {
        this.eventAllDay = eventAllDay;
    }

    public void setEventColour(String eventColour) {
        this.eventColour = eventColour;
    }

public void setEventRDate(String rdate){
        Date d = new Date();
        d.setTime(Long.parseLong(rdate));
    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd");
        this.eventRDate =sdf.format(d);}
}
