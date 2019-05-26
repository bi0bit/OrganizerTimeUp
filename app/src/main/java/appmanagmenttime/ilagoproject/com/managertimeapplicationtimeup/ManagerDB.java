package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import AssambleClassManagmentApp.AbsTask;

import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_CHECKLISTTASK;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_COUNTTASK;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_DAILY;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_GOAL;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_HABIT;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_TAG;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_TASK;
import static appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.DBManagmentTime.TABLENAME_TASKTAG;

/**
 * <pre>ManagerDB to managment data base, remove, additional, update for table db, so it return cursor
 * at table. Singleton
 * Purpose: provide easy remove, additional, update, and other function for action datebase.
 * </pre>
 */
public final class ManagerDB {

    private static volatile ManagerDB managerDB;
    private HandlerUpdateTaskInDb handlerUpdateTaskInDb;
    private HandlerUpdateTagInDb handlerUpdateTagInDb;

    private SQLiteDatabase dbR;
    private SQLiteDatabase dbW;

    public static final int DELETE = 0b01;
    public static final int UPDATE = 0b10;

    public static final String ID_COLUMN = "Id";
    public static final String IDTASK_COLUMN = "IdTask";
    public static final String IDTAG_COLUMN = "IdTag";
    public static final String NAME_COLUMN = "Name";
    public static final String TASKDESCRIPTION_COLUMNNAME = "Description";
    public static final String TASKCOUNT_COLUMNNAME = "CountSeries";
    public static final String TASKPRIORITY_COLUMNNAME = "Priority";
    public static final String HABITTYPE_COLUMNNAME = "Type_Habit";
    public static final String DAILYTYPE_COLUMNNAME = "Type_Daily";
    public static final String DAILYFROMTIME_COLUMNNAME = "FromDate";
    public static final String DAILYTOTIME_COLUMNNAME = "ToDate";
    public static final String GOALSTARTDATE_COLUMNNAME = "StartDate";
    public static final String GOALENDDATE_COLUMNNAME = "EndDate";
    public static final String CHECKLISTTEXT_COLUMNNAME = "UnderTaskText";

    /**
     * select all task
     */
    public static final String SEL_STRING_GETTASK = "SELECT t1.id AS " + ID_COLUMN + ", t1.name AS " + NAME_COLUMN +
            ", t1.description AS " + TASKDESCRIPTION_COLUMNNAME + ", t1.priority AS " + TASKPRIORITY_COLUMNNAME + ", t2.countSeries AS " + TASKCOUNT_COLUMNNAME + " FROM " + TABLENAME_TASK + " t1 " +
            "INNER JOIN " + TABLENAME_COUNTTASK + " t2 ON t1.id = t2.idTask";

    /**
     * <pre>select task by id, mask:
     *      1 ? - id task which will be select</pre>
     */
    public static final String SEL_STRING_GETTASKBYID = "SELECT t1.id AS " + ID_COLUMN + ", t1.name AS " + NAME_COLUMN +
            ", t1.description AS " + TASKDESCRIPTION_COLUMNNAME + ", t2.countSeries AS " + TASKCOUNT_COLUMNNAME + " FROM " + TABLENAME_TASK + " t1 " +
            "INNER JOIN " + TABLENAME_COUNTTASK + " t2 ON t1.id = t2.idTask WHERE t1.id = ?";


    /**
     * select task-habit
     */
    public static final String SEL_STRING_GETHABIT = "SELECT t1.id AS " + ID_COLUMN + ", t1.name AS " + NAME_COLUMN + ", t1.description AS " + TASKDESCRIPTION_COLUMNNAME +
            ", t1.priority AS " + TASKPRIORITY_COLUMNNAME + ", t2.typeHabit AS " + HABITTYPE_COLUMNNAME + ", t3.countSeries AS " + TASKCOUNT_COLUMNNAME + " FROM " + TABLENAME_TASK + " t1 " +
            "INNER JOIN " + TABLENAME_HABIT + " t2 ON t1.id = t2.idTask " +
            "INNER JOIN " + TABLENAME_COUNTTASK + " t3 ON t1.id = t3.idTask";

    /**
     * select task-daily
     */
    public static final String SEL_STRING_GETDAILY = "SELECT t1.id AS " + ID_COLUMN + ", t1.name AS " + NAME_COLUMN + ", t1.description AS " + TASKDESCRIPTION_COLUMNNAME +
            ", t1.priority AS " + TASKPRIORITY_COLUMNNAME + ", t2.typeDaily AS " + DAILYTYPE_COLUMNNAME +
            ", t3.countSeries AS " + TASKCOUNT_COLUMNNAME + " FROM " + TABLENAME_TASK + " t1 " +
            "INNER JOIN " + TABLENAME_DAILY + " t2 ON t1.id = t2.idTask " +
            "INNER JOIN " + TABLENAME_COUNTTASK + " t3 ON t1.id = t3.idTask";

    /**
     * select task-goal
     */
    public static final String SEL_STRING_GETGOAL = "SELECT t1.id AS " + ID_COLUMN + ", t1.name AS " + NAME_COLUMN + ", t1.description AS " + TASKDESCRIPTION_COLUMNNAME +
            ", t1.priority AS " + TASKPRIORITY_COLUMNNAME +
            ", t2.startDate AS " + GOALSTARTDATE_COLUMNNAME  +
            ", t2.endDate AS " + GOALENDDATE_COLUMNNAME +
            ", t3.countSeries AS " + TASKCOUNT_COLUMNNAME +
            " FROM " + TABLENAME_TASK + " t1 " +
            "INNER JOIN " + TABLENAME_GOAL + " t2 ON t1.id = t2.idTask " +
            "INNER JOIN " + TABLENAME_COUNTTASK + " t3 ON t1.id = t3.idTask";

    /**
     * select all tag
     */
    public static final String SEL_STRING_GETTAG = "SELECT id AS " + ID_COLUMN + ", name AS " + NAME_COLUMN + " FROM " + TABLENAME_TAG;

    /**
     * select tag task by id task
     */
    public static final String SEL_STRING_GETTAG_TASK = "SELECT t1.idTag AS "+IDTAG_COLUMN+",t2.name AS "+NAME_COLUMN+" FROM " + TABLENAME_TASKTAG + " t1 " +
            "INNER JOIN " + TABLENAME_TAG + " t2 ON t1.idTag = t2.id WHERE t1.idTask = ?";

    /**
     * <pre>command task: 1 ? - collect id tag for where</pre>
     */
    public static final String SEL_STRING_GETTAG_TASKBYID = "SELECT t1.idTask AS "+IDTASK_COLUMN+",t1.idTag AS "+IDTAG_COLUMN+",t2.name  AS " + NAME_COLUMN +
            " FROM " + TABLENAME_TASKTAG + " t1 " +
            "INNER JOIN " + TABLENAME_TAG + " t2 ON t1.idTag = t2.id WHERE t1.idTag IN (?)";

    /**
     * <pre>command task: 1 ? - id task(int) for where</pre>
     */
    public static final String SEL_STRING_CHECKLISTBYTASK = "SELECT id AS "+ID_COLUMN+", idTask AS "+IDTASK_COLUMN+
            ", textCheckList AS "+CHECKLISTTEXT_COLUMNNAME+" FROM "+ TABLENAME_CHECKLISTTASK + " WHERE idTask = ?";

    /**
     * <pre>command task: 1 ? - id checkbox() for where</pre>
     */
    public static final String SEL_STRING_CHECKLIST = "SELECT id AS "+ID_COLUMN+", idTask AS "+IDTASK_COLUMN+
            ", textCheckList AS "+CHECKLISTTEXT_COLUMNNAME+" FROM "+ TABLENAME_CHECKLISTTASK + " WHERE id = ?";
    /**
     * <pre>command task: 1 ? - name tag(text)</pre>
     */
    public static final String INSERT_STRING_TAG = "INSERT INTO " + TABLENAME_TAG + "(name) VALUES (?)";

    /**
     * <pre>command task: 1 ? - id task(int) foreign key
     *                     2 ? - id tag(int) foreign key</pre>
     */
    public static final String INSERT_STRING_TASKTAG = "INSERT INTO " + TABLENAME_TASKTAG + "(idTask,idTag) VALUES (?,?)";

    /**
     * <pre>command task: 1 ? - idTask(int) foreign key references Task
     *                     2 ? - text checkbox (text)</pre>
     */
    public static final String INSERT_STRING_CHECKLIST = "INSERT INTO "+ TABLENAME_CHECKLISTTASK + "(idTask,textCheckList) VALUES (?,?)";

    /**
     * <pre>command task: 1 ? - name(text)
     *                     2 ? - description(text)
     *                     3 ? - priority(int)</pre>
     */
    public static final String INSERT_STRING_TASK = "INSERT INTO " + TABLENAME_TASK + "(name,description,priority) VALUES(?,?,?)";

    /**
     * <pre>command task: 1 ? - idTask(int)
     *                     2 ? - typeHabits(int)</pre>
     */
    public static final String INSERT_STRING_HABIT = "INSERT INTO "+ TABLENAME_HABIT + "(idTask,typeHabit) VALUES(?,?)";

    /**
     * <pre>command task: 1 ? - idTask(int)
     *                     2 ? - typeDaily(int)
     *                     3 ? - fromTime(text)
     *                     4 ? - toTime(text)</pre>
     */
    public static final String INSERT_STRING_DAILY = "INSERT INTO "+ TABLENAME_DAILY + "(idTask,typeDaily, " +
            "fromTime, toTime) VALUES(?,?,?,?)";

    /**
     * <pre>command task: 1 ? - idTask(int)
     *                     2 ? - startDate(text)
     *                     3 ? - endDate(text)</pre>
     */
    public static final String INSERT_STRING_GOAL = "INSERT INTO "+ TABLENAME_GOAL + "(idTask,startDate,endDate) VALUES(?,?,?)";
    public static final String INSERT_STRING_CALLPUSH = "INSERT INTO";

    /**
     * <pre>command mask:  1 ? - field name(text)
     *                 2 ? - description(text)
     *                 3 ? - priority (int)
     *                 4 ? - id(int) key for where</pre>
     */
    public static final String UPDATE_STRING_TASK = "UPDATE " + TABLENAME_TASK + " SET name = ?, description = ?," +
            "priority = ? WHERE id = ?";

    /**
     * <pre>command mask: 1 ? - typeHabit(int)
     *                     2 ? - idTask(id) key for where</pre>
     */
    public static final String UPDATE_STRING_HABIT = "UPDATE "+ TABLENAME_HABIT + " SET typeHabit = ? WHERE idTask = ?";

    /**
     * <pre>command mask: 1 ? - typeDaily(int)
     *                     2 ? - fromTime(text)
     *                     3 ? - toTime(text)
     *                     4 ? - idTask(id) key for where</pre>
     */
    public static final String UPDATE_STRING_DAILY = "UPDATE "+ TABLENAME_DAILY + " SET typeDaily = ?, " +
            "fromTime = ?, toTime = ? WHERE idTask = ?";

    /**
     * <pre>command mask: 1 ? - startDate(text)
     *                     2 ? - endDate(text)
     *                     3 ? - idTask(id) key for where</pre>
     */
    public static final String UPDATE_STRING_GOAL = "UPDATE "+ TABLENAME_GOAL + " SET startDate = ?, endDate = ? WHERE idTask = ?";

    /**
     * <pre>command mask: 1 ? - countSeries(int)
     *                     2 ? - idTask(int)</pre>
     */
    public static final String UPDATE_STRING_COUNTSERIES = "UPDATE "+ TABLENAME_COUNTTASK +
            "SET countSeries = ? WHERE idTask = ?";

    public static final String UPDATE_STRING_PRIORITYTASK = "UPDATE " + TABLENAME_TASK +
            "SET priority = ? WHERE id = ?";

    public static final String UPDATE_STRING_TYPEHABIT = "UPDATE "+ TABLENAME_HABIT + " SET typeHabit = ?" +
            "WHERE idTask = ?";
    public static final String UPDATE_STRING_TYPEDAILY = "UPDATE "+ TABLENAME_DAILY + " SET typeDaily = ?" +
            "WHERE idTask = ?";

    /**
     * <pre>command mask: 1 ? - name(text)
     *                2 ? - id(int) key for where</pre>
     */
    public static final String UPDATE_STRING_CHECKLIST = "UPDATE "+ TABLENAME_CHECKLISTTASK + " SET name = ? WHERE id = ?";

    /**
     * command mask: 1 ? - idTask(int) key for where
     */
    public static final String DEL_STRING_CHECKLISTBYTASK = "DELETE FROM "+ TABLENAME_CHECKLISTTASK + " WHERE idTask = ?";

    /**
     * command mask: 1 ? - id(int) key for where
     */
    public static final String DEL_STRING_CHECKLIST = "DELETE FROM "+ TABLENAME_CHECKLISTTASK + " WHERE id = ?";

    /**
     * command mask: 1 ? - id(int) key for where
     */
    public static final String DEL_STRING_TASK = "DELETE FROM" + TABLENAME_TASK + " WHERE id =?";

    /**
     * command mask: 1 ? - id(int) key for where
     */
    public static final String DEL_STRING_TAG = "DELETE FROM" + TABLENAME_TAG + " WHERE id = ?";

    /**
     * <pre>delete row in task_tag by field idTask
     *  command mask: 1 ? - idTask(int) key for where</pre>
     */
    public static final String DEL_STRING_TASKTAGBYTASK = "DELETE FROM" + TABLENAME_TASKTAG + " WHERE idTask = ?";

    /**
     * <pre>delete row in task_tag by field idTag
     *  command mask: 1 ? - idTag(int) key for where</pre>
     */
    public static final String DEL_STRING_TASKTAGBYTAG = "DELETE FROM" + TABLENAME_TASKTAG + " WHERE idTag = ?";

    /**
     * <pre>delete row in task_tag by two field, idTask and idTag
     *      command mask: 1 ? - idTask(int) for where
     *                    2 ? - idTag(int) for where</pre>
     */
    public static final String DEL_STRING_TASKTAG = "DELETE FROM" + TABLENAME_TASKTAG +
            "WHERE idTask = ? AND idTag = ?";

    private ManagerDB(DBManagmentTime managmentDb) {
        dbR = managmentDb.getReadableDatabase();
        dbW = managmentDb.getWritableDatabase();
    }

    public static ManagerDB getManagerDB(DBManagmentTime db) {
        if (managerDB == null) {
            synchronized (ManagerDB.class) {
                if (managerDB == null)
                    managerDB = new ManagerDB(db);
            }
        }
        return managerDB;
    }

    public SQLiteDatabase getDbReadable() {
        return dbR;
    }

    public SQLiteDatabase getDbWriteble() {
        return dbW;
    }

    public HandlerUpdateTaskInDb getHandlerUpdateTaskInDb() {
        return handlerUpdateTaskInDb;
    }

    public void setHandlerUpdateTaskInDb(HandlerUpdateTaskInDb handlerUpdateTaskInDb) {
        this.handlerUpdateTaskInDb = handlerUpdateTaskInDb;
    }

    public HandlerUpdateTagInDb getHandlerUpdateTagInDb() {
        return handlerUpdateTagInDb;
    }

    public void setHandlerUpdateTagInDb(HandlerUpdateTagInDb handlerUpdateTagInDb) {
        this.handlerUpdateTagInDb = handlerUpdateTagInDb;
    }


    public Cursor getCursorGoal() {
        return dbR.rawQuery(SEL_STRING_GETGOAL, null);
    }

    public Cursor getCursorHabit() {
        return dbR.rawQuery(SEL_STRING_GETHABIT, null);
    }

    public Cursor getCursorDaily() {
        return dbR.rawQuery(SEL_STRING_GETDAILY, null);
    }

    public Cursor getCursorTag() {
        return dbR.rawQuery(SEL_STRING_GETTAG, null);
    }

    public Cursor getCursorTagByTask(int idTask) {
        return dbR.rawQuery(SEL_STRING_GETTAG_TASK,
                new String[]{String.valueOf(idTask)});
    }

    public Cursor getCursorTaskByTag(int... idTag) {
        StringBuilder tag = new StringBuilder();
        for (int i : idTag) {
            tag.append((tag.length() == 0) ? i : "," + i);
        }
        return dbR.rawQuery(SEL_STRING_GETTAG_TASKBYID,
                new String[]{tag.toString()});
    }


    /**
     * <pre>
     *     Add new row in Task
     * </pre>
     *
     * @param Tag name Tag
     */
    public void addTag(String Tag) {
        dbW.execSQL(INSERT_STRING_TAG,
                new String[]{Tag});
        notifyTagHandler(UPDATE);
    }

    /**
     * <pre>
     *     Delete row from table Task by id
     * </pre>
     *
     * @param idTask id task
     */
    public void deleteTask(int idTask) {
        dbW.execSQL(DEL_STRING_TASK, new String[]{String.valueOf(idTask)});
        notifyHandler(DELETE);
    }

    /**
     * <pre>
     *     Delete row from table Tag by id
     * </pre>
     *
     * @param idTag id task
     */
    public void deleteTag(int idTag) {
        dbW.execSQL(DEL_STRING_TAG, new String[]{String.valueOf(idTag)});
        notifyTagHandler(DELETE);
    }

    /**
     * <pre>
     *     Delete row from table Task_Tag by id task and idtag
     * </pre>
     *
     * @param idTask id task
     * @param idTag  id tag
     */
    public void deleteTaskTag(int idTask, int idTag) {
        dbW.execSQL(DEL_STRING_TASKTAG, new String[]{String.valueOf(idTask), String.valueOf(idTag)});
    }

    /**
     * insert record in table tag_task
     * @param task
     */
    public void initTagTaskInDb(AbsTask task) {
        HashMap<Integer, String> tags = task.getTags();
        try {
            dbW.beginTransaction();
            for (int key : tags.keySet()) {
                dbW.execSQL(INSERT_STRING_TASKTAG, new String[]{
                        String.valueOf(task.getId()), String.valueOf(key)});
            }
            dbW.setTransactionSuccessful();
        } finally {
            dbW.endTransaction();
        }
    }

    public void updateTagTaskInDb(AbsTask task) {
        try {
            dbW.beginTransaction();
            dbW.execSQL(DEL_STRING_TASKTAGBYTASK, new String[]{String.valueOf(task.getId())});
            initTagTaskInDb(task);
            dbW.setTransactionSuccessful();
        } finally {
            dbW.endTransaction();
        }
    }

    /**
     * <pre>Add new row in Task</pre>
     *
     * @param task Task which add in table
     */
    public void addTask(AbsTask task) {
        InitialTaskInDb initialTaskInDb = task::initialTaskInDB;
        initialTaskInDb.addTask();
        initTagTaskInDb(task);
        notifyHandler(UPDATE);
    }

    /**
     * <pre>
     *     Update row in table Task by id
     * </pre>
     *
     * @param task object AbsTask for update
     */
    public void updateTask(AbsTask task) {
        UpdateTaskInDb updateTaskInDb = task::updateInDb;
        updateTaskInDb.update();
        updateTagTaskInDb(task);
        notifyHandler(UPDATE);
    }

    public void notifyHandler(int flag){
        handlerUpdateTaskInDb.notifyChange(flag);
    }

    public void notifyTagHandler(int flag){ handlerUpdateTagInDb.notifyChangeTag(flag);}

    public void incrementTaskCountSeriesDb(final int idTask, final int increment){
        Cursor c = dbR.rawQuery(SEL_STRING_GETTASKBYID,new String[]{String.valueOf(idTask)});
        c.moveToFirst();
        int i = c.getInt(c.getColumnIndex(TASKCOUNT_COLUMNNAME));
        dbW.execSQL(UPDATE_STRING_COUNTSERIES, new String[]{String.valueOf(i+increment), String.valueOf(idTask)});
    }

    public void updateTaskCountSeriesDb(final int idTask, final int count){
        Cursor c = dbR.rawQuery(SEL_STRING_GETTASKBYID,new String[]{String.valueOf(idTask)});
        c.moveToFirst();
        dbW.execSQL(UPDATE_STRING_COUNTSERIES, new String[]{String.valueOf(count), String.valueOf(idTask)});
    }

    public interface HandlerUpdateTaskInDb {
        void notifyChange(int flag);
    }

    public interface HandlerUpdateTagInDb {
        void notifyChangeTag(int flag);
    }

    public interface InitialTaskInDb {
        void addTask();
    }

    public interface UpdateTaskInDb {
        void update();
    }
}
