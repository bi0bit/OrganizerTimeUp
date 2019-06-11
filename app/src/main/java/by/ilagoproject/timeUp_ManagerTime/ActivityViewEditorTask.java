package by.ilagoproject.timeUp_ManagerTime;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import AssambleClassManagmentTime.AbsTask;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityViewEditorTask extends AppCompatActivity {


    boolean update;
    AbsTask object;
    ViewDataBinding binding;
    @BindView(R.id.viewNameTask) EditText editName;
    @BindView(R.id.viewDescriptionTask) EditText editDesc;
    @BindView(R.id.spinnerTypePriority) Spinner spnPriority;
    @Nullable @BindView(R.id.spinnerTypeHabit) Spinner spnTypeHabit;
    @Nullable @BindView(R.id.spinnerTypeDaily) Spinner spnTypeDaily;

    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        update = getIntent().getBooleanExtra("update",false);
        object = getIntent().getParcelableExtra("object");
        initViewActivity(object);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.invalidateAll();
        object.setEditorTask(findViewById(android.R.id.content));
    }

    protected void initViewActivity(AbsTask object){
        binding = object.createBindingEditorHeader(this);
        setContentView(R.layout.activity_editor_task);
        LinearLayout l = findViewById(R.id.HeaderViewTask);
        l.addView(binding.getRoot());
        object.setEditorTask(findViewById(android.R.id.content));
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
    }

    protected void saveChangeTask(AbsTask object){
        if (update){
            ManagerDB.getManagerDB(null).updateTask(object);
        }
        else {
            ManagerDB.getManagerDB(null).addTask(object);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.standard_menu_activity_editor_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name = editName.getText().toString();
        switch (item.getItemId()){
            case R.id.saveButton:
                if (name.isEmpty()){
                    Toast.makeText(this,R.string.eventEmptyField, Toast.LENGTH_LONG).show();
                    return super.onOptionsItemSelected(item);
                }
                saveChangeTask(object);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
