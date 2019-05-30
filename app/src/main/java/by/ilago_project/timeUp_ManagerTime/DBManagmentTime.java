package by.ilago_project.timeUp_ManagerTime;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManagmentTime extends SQLiteOpenHelper {

    public static final String DB_NAME = "DBManagmentTime";
    public static final int DB_VERSION = 1;

    public static final String TABLENAME_TASK = "`Task`";
    public static final String TABLENAME_TAG = "`Tag`";
    public static final String TABLENAME_TASKTAG = "`Task_Tag`";
    public static final String TABLENAME_COUNTTASK = "`Series_count`";
    public static final String TABLENAME_HABIT = "`Habit`";
    public static final String TABLENAME_DAILY = "`Daily`";
    public static final String TABLENAME_GOAL = "`Goal`";
    public static final String TABLENAME_CHECKLISTTASK = "`Checklist_task`";
    public static final String TABLENAME_NOTIFYTASK = "`Notify_task`";
    public static final String TABLENAME_DATEDAILY = "`Daily_Date`";
    public static final String TABLENAME_HISTORYCOMPLETE = "`History_Complete`";



    public static final String CREATE_TABLE_TASK = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_TASK + "(" +
            "id integer primary key autoincrement," +
            "name text not null," +
            "description text," +
            "priority integer not null)";

    public static final String CREATE_TABLE_TAG = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_TAG + "(" +
            "id integer primary key autoincrement," +
            "name text not null)";

    public static final String CREATE_TABLE_TASK_TAG = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_TASKTAG + "(" +
            "idTask integer not null," +
            "idTag integer not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id),"+
            "foreign key(idTag) references "+ TABLENAME_TAG + "(id))";

    public static final String CREATE_TABLE_CHECKLIST = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_CHECKLISTTASK + "(" +
            "id integer primary key autoincrement," +
            "idTask integer not null," +
            "textCheckList text not null," +
            "isCheck integer not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_NOTIFYLIST = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_NOTIFYTASK + "(" +
            "id integer primary key autoincrement," +
            "idTask integer not null," +
            "titleNotify text not null," +
            "messageNotify text," +
            "timeNotify real not null," +
            "dateNotify real," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_SERIESCOUNT = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_COUNTTASK + "(" +
            "idTask integer primary key," +
            "countSeries integer not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_HABITS_TASK = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_HABIT + "(" +
            "idTask integer primary key," +
            "typeHabit integer not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_DAILY_TASK = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_DAILY + "(" +
            "idTask integer primary key," +
            "typeDaily integer not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_DATEDAILY = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_DATEDAILY + "(" +
            "idTask integer," +
            "Date real not null," +
            "every integer,"+
            "foreign key(idTask) references "+ TABLENAME_DAILY + "(idTask))";

    public static final String CREATE_TABLE_GOAL_TASK = "CREATE TABLE IF NOT EXISTS "+ TABLENAME_GOAL + "(" +
            "idTask integer primary key," +
            "startDate real not null," +
            "endDate real not null," +
            "foreign key(idTask) references "+ TABLENAME_TASK + "(id))";

    public static final String CREATE_TABLE_HISTORYCOMPLETETASK = "CREATE TABLE IF NOT EXISTS " + TABLENAME_HISTORYCOMPLETE + "(" +
            "idTask integer," +
            "TypeComplete integer,"+
            "DateComplete real,"+
            "foreign key(idTask) references " + TABLENAME_TASK + "(id))";

    public static final String CREATE_TRIGGER_INITTASK = "CREATE TRIGGER IF NOT EXISTS InitTask " +
            "AFTER INSERT ON "+ TABLENAME_TASK + " BEGIN " +
            " INSERT INTO "+ TABLENAME_COUNTTASK + "(idTask,countSeries) VALUES(new.id,0);" +
            "END";

    public static final String CREATE_TRIGGER_DELTASK = "CREATE TRIGGER IF NOT EXISTS DelTask" +
            " BEFORE DELETE ON "+ TABLENAME_TASK + " BEGIN" +
            " DELETE FROM "+ TABLENAME_COUNTTASK + " WHERE idTask = old.id;" +
            " DELETE FROM "+ TABLENAME_NOTIFYTASK + " WHERE idTask = old.id;"+
            " DELETE FROM "+ TABLENAME_CHECKLISTTASK + " WHERE idTask = old.id;"+
            " DELETE FROM "+ TABLENAME_TASKTAG + " WHERE idTask = old.id;" +
            " DELETE FROM "+ TABLENAME_DAILY + " WHERE idTask = old.id;"+
            " DELETE FROM "+ TABLENAME_GOAL + " WHERE idTask = old.id;"+
            " DELETE FROM "+ TABLENAME_HABIT + " WHERE idTask = old.id;"+
            "END";

    public static final String CREATE_TRIGGER_DELDAILY = "CREATE TRIGGER IF NOT EXISTS DelDaily" +
            " BEFORE DELETE ON "+ TABLENAME_TASK + " BEGIN" +
            " DELETE FROM "+ TABLENAME_DATEDAILY + " WHERE idTask = old.id;" +
            "END";

    public static final String CREATE_TRIGGER_DELTAG = "CREATE TRIGGER IF NOT EXISTS DelTag " +
            "BEFORE DELETE ON "+ TABLENAME_TAG + " BEGIN" +
            " DELETE FROM "+ TABLENAME_TASKTAG + " WHERE idTag = old.id;" +
            "END";


    public DBManagmentTime(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_TABLE_TAG);
            db.execSQL(CREATE_TABLE_TASK);
            db.execSQL(CREATE_TABLE_SERIESCOUNT);
            db.execSQL(CREATE_TABLE_TASK_TAG);
            db.execSQL(CREATE_TABLE_CHECKLIST);
            db.execSQL(CREATE_TABLE_NOTIFYLIST);
            db.execSQL(CREATE_TABLE_HABITS_TASK);
            db.execSQL(CREATE_TABLE_DAILY_TASK);
            db.execSQL(CREATE_TABLE_GOAL_TASK);
            db.execSQL(CREATE_TABLE_DATEDAILY);
            db.execSQL(CREATE_TABLE_HISTORYCOMPLETETASK);
            db.execSQL(CREATE_TRIGGER_DELTAG);
            db.execSQL(CREATE_TRIGGER_DELTASK);
            db.execSQL(CREATE_TRIGGER_DELDAILY);
            db.execSQL(CREATE_TRIGGER_INITTASK);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
