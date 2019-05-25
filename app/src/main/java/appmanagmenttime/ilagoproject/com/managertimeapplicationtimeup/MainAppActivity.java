package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import AssambleClassManagmentApp.AbsTask;
import AssambleClassManagmentApp.Daily;
import AssambleClassManagmentApp.Goal;
import AssambleClassManagmentApp.Habit;
import AssambleClassManagmentApp.TagManager;
import AssambleClassManagmentApp.TaskManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class MainAppActivity extends AppCompatActivity implements ManagerDB.HandlerUpdateTaskInDb, ManagerDB.HandlerUpdateTagInDb{

    private AbsTask.Type_Task selectTypeTask;
    ManagerDB dbManager;
    TaskManager taskManager;
    ListView listView;
    TaskAdapterList dailyList;
    TaskAdapterList goalList;
    TaskAdapterList habitList;


    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ManagerDB.getManagerDB(new DBManagmentTime(this));
        TagManager.initTags();
        dbManager = ManagerDB.getManagerDB(new DBManagmentTime(this));
        dbManager.setHandlerUpdateTagInDb(this);
        taskManager = new TaskManager();
        dbManager.setHandlerUpdateTaskInDb(taskManager);
        taskManager.setHandlerUpdateTaskInDb(this);
        taskManager.initTask();
        listView = findViewById(R.id.listTask);
        dailyList = new TaskAdapterList(this,taskManager.getDaily());
        goalList = new TaskAdapterList(this,taskManager.getGoal());
        habitList = new TaskAdapterList(this,taskManager.getHabit());
        setSelectTypeTask(AbsTask.Type_Task.HABIT);
        Button btn = findViewById(R.id.btnAddTask);
        btn.setOnClickListener((v) ->
           /*!!!*/     startViewActivity(createNewTask(), ActivityViewEditorTask.class));
        listView.setOnItemClickListener((parent, view, position, id) ->
          /*!!!*/      startViewActivity((AbsTask) listView.getAdapter().getItem(position), ActivityViewTask.class));
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
    }

    //!!!
    protected void startViewActivity(AbsTask obj, Class cls){
        Intent intent = new Intent(MainAppActivity.this, cls);
        intent.putExtra("object", obj);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standart_menu_activity_task, menu);
        Spinner spinner = (Spinner) menu.findItem(R.id.spinnerTypeTask).getActionView();
        ArrayAdapter adapter =
                ArrayAdapter.createFromResource(this,R.array.strings_type_task,R.layout.item_spinner_type_task);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_type_task);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setSelectTypeTask(AbsTask.Type_Task.HABIT);
                        break;
                    case 1:
                        setSelectTypeTask(AbsTask.Type_Task.DAILY);
                        break;
                    case 2:
                        setSelectTypeTask(AbsTask.Type_Task.GOAL);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    public void setSelectTypeTask(AbsTask.Type_Task selectTypeTask) {
        this.selectTypeTask = selectTypeTask;//?
        switch (this.selectTypeTask) {
            case GOAL:
                listView.setAdapter(goalList);
                break;
            case DAILY:
                listView.setAdapter(dailyList);
                break;
            case HABIT:
                listView.setAdapter(habitList);
                break;
        }
    }

    //!!!
    public void createNewTask(AbsTask.Type_Task type) {
        switch (type){
            case HABIT:
                habitList.add(new Habit(1));
                break;
            case DAILY:
                dailyList.add(new Daily(1));
                break;
            case GOAL:
                goalList.add(new Goal(1));
                break;
        }
    }

    public AbsTask createNewTask() {
        AbsTask returnObject = null;
        switch (selectTypeTask){
            case HABIT:
                returnObject = new Habit(1);
                break;
            case DAILY:
                returnObject = new Daily(1);
                break;
            case GOAL:
                returnObject = new Goal(1);
                break;
        }
        return returnObject;
    }

    public void updateDailyList(){
        dailyList.clear();
        dailyList.addAll(taskManager.getDaily());
        dailyList.notifyDataSetChanged();
    }
    public void updateGoalList(){
        goalList.clear();
        goalList.addAll(taskManager.getGoal());
        goalList.notifyDataSetChanged();
    }
    public void updateHabitList(){
        habitList.clear();
        habitList.addAll(taskManager.getHabit());
        habitList.notifyDataSetChanged();
    }

    @Override
    public void notifyChange(int flag) {
        updateDailyList();
        updateGoalList();
        updateHabitList();
    }

    @Override
    public void notifyChangeTag(int flag) {
        if((flag & ManagerDB.DELETE) == ManagerDB.DELETE)
            TagManager.getTags().clear();
        TagManager.update();
    }

    public static void setListViewHeightBasedOnChildren (ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
