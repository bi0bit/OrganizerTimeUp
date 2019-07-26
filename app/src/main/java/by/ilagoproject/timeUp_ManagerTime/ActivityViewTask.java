package by.ilagoproject.timeUp_ManagerTime;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import AssambleClassManagmentTime.AbsTask;
import AssambleClassManagmentTime.Daily;
import AssambleClassManagmentTime.Goal;
import AssambleClassManagmentTime.Habit;
import AssambleClassManagmentTime.TaskManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;

import java.util.Calendar;

import by.ilagoproject.timeUp_ManagerTime.databinding.ViewerHeaderDailyBinding;
import by.ilagoproject.timeUp_ManagerTime.databinding.ViewerHeaderGoalBinding;
import by.ilagoproject.timeUp_ManagerTime.databinding.ViewerHeaderHabitBinding;

import static AssambleClassManagmentTime.AbsTask.resetTime;


public class ActivityViewTask extends AppCompatActivity {

    AbsTask task;
    ViewDataBinding binding;

    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = getIntent().getParcelableExtra("object");
        binding = task.createBindingViewerHeader(this);
        setContentView(R.layout.activity_view_task);
        LinearLayout l = findViewById(R.id.HeaderViewTask);
        l.addView(binding.getRoot());
        task.setViewerTask(l.getRootView());
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standard_menu_activity_viewer_task,menu);
        if(task.isComplete()){
            menu.findItem(R.id.completeButton).setTitle(R.string.app_toNonComplete);
            menu.findItem(R.id.completeButton).setIcon(R.drawable.v_on_no_complete);
        }
        else {
            menu.findItem(R.id.completeButton).setTitle(R.string.app_toComplete);
            menu.findItem(R.id.completeButton).setIcon(R.drawable.v_tick);
        }

        if(task.TYPE == AbsTask.Type_Task.GOAL)
            menu.findItem(R.id.statisticButton).setVisible(false);
        if(task.TYPE == AbsTask.Type_Task.HABIT)
            menu.findItem(R.id.completeButton).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        updateTask();
        updateBinding();
        task.getBuilderView().setObject(task);
        task.setViewerTask(findViewById(android.R.id.content));
        super.onResume();
    }

    protected void updateBinding(){
        if(task instanceof Habit){
            ((ViewerHeaderHabitBinding)binding).setTask((Habit) task);
        }
        else if(task instanceof Daily){
            ((ViewerHeaderDailyBinding)binding).setTask((Daily)task);
        }
        else{
            ((ViewerHeaderGoalBinding)binding).setTask((Goal) task);
        }
    }

    protected void updateTask(){
        task = TaskManager.getInstance(this).getById(task.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.editButton:
                intent = new Intent(ActivityViewTask.this, ActivityViewEditorTask.class);
                intent.putExtra("update",true);
                intent.putExtra("object",task);
                startActivity(intent);
                return true;
            case R.id.deleteButton:
                //delete button
                AlertDialog.Builder dialog = QDialog.getBuilder()
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.eventDeleteTask))
                        .setMessage(String.format(getResources().getString(R.string.askDeleteTask), task.getName()))
                        .setPositiveBtnStr(R.string.app_delete)
                        .setOnClickPositiveBtn(this::deleteTask)
                        .buildDialog(this.findViewById(android.R.id.content),QDialog.DIALOG_QUATION);
                dialog.show();
                return true;
            case R.id.statisticButton:
                intent = new Intent(ActivityViewTask.this, StatisticActivity.class);
                intent.putExtra("object", task);
                startActivity(intent);
                return true;
            case R.id.completeButton:
                // complete task ev
                if(task.isActual()) {
                    Calendar calendar = Calendar.getInstance();
                    resetTime(calendar);
                    long date = calendar.getTimeInMillis();
                    if (!task.isComplete()) {
                        ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(task.getId(), 1);
                        ManagerDB.getManagerDB(null).completeTask(task.getId(), task.getCountSeries() + 1, date, AbsTask.Type_Complete.COMPLETE);
                        task.setCountSeries(task.getCountSeries()+1);
                        item.setTitle(R.string.app_toNonComplete);
                        item.setIcon(R.drawable.v_on_no_complete);
                    }
                    else {
                        int increment = (task.getCountSeries() == 0) ? 0 : -1;
                        ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(task.getId(), increment);
                        task.setCountSeries(task.getCountSeries()+increment);
                        if (task.TYPE == AbsTask.Type_Task.GOAL)
                            ManagerDB.getManagerDB(null).uncompleteTask(task.getId());
                        else ManagerDB.getManagerDB(null).uncompleteTask(task.getId(), date);
                        item.setTitle(R.string.app_toComplete);
                        item.setIcon(R.drawable.v_tick);
                    }
                    task.getBuilderView().setCountItem(this.findViewById(android.R.id.content),task);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(DialogInterface dialog, int witch ){
        ManagerDB.getManagerDB(null).deleteTask(task.getId());
        dialog.dismiss();
        finish();
    }

}
