package by.ilagoproject.timeUp_ManagerTime.broadreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import AssambleClassManagmentTime.AbsTask;
import AssambleClassManagmentTime.Daily;
import AssambleClassManagmentTime.Filtering.BuilderFilter;
import AssambleClassManagmentTime.Filtering.FilterSetting;
import AssambleClassManagmentTime.Habit;
import AssambleClassManagmentTime.Sorting.SortByIdTask;
import AssambleClassManagmentTime.TagManager;
import AssambleClassManagmentTime.TaskManager;
import by.ilagoproject.timeUp_ManagerTime.DBManagmentTime;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;

import static AssambleClassManagmentTime.AbsTask.resetTime;
import static by.ilagoproject.timeUp_ManagerTime.ManagerDB.UPDATE;
import static by.ilagoproject.timeUp_ManagerTime.ManagerDB.getManagerDB;

public class StartNewDate extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("StartDayReceiver", "NEW DAY");
        ManagerDB dbM = getManagerDB(new DBManagmentTime(context));
        TagManager.initTags();
        TagManager.update();
        TaskManager tM = TaskManager.getInstance(context);
        tM.initTask();
        Calendar calendarNow = Calendar.getInstance();
        resetTime(calendarNow);
        Calendar calendarPre = Calendar.getInstance();
        calendarPre.add(Calendar.DATE, -1);
        resetTime(calendarPre);
        FilterSetting setting = new FilterSetting(null, null, true, true,  calendarPre.getTimeInMillis());
        List<AbsTask> tasks = tM.getAllTask(BuilderFilter.buildFilter(setting), new SortByIdTask());
        Log.d("StartDayReceiver", "pre date: "+calendarPre.toString());
        for(AbsTask task : tasks){
            if(task.TYPE == AbsTask.Type_Task.GOAL) continue;
            if(task.TYPE != AbsTask.Type_Task.HABIT) {
                Daily daily = (Daily) task;
                Log.d("StartDayReceiver", task.getName() + " count:"+task.getCountSeries() +" ");
                //crutches
                if(daily.getTypeDaily() == Daily.Type_Daily.EVERMONTH){
                    if(calendarNow.get(Calendar.MONTH) != calendarPre.get(Calendar.MONTH)){
                        dbM.resetCheck(task.getId());
                        int increment = (task.getCountSeries() == 0) ? 0 : 1;
                        dbM.completeTaskNonHandler(task.getId(), task.getCountSeries() - increment, calendarPre.getTimeInMillis(), AbsTask.Type_Complete.NO_COMPLETE);
                        dbM.incrementTaskCountSeriesDb(task.getId(), -increment);
                        task.setCountSeries(task.getCountSeries() - increment);
                    }
                    else return;
                }
                else if(daily.getTypeDaily() == Daily.Type_Daily.EVERYEAR){
                    if(calendarNow.get(Calendar.YEAR) != calendarPre.get(Calendar.YEAR)){
                        dbM.resetCheck(task.getId());
                        int increment = (task.getCountSeries() == 0) ? 0 : 1;
                        dbM.completeTaskNonHandler(task.getId(), task.getCountSeries() - increment, calendarPre.getTimeInMillis(), AbsTask.Type_Complete.NO_COMPLETE);
                        dbM.incrementTaskCountSeriesDb(task.getId(), -increment);
                        task.setCountSeries(task.getCountSeries() - increment);
                    }
                    else return;
                }
                else {
                    dbM.resetCheck(task.getId());
                    int increment = (task.getCountSeries() == 0) ? 0 : 1;
                    dbM.completeTaskNonHandler(task.getId(), task.getCountSeries() - increment, calendarPre.getTimeInMillis(), AbsTask.Type_Complete.NO_COMPLETE);
                    dbM.incrementTaskCountSeriesDb(task.getId(), -increment);
                    task.setCountSeries(task.getCountSeries() - increment);
                }
            }
            else{
                Log.d("StartDayReceiver", task.getName() + " count:"+task.getCountSeries() +" ");
                int countSeries =  Math.abs(task.getCountSeries());
                dbM.completeTaskNonHandler(task.getId(), countSeries, calendarPre.getTimeInMillis(), (countSeries > 0)? AbsTask.Type_Complete.COMPLETE : AbsTask.Type_Complete.NO_COMPLETE);
                dbM.updateTaskCountSeriesDb(task.getId(), 0);
                task.setCountSeries(0);
            }
        }
        tM.notifyHandler(UPDATE);
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
