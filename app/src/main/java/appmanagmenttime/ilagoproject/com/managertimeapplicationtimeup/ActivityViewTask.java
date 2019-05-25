package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import AssambleClassManagmentApp.AbsTask;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;


public class ActivityViewTask extends AppCompatActivity {

    AbsTask task;

    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = getIntent().getParcelableExtra("object");
        ViewDataBinding binding = task.createBindingViewerHeader(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editButton:
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
        dialog.dismiss();
    }

    public void editTask(DialogInterface dialog, int witch ){
        dialog.dismiss();
    }

}