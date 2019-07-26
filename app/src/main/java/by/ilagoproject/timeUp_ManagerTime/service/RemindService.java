package by.ilagoproject.timeUp_ManagerTime.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import AssambleClassManagmentTime.AbsTask;
import AssambleClassManagmentTime.Filtering.BuilderFilter;
import AssambleClassManagmentTime.Filtering.FilterSetting;
import AssambleClassManagmentTime.NotificationTask;
import AssambleClassManagmentTime.Sorting.SortByIdTask;
import AssambleClassManagmentTime.TagManager;
import AssambleClassManagmentTime.TaskManager;
import by.ilagoproject.timeUp_ManagerTime.ActivityViewTask;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;
import by.ilagoproject.timeUp_ManagerTime.R;

public class RemindService extends Service {

    private final int INTERVAL = 60_000; // 1 min
    private final int INTERVAL_TRASHCLEAR = 300_000; //5 min

    NotificationManager notifyManager;
    TaskManager taskManager;
    static Timer timer;
    static Timer timerClearTrash;
    TimerTask taskTimer;
    Calendar calendar;

    static List<Integer> notifyTrash = new ArrayList<>();


    @Override
    public void onCreate() {
//        Toast.makeText(this, " i m work", Toast.LENGTH_LONG).show();
        Log.d("Services", "Create RemindService");
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        calendar = Calendar.getInstance();
        taskManager = TaskManager.getInstance(this);
        taskManager.initTask();
        if(timer == null){
            timer = new Timer();
        }
        if(timerClearTrash == null) {
            timerClearTrash = new Timer();
        }
        timerClearTrash.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("RemindService","clear trash");
                notifyTrash.clear();
            }
        }, INTERVAL_TRASHCLEAR, INTERVAL_TRASHCLEAR);
        if(ManagerDB.getManagerDB(null).getHandlerUpdateTaskInDb() == null)
            ManagerDB.getManagerDB(null).setHandlerUpdateTaskInDb(taskManager);
        TagManager.initTags();
        TagManager.update();
        scanRemind();
        super.onCreate();
    }


    void scanRemind(){
        if(taskTimer != null) taskTimer.cancel();
        taskTimer =  new TimerTask() {
            @Override
            public void run() {
                Log.d("RemindService","schedule");
                Log.d("RemindService", "trash hash:"+notifyTrash.hashCode());
                Log.d("RemindService", "trash size:"+notifyTrash.size());
                calendar.setTimeInMillis(System.currentTimeMillis());
                FilterSetting setting = new FilterSetting(null,null,true,true);
                List<AbsTask> tasks = taskManager.getAllTask(BuilderFilter.buildFilter(setting), new SortByIdTask());
                Log.d("RemindService",String.valueOf(tasks.size()));
                for(AbsTask task : tasks){
                    List<NotificationTask> notificationTasks = task.getListNotify();
                    for(NotificationTask notify : notificationTasks){
                        if(!notifyTrash.contains(notify.getId())) {
                            Calendar time = Calendar.getInstance();
                            time.setTimeInMillis(notify.getTimeAlarm());
                            Calendar date = Calendar.getInstance();
                            date.setTimeInMillis(notify.getDateAlarm());
                            if (task.TYPE != AbsTask.Type_Task.GOAL) {
                                if (time.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY) &&
                                        time.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)) {
                                    Log.d("RemindService", "notify");
                                    notifyTrash.add(notify.getId());
                                    sendNotif(task, notify);
                                }
                            } else {
                                if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                                        date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                                        date.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                                        time.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY) &&
                                        time.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)) {
                                    Log.d("RemindService", "notify");
                                    notifyTrash.add(notify.getId());
                                    sendNotif(task, notify);
                                }
                            }
                        }
                    }
                }
            }
        };
        timer.schedule(taskTimer, 0, INTERVAL);
    }

    void sendNotif(AbsTask task, NotificationTask notifyTask){
        Context context = this;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round, options);
        notifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(this, ActivityViewTask.class);
        notifyIntent.putExtra("object", task);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
// Since android Oreo notification channel is needed.
        String channelId = "100";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notifyManager.createNotificationChannel(channel);
        }
        Notification.Builder notifyBuilder = new Notification.Builder(context);
        notifyBuilder.setContentIntent(contentIntent)
                .setTicker(task.getName() + ": " + notifyTask.getTitle())
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(task.getName() + ": "+notifyTask.getTitle())
                .setContentText(notifyTask.getMessage())
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(android.R.drawable.btn_star)
                .setLargeIcon(bitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyBuilder.setChannelId(channelId);
        }
        Notification notification = notifyBuilder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        notifyManager.notify(notifyTask.getId(),notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Services", "StartCommand RemindService");
        scanRemind();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Services", "Destroy RemindService");
        if(timer != null) timer.cancel();
        super.onDestroy();
    }

}
