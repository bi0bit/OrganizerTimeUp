package by.ilagoproject.timeUp_ManagerTime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import AssambleClassManagmentTime.AbsTask;
import AssambleClassManagmentTime.Daily;
import AssambleClassManagmentTime.Filtering.BuilderFilter;
import AssambleClassManagmentTime.Filtering.FilterSetting;
import AssambleClassManagmentTime.Goal;
import AssambleClassManagmentTime.Habit;
import AssambleClassManagmentTime.Sorting.SortByIdTask;
import AssambleClassManagmentTime.Sorting.SortByName;
import AssambleClassManagmentTime.Sorting.SortByPriority;
import AssambleClassManagmentTime.Sorting.Sorter;
import AssambleClassManagmentTime.TagManager;
import AssambleClassManagmentTime.TaskManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import by.ilagoproject.timeUp_ManagerTime.service.RemindService;

public class MainAppActivity extends AppCompatActivity implements ManagerDB.HandlerUpdateTaskInDb, ManagerDB.HandlerUpdateTagInDb{

    private AbsTask.Type_Task selectTypeTask;
    ManagerDB dbManager;
    static TaskManager taskManager;
    ListView listView;
    TaskAdapterList dailyList;
    TaskAdapterList goalList;
    TaskAdapterList habitList;
    FilterSetting filterSetting;

    public final static String SHARED_PREFERENCE_NAME = "SETTING";
    public final static String SHARED_PREFERENCE_SHORTMODE = "short_mode";

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
        taskManager = TaskManager.getInstance(this);
        taskManager.initTask();
        dbManager.setHandlerUpdateTaskInDb(taskManager);
        taskManager.setHandlerUpdateTaskInDb(this);
        listView = findViewById(R.id.listTask);
        filterSetting = new FilterSetting(null,null,false,false);
        dailyList = new TaskAdapterList(this,taskManager.getTaskByType(AbsTask.Type_Task.DAILY));
        goalList = new TaskAdapterList(this,taskManager.getTaskByType(AbsTask.Type_Task.GOAL));
        habitList = new TaskAdapterList(this,taskManager.getTaskByType(AbsTask.Type_Task.HABIT));
        taskManager.setFilter(BuilderFilter.buildFilter(filterSetting));
        setSelectTypeTask(AbsTask.Type_Task.HABIT);
        Button btn = findViewById(R.id.btnAddTask);
        btn.setOnClickListener((v) ->
            startViewActivity(createNewTask(), ActivityViewEditorTask.class));
        listView.setOnItemClickListener((parent, view, position, id) ->
            startViewActivity((AbsTask) listView.getAdapter().getItem(position), ActivityViewTask.class));
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        Intent intent = new Intent(this, RemindService.class);
        startService(intent);
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
                 addTagShow();
                break;
            case R.id.tags:
                 allTagShow();
                break;
            case R.id.sortBy:
                sortByShow();
                break;
            case R.id.filter:
                filterShow();
                break;
            case R.id.shortMode:
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor e = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE).edit();
                e.putBoolean(SHARED_PREFERENCE_SHORTMODE, item.isChecked());
                e.apply();
                updateDailyList();
                updateGoalList();
                updateHabitList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByShow(){
        QDialog.Builder builder = QDialog.getBuilder();
        String[] sortType = getResources().getStringArray(R.array.sortingBy);
        builder.setCancelable(true)
                .setItems(sortType)
                .setOnClickItem((dialog, which) -> {
                    Sorter sort = (which == 0)? new SortByIdTask() :
                            (which == 1) ? new SortByName() : new SortByPriority();
                    taskManager.setSorter(sort);
                    dialog.dismiss();
                });
        QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_LIST).show();
    }

    private void filterShow(){
        AtomicBoolean isActual = new AtomicBoolean(filterSetting.actual);
        AtomicBoolean isNoCompleted = new AtomicBoolean(filterSetting.nonComplete);
        EnumSet<AbsTask.Priority_Task> prioritySet =
                (filterSetting.priority_tasks != null)? filterSetting.priority_tasks : EnumSet.noneOf(AbsTask.Priority_Task.class);
        List<Integer> intTags = (filterSetting.tags!=null)? filterSetting.tags : new ArrayList<>();

        QDialog.Builder builder = QDialog.getBuilder();
        QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
        dialogSG.setValuesId(new int[]{
                R.id.switchActual,
                R.id.switchNoComplete,
                R.id.switchPriority,
                R.id.expandablePriority,
                R.id.listPriority,
                R.id.switchTags,
                R.id.expandableTags,
                R.id.listTags
        });
        builder.setIdView(R.layout.dialog_filter)
                .setCancelable(true)
                .setSetterGetterDialog(dialogSG)
                .setNeutralBtnStr(R.string.app_clear)
                .setOnClickNeutralBtn((dialog, which)->{
                    filterSetting.actual = false;
                    filterSetting.nonComplete = false;
                    filterSetting.priority_tasks = null;
                    filterSetting.tags = null;
                    taskManager.setFilter(BuilderFilter.buildFilter(filterSetting));
                    dialog.cancel();
                })
                .setOnClickPositiveBtn((dialog,which)->{
                    filterSetting.actual = isActual.get();
                    filterSetting.nonComplete = isActual.get() && isNoCompleted.get();
                    filterSetting.priority_tasks = (!prioritySet.isEmpty())? prioritySet : null;
                    filterSetting.tags = (!intTags.isEmpty())? intTags : null;
                    taskManager.setFilter(BuilderFilter.buildFilter(filterSetting));
                    dialog.dismiss();
                });

        AlertDialog dialog = QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_CUSTOM);

        ListView listPrior = (ListView) dialogSG.valuesView[dialogSG.getPosViewById(R.id.listPriority)];
        listPrior.setAdapter(new ArrayAdapter<>(MainAppActivity.this, android.R.layout.simple_list_item_checked,getResources().getStringArray(R.array.strings_priority_type_task)));
        listPrior.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listPrior.setOnItemClickListener((parent, view, position, id) -> {
            CheckedTextView chkView = (CheckedTextView) view;
            if(chkView.isChecked()) prioritySet.add(AbsTask.Priority_Task.values()[position]);
            else prioritySet.remove(AbsTask.Priority_Task.values()[position]);
        });
        setListViewHeightBasedOnChildren(listPrior);

        List<String> tags = TagManager.getListNameTag();
        ListView listTag = (ListView) dialogSG.valuesView[dialogSG.getPosViewById(R.id.listTags)];
        listTag.setAdapter( new ArrayAdapter<>(MainAppActivity.this, android.R.layout.simple_list_item_checked, tags));
        listTag.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listTag.setOnItemClickListener(((parent, view, position, id) -> {
            CheckedTextView chkView = (CheckedTextView) view;
            if(chkView.isChecked()) intTags.add(TagManager.getListIdTag().get(position));
            else intTags.remove(TagManager.getListIdTag().get(position));
        }));
        setListViewHeightBasedOnChildren(listTag);


        ExpandableLinearLayout expandablePriority = (ExpandableLinearLayout) dialogSG.valuesView[dialogSG.getPosViewById(R.id.expandablePriority)];
        ExpandableLinearLayout expandableTags = (ExpandableLinearLayout) dialogSG.valuesView[dialogSG.getPosViewById(R.id.expandableTags)];

        Switch swNoComplete = (Switch) dialogSG.valuesView[dialogSG.getPosViewById(R.id.switchNoComplete)];
        swNoComplete.setOnCheckedChangeListener((button, checked) -> isNoCompleted.set(checked));
        Switch swActual = (Switch) dialogSG.valuesView[dialogSG.getPosViewById(R.id.switchActual)];
        swActual.setOnCheckedChangeListener((button, checked) -> {
            isActual.set(checked);
            swNoComplete.setEnabled(checked);
        });

        Switch swPriority = (Switch) dialogSG.valuesView[dialogSG.getPosViewById(R.id.switchPriority)];
        swPriority.setOnCheckedChangeListener((button, checked)->{
            if(checked){
                expandablePriority.expand();
            }
            else expandablePriority.collapse();
        });
        Switch swTag = (Switch) dialogSG.valuesView[dialogSG.getPosViewById(R.id.switchTags)];
        swTag.setOnCheckedChangeListener((button, checked)->{
            if(checked){
                expandableTags.expand();
            }
            else expandableTags.collapse();
        });

        swActual.setChecked(filterSetting.actual);
        swNoComplete.setChecked(filterSetting.nonComplete);
        swNoComplete.setEnabled(swActual.isChecked());
        dialog.setOnShowListener(dialog1 -> {
            if(filterSetting.priority_tasks != null && !filterSetting.priority_tasks.isEmpty()){
                swPriority.toggle();
                expandablePriority.expand();
                for(int i = 0; i < AbsTask.Priority_Task.values().length; i++){
                    listPrior.setItemChecked(i, prioritySet.contains(AbsTask.Priority_Task.values()[i]));
                }
            }
            if(filterSetting.tags != null && !filterSetting.tags.isEmpty()){
                swTag.toggle();
                expandableTags.expand();
                List<Integer> keyTags = TagManager.getListIdTag();
                for(int i=0; i < keyTags.size(); i++){
                    listTag.setItemChecked(i, intTags.contains(keyTags.get(i)));
                }
            }
        });
        dialog.show();
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
        AlertDialog alertDialog = QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_INPUT_STRING);
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

    private void allTagShow(){
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

    private void addTagShow(){
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
        AlertDialog alertDialog = QDialog.make(builder, findViewById(android.R.id.content), QDialog.DIALOG_INPUT_STRING);
        dialogSG.setLabelString(getResources().getString(R.string.fieldEnterNameUnderTask));
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.standard_menu_activity_task, menu);
        boolean isCheck = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE).getBoolean(SHARED_PREFERENCE_SHORTMODE, false);
        menu.findItem(R.id.shortMode).setChecked(isCheck);
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
        this.selectTypeTask = selectTypeTask;
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
        dailyList.addAll(taskManager.getTaskByType(AbsTask.Type_Task.DAILY));
        dailyList.notifyDataSetChanged();
    }
    public void updateGoalList(){
        goalList.clear();
        goalList.addAll(taskManager.getTaskByType(AbsTask.Type_Task.GOAL));
        goalList.notifyDataSetChanged();
    }
    public void updateHabitList(){
        habitList.clear();
        habitList.addAll(taskManager.getTaskByType(AbsTask.Type_Task.HABIT));
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
