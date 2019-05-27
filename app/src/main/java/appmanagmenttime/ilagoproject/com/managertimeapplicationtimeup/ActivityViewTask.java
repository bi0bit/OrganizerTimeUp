package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import AssambleClassManagmentApp.AbsTask;
import AssambleClassManagmentApp.Daily;
import AssambleClassManagmentApp.Goal;
import AssambleClassManagmentApp.Habit;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.ViewerHeaderDailyBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.ViewerHeaderGoalBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.ViewerHeaderHabitBinding;


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
        getMenuInflater().inflate(R.menu.standart_menu_activity_viewer_task,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        updateTask();
        updateBinding();
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
        task = MainAppActivity.taskManager.getById(task.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editButton:
                Intent intent = new Intent(ActivityViewTask.this, ActivityViewEditorTask.class);
                intent.putExtra("update",true);
                intent.putExtra("object",task);
                startActivity(intent);
                return true;
            case R.id.deleteButton:
                AlertDialog.Builder dialog = QDialog.getBuilder()
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.eventDeleteTask))
                        .setMessage(String.format(getResources().getString(R.string.askDeleteTask), task.getName()))
                        .setPositiveBtnStr(R.string.app_delete)
                        .setPositiveBtn(this::deleteTask)
                        .buildDialog(this.findViewById(android.R.id.content),QDialog.DIALOG_QUATION);
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(DialogInterface dialog, int witch ){
        ManagerDB.getManagerDB(null).deleteTask(task.getId());
        dialog.dismiss();
        finish();
    }

    public void editTask(DialogInterface dialog, int witch ){
        dialog.dismiss();
    }

}
