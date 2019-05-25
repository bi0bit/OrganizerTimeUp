package AssambleClassManagmentApp;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.ManagerDB;

public class TaskManager implements ManagerDB.HandlerUpdateTaskInDb {

    private Hashtable<Integer,AbsTask> tasks = new Hashtable<>();
    private ManagerDB dbM;


    private ManagerDB.HandlerUpdateTaskInDb handlerUpdateTaskInDb;
    FilterTask filter;


    public TaskManager(){
        dbM = ManagerDB.getManagerDB(null);
    }


    public void initTask(){
        initHabit();
        initDaily();
        initGoal();
    }

    public void initDaily(){
        Cursor c = dbM.getCursorDaily();
        while(c.moveToNext()){
            Daily daily = (Daily) Daily.initTaskByCursor(c);
            tasks.put(daily.getId(),daily);
        }
    }

    public void initHabit(){
        Cursor c = dbM.getCursorHabit();
        while(c.moveToNext()){
            Habit habit = (Habit) Habit.initTaskByCursor(c);
            tasks.put(habit.getId(),habit);
        }
    }

    public void initGoal(){
        Cursor c = dbM.getCursorGoal();
        while(c.moveToNext()){
            Goal goal = (Goal) Goal.initTaskByCursor(c);
            tasks.put(goal.getId(),goal);
        }
    }

    public void setHandlerUpdateTaskInDb(ManagerDB.HandlerUpdateTaskInDb handlerUpdateTaskInDb) {
        this.handlerUpdateTaskInDb = handlerUpdateTaskInDb;
    }

    public List<AbsTask> getHabit(){
        List<AbsTask> habits = new ArrayList<>();
        for(AbsTask task : tasks.values()){
            if(task.TYPE == AbsTask.Type_Task.HABIT)
                habits.add(task);
        }
        return habits;
    }

    public List<AbsTask> getDaily(){
        List<AbsTask> dailies = new ArrayList<>();
        for(AbsTask task : tasks.values()){
            if(task.TYPE == AbsTask.Type_Task.DAILY)
                dailies.add(task);
        }

        return dailies;
    }

    public List<AbsTask> getGoal(){
        List<AbsTask> goals = new ArrayList<>();
        for(AbsTask task : tasks.values()){
            if(task.TYPE == AbsTask.Type_Task.GOAL)
                goals.add(task);
        }
        return goals;
    }

    @Override
    public void notifyChange(int flag) {
        if((flag & ManagerDB.DELETE) == ManagerDB.DELETE){
            tasks.clear();
        }
        initTask();
        notifyHandler(flag);
    }

    public void notifyHandler(int flag){
        handlerUpdateTaskInDb.notifyChange(flag);
    }
}
