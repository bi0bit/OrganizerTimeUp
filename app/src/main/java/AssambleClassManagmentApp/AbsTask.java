package AssambleClassManagmentApp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ViewDataBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.AdapterArrayPriorityType;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.CheckListAdapter;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.MainAppActivity;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.ManagerDB;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.NotifyAdapterList;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.QDialog;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.R;

public abstract class AbsTask implements Parcelable {

    public final Type_Task TYPE;

    private int id;
    private String name;
    private String description;
    private HashMap<Integer,String> tag;
    private List<CheckTask> listUnderTaskChecked;


    private List<NotificationTask> listNotify;
    private int countSerias;
    private Priority_Task PRIORITY;
    private boolean NonLinkedWithDBId=true;
    private boolean TaskNoInitInDB=true;

    //@SuppressLint("UseSparseArrays")
    public AbsTask(int id, Type_Task type) {
        this.id = id;
        this.TYPE = type;
        this.PRIORITY = Priority_Task.MIN;
        this.tag = new HashMap<>();
        this.listUnderTaskChecked = new ArrayList<>();
        this.listNotify = new ArrayList<>();
        listUnderTaskChecked.add(new CheckTask("test1"));
    }

    protected AbsTask(Parcel in, Type_Task type){
        this.TYPE = type;
        setId(in.readInt());
        TaskNoInitInDB = in.readByte() != 0;
        NonLinkedWithDBId = in.readByte() != 0;
        setName(in.readString());
        setDescription(in.readString());
        setPriority(Priority_Task.values()[in.readInt()]);
        setTags((HashMap<Integer,String>)in.readSerializable());
        setCountSerias(in.readInt());
        setListUnderTaskChecked(new ArrayList<>());
        in.readList(getListUnderTaskChecked(),CheckTask.class.getClassLoader());
        setListNotify(new ArrayList<>());
        in.readList(getListNotify(), NotificationTask.class.getClassLoader());
    }

    public abstract BuilderView getBuilderView();

    public abstract void setBuilderView(BuilderView<? extends AbsTask> builder);

    public List<NotificationTask> getListNotify() {
        return listNotify;
    }

    public void setListNotify(List<NotificationTask> listNotify) {
        this.listNotify = listNotify;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<Integer,String> getTags() {
        return tag;
    }

    protected void setTags(HashMap tags) {
        this.tag = tags;
    }

    @Nullable
    public String getTag(int id){
        String s = tag.get(id);
        return s;
    }

    public void setTag(int id, String tag){
        this.tag.put(id,tag);
    }

    public List<CheckTask> getListUnderTaskChecked() {
        return listUnderTaskChecked;
    }

    public void setListUnderTaskChecked(List<CheckTask> checkLisk) {
        this.listUnderTaskChecked = checkLisk;
    }

    public static void initTaskByCursor(Cursor cur, AbsTask task){
        task.TaskNoInitInDB = false;
        task.setId(cur.getInt(cur.getColumnIndex(ManagerDB.ID_COLUMN)));
        task.NonLinkedWithDBId = false;
        task.setName(cur.getString(cur.getColumnIndex(ManagerDB.NAME_COLUMN)));
        task.setDescription(cur.getString(cur.getColumnIndex(ManagerDB.TASKDESCRIPTION_COLUMNNAME)));
        task.setCountSerias(cur.getInt(cur.getColumnIndex(ManagerDB.TASKCOUNT_COLUMNNAME)));
        task.setPriority(Priority_Task.values()[cur.getInt(cur.getColumnIndex(ManagerDB.TASKPRIORITY_COLUMNNAME))]);
    }

    //!!!!!!!!!
    public static void initCheckList(Cursor c, AbsTask task){

    }

    /**
     * <pre>Standart write in to parcel object for children</pre>
     * @param dest
     * @param flags
     */
    protected void writeInToParcel(Parcel dest, int flags){
        dest.writeInt(getId());
        dest.writeByte((byte)(TaskNoInitInDB ? 1 : 0));
        dest.writeByte((byte)(NonLinkedWithDBId ? 1 : 0));
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeInt(getPriority().ordinal());
        dest.writeSerializable(getTags());
        dest.writeInt(getCountSerias());
        dest.writeList(getListUnderTaskChecked());
        dest.writeList(getListNotify());
    }

    /**
     * <pre>get Cursor which shows on this task</pre>
     * @return
     */
    public Cursor getCursorOnTask(){
        Cursor c = ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETTASKBYID,new String[]{String.valueOf(getId())});
        return c;
    }

    public void setViewerTask(View view){
        getBuilderView().setViewerTask(view);
    }

    public void setEditorTask(View view){
        getBuilderView().setEditorTask(view);
    }

    public int getCountSerias() {
        return countSerias;
    }

    public void setCountSerias(int countSerias) {
        this.countSerias = countSerias;
    }

    public Priority_Task getPriority() {
        return PRIORITY;
    }

    public void setPriority(Priority_Task priority) {
        this.PRIORITY = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(TaskNoInitInDB || NonLinkedWithDBId)
            this.id = id;
    }

    /**
     * add new record about task in DB
     */
    public void initialTaskInDB() {
        if(TaskNoInitInDB) {
            SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
            dbLite.execSQL(ManagerDB.INSERT_STRING_TASK, new String[]{getName(), getDescription(), String.valueOf(getPriority().ordinal())});
            initIdTask(dbLite);
            TaskNoInitInDB = false;
            ManagerDB.getManagerDB(null).updateTaskCountSeriesDb(getId(), getCountSerias());
        }
        else throw new ManagerDB.TaskInitInDB();
    }

    /**
     * update task in DB
     */
    public void updateInDb(){
        if (!TaskNoInitInDB && !NonLinkedWithDBId){
            SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
            dbLite.execSQL(ManagerDB.UPDATE_STRING_TASK, new String[]{getName(),getDescription(),String.valueOf(getPriority().ordinal()), String.valueOf(getId())});
            ManagerDB.getManagerDB(null).updateTaskCountSeriesDb(getId(), getCountSerias());
        }
        else {
            throw new ManagerDB.TaskNoInitInDB();
        }
    }

    /**
     * init field id
     * @param db object db
     */
    protected void initIdTask(SQLiteDatabase db){
        Cursor c = db.rawQuery(ManagerDB.SEL_STRING_GETTASK,null);
        c.moveToLast();
        setId(c.getInt(0));
        NonLinkedWithDBId = false;
    }

    public abstract List<Date> getDateTask();

    public View createView() {
        getBuilderView().setObject(this);
        View view = getBuilderView().createViewItemTask();
        return view;
    }



    public ViewDataBinding createBindingViewerHeader(Activity activity){
        getBuilderView().setObject(this);
        getBuilderView().setParent(activity.findViewById(android.R.id.content));
        return getBuilderView().getBindingViewerHeader(activity);
    }
    public ViewDataBinding createBindingEditorHeader(Activity activity){
        getBuilderView().setObject(this);
        getBuilderView().setParent(activity.findViewById(android.R.id.content));
        return getBuilderView().getBindingEditorHeader(activity);
    }

    public void onItemSelectedPriority(AdapterView<?> parent, View view, int position, long id){
        Priority_Task priority = Priority_Task.values()[position];
        setPriority(priority);
    }

    @BindingAdapter({"app:selectSpin"})
    public static void selectSpin(Spinner spin, int priority){
        spin.setSelection(priority);
    }

    public void addCheck(View view, ListView listView, CheckListAdapter checkListAdapter){
        QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventAddNewUnderTask))
                .setCancelable(true)
                .setSetterGetterDialog(dialogSG)
                .setPositiveBtn((dialog,which)->{
                    if(!dialogSG.getUserInputString().isEmpty()) {
                        getListUnderTaskChecked().add(new CheckTask(dialogSG.getUserInputString()));
                        checkListAdapter.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren(listView);
                    }else Toast.makeText(view.getContext(),R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
        AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_INPUT_STRING);
        dialogSG.setLabelString(view.getResources().getString(R.string.fieldEnterNameUnderTask));
        alertDialog.show();
    }

    public void clearCheck(View view, ListView listView, CheckListAdapter checkListAdapter){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventClearUnderTask))
                .setMessage(view.getResources().getString(R.string.askClearUnderTask))
                .setCancelable(true)
                .setPositiveBtn(((dialog, which) -> {
                    getListUnderTaskChecked().clear();
                    checkListAdapter.notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren(listView);
                }));
        AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_QUATION);
        alertDialog.show();
    }

    public void  addNotify(View view, ListView listView, NotifyAdapterList notifyAdapterList){
        Calendar cldDate = Calendar.getInstance();

        QDialog.Builder builder = QDialog.getBuilder();
        QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
        dialogSG.setValuesId(new int[]{
                R.id.title_notify,
                R.id.message_notify,
                R.id.time,
                R.id.date,
                R.id.buttonSelectTime,
                R.id.buttonSelectDate});
        builder.setTitle(view.getResources().getString(R.string.eventAddNewNotification))
                .setSetterGetterDialog(dialogSG)
                .setIdView(R.layout.dialog_notify_view)
                .setCancelable(true)
                .setPositiveBtn((dialog,which)->{
                    Object result = dialogSG.getValueView(0,"getText",null,null);
                    String title = result.toString();

                    result = dialogSG.getValueView(1, "getText",null,null);
                    String message = result.toString();

                    if(title.isEmpty()){
                        Toast.makeText(view.getContext(), R.string.eventEmptyField, Toast.LENGTH_LONG).show();
                        return;
                    }

                    long date = cldDate.getTimeInMillis();
                    NotificationTask notificationTask = new NotificationTask( date, date);
                    notificationTask.setTitle(title);
                    notificationTask.setMessage(message);
                    getListNotify().add(notificationTask);
                    notifyAdapterList.notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren(listView);
                    dialog.dismiss();
                });
        AlertDialog.Builder dialog = builder.buildDialog(view,QDialog.DIALOG_CUSTOM);
        dialogSG.setValueView(4,"setOnClickListener",
                new Class[]{View.OnClickListener.class},
                new View.OnClickListener[]{(v2) ->{
                    TimePickerDialog pickerDialog = new TimePickerDialog(view.getContext(),
                            (v3,h,m)->{
                                String hours = (h >= 10)? String.valueOf(h) : "0" + h;
                                String min = (m >= 10)? String.valueOf(m) : "0" + m;
                                dialogSG.setValueView(2,"setText",new Class[]{CharSequence.class},new Object[]{
                                        hours + ":" + min});
                                cldDate.set(Calendar.HOUR_OF_DAY,h);
                                cldDate.set(Calendar.MINUTE,m);
                            }, cldDate.get(Calendar.HOUR_OF_DAY), cldDate.get(Calendar.MINUTE),true);

                    pickerDialog.show();

                }});
        dialogSG.setValueView(5,"setOnClickListener",
                new Class[]{View.OnClickListener.class},
                new View.OnClickListener[]{(v2) ->{
                    DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                            (v3,y,m,d)->{
                                String mouth  = (m >= 10)? String.valueOf(m) : "0" + m;
                                String day = (d >= 10)? String.valueOf(d) : "0" + d;
                                dialogSG.setValueView(3,"setText",new Class[]{CharSequence.class},new Object[]{
                                        day + "." + mouth + "." + y});
                                cldDate.set(y,m,d);
                            }, cldDate.get(Calendar.YEAR), cldDate.get(Calendar.MONDAY), cldDate.get(Calendar.DAY_OF_MONTH));
                    pickerDialog.show();
                }});
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                new Object[]{
                        formatterTime.format(cldDate.getTime())});
        dialogSG.setValueView(3,"setText", new Class[]{CharSequence.class},
                new Object[]{
                        formatterDate.format(cldDate.getTime())});
        dialog.show();
    }


    public void clearNotify(View view, ListView listView, NotifyAdapterList notifyAdapterList){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventClearNotification))
                .setMessage(view.getResources().getString(R.string.askClearNotification))
                .setCancelable(true)
                .setPositiveBtn(((dialog, which) -> {
                    getListNotify().clear();
                    notifyAdapterList.notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren(listView);
                }));
        AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_QUATION);
        alertDialog.show();
    }

    public enum Type_Task {
        HABIT,
        DAILY,
        GOAL
    }

    public enum Priority_Task {
        MIN,
        NORMAL,
        MAX
    }


    /**
     * This class build view for ListItem, ViewEditorTask, ViewViewerTask
     * @param <T> extend by AbsTask, pick object for storage and build
     */
    public abstract static class BuilderView<T extends AbsTask>{

        private T object;
        private LayoutInflater inflater;
        private ViewGroup parent;
        private boolean attachToRoot;
        BuilderView(T object, LayoutInflater inflater, ViewGroup parent, boolean attachToRoot){
            this.object = object;
            this.inflater = inflater;
            this.parent = parent;
            this.attachToRoot = attachToRoot;
        }

        public abstract int getIdLayoutViewerHeader();
        public abstract int getIdLayoutEditorHeader();

        public void setObject(T object){
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public LayoutInflater getInflater() {
            return inflater;
        }

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        public ViewGroup getParent() {
            return parent;
        }

        public void setParent(ViewGroup parent) {
            this.parent = parent;
        }

        public boolean isAttachToRoot() {
            return attachToRoot;
        }

        public void setAttachToRoot(boolean attachToRoot) {
            this.attachToRoot = attachToRoot;
        }

        public void setTagItem(View view){
            LinearLayout linearLayout = view.findViewById(R.id.tagPanel);
            float dp = view.getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            float margin = 5;
            float textSize = 9;
            margin = dp * margin;
            layout.setMargins((int) margin,0,0,0);
            for(String tag : object.getTags().values()){
                TextView textView = new TextView(linearLayout.getContext());
                textView.setText(tag);
                textView.setLayoutParams(layout);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                linearLayout.addView(textView);
            }
        }
        public void setDateItem(View view){}
        public void setContentItem(View view){
            TextView textName =  view.findViewById(R.id.nameTask);
            textName.setText(getObject().getName());
            TextView textDescription =  view.findViewById(R.id.desriptionTask);
            textDescription.setText(getObject().getDescription());
        }
        public void setCountItem(View view,AbsTask task){
            TextView textCount = view.findViewById(R.id.countSerias);
            textCount.setText(Integer.toString(task.getCountSerias()));
        }
        public void setControlItem(View view){

        }
        public abstract View createBasicViewItem();
        public View createViewItemTask(){
            View view = createBasicViewItem();
            setControlItem(view);
            setContentItem(view);
            setCountItem(view,object);
            setTagItem(view);
            setDateItem(view);
            return view;
        }

        public abstract ViewDataBinding getBindingViewerHeader(Activity activity);
        public abstract ViewDataBinding getBindingEditorHeader(Activity activity);

        public void setViewerTask(View view){
            AbsTask task = getObject();
            TextView countSeries = view.findViewById(R.id.countSerias);
            countSeries.setText(String.valueOf(task.getCountSerias()));

            ArrayAdapter<String> adapterTag =
                    new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_checked, new String[]{});
            ListView tagList = view.findViewById(R.id.panelTag);
            tagList.setAdapter(adapterTag);

            if(task.getListUnderTaskChecked().size() > 0){
                CheckListAdapter adapterCheckList = new CheckListAdapter(view.getContext(),getObject().getListUnderTaskChecked());
                adapterCheckList.setEditable(false);
                ListView listViewUnderTask = view.findViewById(R.id.underTaskList);
                listViewUnderTask.setAdapter(adapterCheckList);
            }
            else {
                View v = view.findViewById(R.id.panelControlCheckList);
                v.setVisibility(View.GONE);
            }

            if(task.getListNotify().size() > 0){
                NotifyAdapterList adapterListNotification = new NotifyAdapterList(view.getContext(), task.getListNotify());
                adapterListNotification.setEditable(false);
                ListView listViewNotify = view.findViewById(R.id.notifyList);
                listViewNotify.setAdapter(adapterListNotification);
            }
            else {
                View v = view.findViewById(R.id.panelNotify);
                v.setVisibility(View.GONE);
            }

        }




        public void setEditorTask(View view){
            AbsTask task = getObject();

            TextView countSeries = view.findViewById(R.id.countSerias);
            countSeries.setText(String.valueOf(task.getCountSerias()));

            Spinner spinner = view.findViewById(R.id.spinnerTypePriority);
            ArrayAdapter adapter = new AdapterArrayPriorityType(view.getContext(),
                    Arrays.asList(view.getContext().getResources().getStringArray(R.array.strings_priority_type_task)));
            spinner.setAdapter(adapter);

            CheckListAdapter adapterCheckList = new CheckListAdapter(view.getContext(),getObject().getListUnderTaskChecked());
            ListView listViewUnderTask = view.findViewById(R.id.underTaskList);
            listViewUnderTask.setAdapter(adapterCheckList);

            Button buttonAddCheckBox = view.findViewById(R.id.addCheckTask);
            buttonAddCheckBox.setOnClickListener((v)-> task.addCheck(view,listViewUnderTask,adapterCheckList));
            Button buttonClearCheckBox = view.findViewById(R.id.clearCheckTask);
            buttonClearCheckBox.setOnClickListener((v)-> task.clearCheck(view,listViewUnderTask,adapterCheckList));

            Button buttonAddCount = view.findViewById(R.id.addCounterButton);
            buttonAddCount.setOnClickListener(v ->{
                task.setCountSerias(task.getCountSerias() + 1);
                countSeries.setText(String.valueOf(task.getCountSerias()));
                    });

            Button buttonSubCount = view.findViewById(R.id.subCounterButton);
            buttonSubCount.setOnClickListener(v ->{
                task.setCountSerias(task.getCountSerias() - 1);
                countSeries.setText(String.valueOf(task.getCountSerias()));
            });

            ListView listViewNotify = view.findViewById(R.id.notifyList);
            NotifyAdapterList adapterListNotification = new NotifyAdapterList(view.getContext(), task.getListNotify());
            listViewNotify.setAdapter(adapterListNotification);

            Button buttonAddNotify = view.findViewById(R.id.addNotify);
            buttonAddNotify.setOnClickListener((v)->task.addNotify(view,listViewNotify,adapterListNotification));
            Button buttonClearNotify = view.findViewById(R.id.clearNotify);
            buttonClearNotify.setOnClickListener((v)->task.clearNotify(view,listViewNotify,adapterListNotification));
        }
    }

}

