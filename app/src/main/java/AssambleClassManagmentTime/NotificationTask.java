package AssambleClassManagmentTime;

import java.io.Serializable;

public class NotificationTask implements Serializable {


    private Integer id;


    private String title;
    private String message;
    private long timeAlarm;
    private long dateAlarm;

    public NotificationTask(long dateAlarm, long time){
        this.timeAlarm = time;
        this.dateAlarm = dateAlarm;
        this.title = "";
        this.message = "";
    }

    public NotificationTask(int id, String title, String message ,long dateAlarm, long time){
        this.id = id;
        this.title = title;
        this.message = message;
        this.timeAlarm = time;
        this.dateAlarm = dateAlarm;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeAlarm(long timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public void setDateAlarm(long dateAlarm) {
        this.dateAlarm = dateAlarm;
    }

    public void setId(int id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimeAlarm() {
        return timeAlarm;
    }

    public long getDateAlarm() {
        return dateAlarm;
    }

}
