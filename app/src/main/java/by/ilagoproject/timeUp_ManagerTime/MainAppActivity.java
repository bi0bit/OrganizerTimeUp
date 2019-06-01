package by.ilagoproject.timeUp_ManagerTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import AssambleClassManagmentApp.AbsTask;
import AssambleClassManagmentApp.Daily;
import AssambleClassManagmentApp.Goal;
import AssambleClassManagmentApp.Habit;
import AssambleClassManagmentApp.TagManager;
import AssambleClassManagmentApp.TaskManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class MainAppActivity extends AppCompatActivity implements ManagerDB.HandlerUpdateTaskInDb, ManagerDB.HandlerUpdateTagInDb{

    private AbsTask.Type_Task selectTypeTask;
    ManagerDB dbManager;
    static TaskManager taskManager;
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
        DBManagmentTime db = new DBManagmentTime(this);
        ManagerDB.getManagerDB(db);
        TagManager.initTags();
        dbManager = ManagerDB.getManagerDB(new DBManagmentTime(this));
        dbManager.setHandlerUpdateTagInDb(this);
        TagManager.initTags();
        TagManager.update();
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
        startViewActivity(createNewTask(), ActivityViewEditorTask.class));
        listView.setOnItemClickListener((parent, view, position, id) ->
        startViewActivity((AbsTask) listView.getAdapter().getItem(position), ActivityViewTask.class));
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
    }

    protected void startViewActivity(AbsTask obj, Class cls){
        Intent intent = new Intent(MainAppActivity.this, cls);
        intent.putExtra("object", obj);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addTag:
                 addTag();
                break;
            case R.id.tags:
                 allTag();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void renameTag(final int idTag){
        QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
        QDialog.Builder builder = QDialog.getBuilder();
        String name = TagManager.getStringTag(idTag);
        builder.setTitle(getResources().getString(R.string.eventRenameTag))
                .setCancelable(true)
                .setSetterGetterDialog(dialogSG)
                .setOnClickPositiveBtn((dialog, which)->{
                    if(!dialogSG.getUserInputString().isEmpty()) {
                        String nameTag = dialogSG.getUserInputString();
                        TagManager.rename(idTag, nameTag);
                    }else Toast.makeText(this,R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
        AlertDialog.Builder alertDialog = QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_INPUT_STRING);
        dialogSG.setLabelString(getResources().getString(R.string.fieldNameTag));
        dialogSG.setUserInputString(name);
        alertDialog.show();
    }

    private void deleteTag(final int idTag){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setCancelable(true)
                .setTitle(getResources().getString(R.string.eventDeleteTag))
                .setMessage(String.format(getResources().getString(R.string.askDeleteTag), TagManager.getStringTag(idTag)))
                .setNegativeBtnStr(R.string.dialog_CANCEL)
                .setPositiveBtnStr(R.string.app_delete)
                .setOnClickNegativeBtn(QDialog.STANDARD_ONCLICK_BUTTON)
                .setOnClickPositiveBtn((dialog,which)->{
                    TagManager.deleteTag(idTag);
                    dialog.dismiss();
                });
        QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_QUATION).show();
    }

    private void changeTag(final int idTag){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setCancelable(true)
                .setTitle(getResources().getString(R.string.eventChangeTag))
                .setMessage(getResources().getString(R.string.askChangeTag))
                .setNegativeBtnStr(R.string.app_delete)
                .setPositiveBtnStr(R.string.app_rename)
                .setNeutralBtnStr(R.string.dialog_CANCEL)
                .setOnClickNeutralBtn(QDialog.STANDARD_ONCLICK_BUTTON)
                .setOnClickNegativeBtn((dialog,which)->{
                    deleteTag(idTag);
                    dialog.dismiss();
                })
                .setOnClickPositiveBtn((dialog,which)->{
                    renameTag(idTag);
                    dialog.dismiss();
                });
        QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_QUATION).show();
    }

    private void allTag(){
        String[] strings  = TagManager.getListNameTag().toArray(new String[0]);
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(getResources().getString(R.string.tags))
               .setCancelable(true)
                .setItems(strings)
                .setOnClickItem((dialog, which)->{
                    int idTag = TagManager.getListIdTag().get(which);
                   changeTag(idTag);
                });
        QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_LIST).show();
    }

    private void addTag(){
        QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(getResources().getString(R.string.eventAddTag))
                .setCancelable(true)
                .setSetterGetterDialog(dialogSG)
                .setOnClickPositiveBtn((dialog, which)->{
                    if(!dialogSG.getUserInputString().isEmpty()) {
                        String nameTag = dialogSG.getUserInputString();
                        TagManager.addTag(nameTag);
                    }else Toast.makeText(this,R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
        AlertDialog.Builder alertDialog = QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_INPUT_STRING);
        dialogSG.setLabelString(getResources().getString(R.string.fieldEnterNameUnderTask));
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standard_menu_activity_task, menu);
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

    public AbsTask createNewTask() {
        AbsTask returnObject = null;
        switch (selectTypeTask){
            case HABIT:
                returnObject = new Habit(0);
                break;
            case DAILY:
                returnObject = new Daily(0);
                break;
            case GOAL:
                returnObject = new Goal(0);
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
        updateDailyList();
        updateGoalList();
        updateHabitList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
