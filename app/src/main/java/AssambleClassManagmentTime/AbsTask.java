package AssambleClassManagmentTime;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ViewDataBinding;
import by.ilagoproject.timeUp_ManagerTime.AdapterArrayPriorityType;
import by.ilagoproject.timeUp_ManagerTime.CheckListAdapter;
import by.ilagoproject.timeUp_ManagerTime.MainAppActivity;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;
import by.ilagoproject.timeUp_ManagerTime.NotifyAdapterList;
import by.ilagoproject.timeUp_ManagerTime.QDialog;
import by.ilagoproject.timeUp_ManagerTime.R;

public abstract class AbsTask implements Parcelable {

    public final Type_Task TYPE;

    private int id;
    private String name;
    private String description;
    private List<Integer> tag;
    private List<CheckTask> listUnderTaskChecked;


    private List<NotificationTask> listNotify;
    private int countSeries;
    private Priority_Task PRIORITY;
    private boolean NonLinkedWithDBId=true;
    private boolean TaskNoInitInDB=true;

    public AbsTask(int id, Type_Task type) {
        this.id = id;
        this.TYPE = type;
        this.PRIORITY = Priority_Task.MIN;
        this.tag = new ArrayList<>();
        this.listUnderTaskChecked = new ArrayList<>();
        this.listNotify = new ArrayList<>();
    }

    protected AbsTask(Parcel in, Type_Task type){
        this.TYPE = type;
        setId(in.readInt());
        TaskNoInitInDB = in.readByte() != 0;
        NonLinkedWithDBId = in.readByte() != 0;
        setName(in.readString());
        setDescription(in.readString());
        setPriority(Priority_Task.values()[in.readInt()]);
        setTags(new ArrayList<>());
        in.readList(getIntTags(), Integer.class.getClassLoader());
        setCountSeries(in.readInt());
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

    public List<Integer> getIntTags() {
        return tag;
    }

    protected void setTags(List<Integer> tag) {
        this.tag = tag;
    }

    public void setTag(int id, String tag){
        this.tag.add(id);
    }

    public List<CheckTask> getListUnderTaskChecked() {
        return listUnderTaskChecked;
    }

    public void setListUnderTaskChecked(List<CheckTask> checkList) {
        this.listUnderTaskChecked = checkList;
    }

    public static void initTaskByCursor(Cursor cur, AbsTask task){
        task.TaskNoInitInDB = false;
        task.setId(cur.getInt(cur.getColumnIndex(ManagerDB.ID_COLUMN)));
        task.NonLinkedWithDBId = false;
        task.setName(cur.getString(cur.getColumnIndex(ManagerDB.NAME_COLUMN)));
        task.setDescription(cur.getString(cur.getColumnIndex(ManagerDB.TASKDESCRIPTION_COLUMNNAME)));
        task.setCountSeries(cur.getInt(cur.getColumnIndex(ManagerDB.TASKCOUNT_COLUMNNAME)));
        task.setPriority(Priority_Task.values()[cur.getInt(cur.getColumnIndex(ManagerDB.TASKPRIORITY_COLUMNNAME))]);
    }

    //!!!!!!!!!
    public static void initCheckList(Cursor c, AbsTask task){

    }


    /**
     * <pre>Standard write in to parcel object for children</pre>
     * @param dest parcel
     * @param flags flag
     */
    protected void writeInToParcel(Parcel dest, int flags){
        dest.writeInt(getId());
        dest.writeByte((byte)(TaskNoInitInDB ? 1 : 0));
        dest.writeByte((byte)(NonLinkedWithDBId ? 1 : 0));
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeInt(getPriority().ordinal());
        dest.writeList(getIntTags());
        dest.writeInt(getCountSeries());
        dest.writeList(getListUnderTaskChecked());
        dest.writeList(getListNotify());
    }

    /**
     * <pre>get Cursor which shows on this task</pre>
     * @return cursor locate in table task
     */
    public Cursor getCursorOnTask(){
        return ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETTASKBYID,new String[]{String.valueOf(getId())});
    }

    public void setViewerTask(View view){
        getBuilderView().setViewerTask(view);
    }

    public void setEditorTask(View view){
        getBuilderView().setEditorTask(view);
    }

    public int getCountSeries() {
        return countSeries;
    }

    public void setCountSeries(int countSeries) {
        this.countSeries = countSeries;
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
            initIdTask();
            TaskNoInitInDB = false;
            ManagerDB.getManagerDB(null).updateTaskCountSeriesDb(getId(), getCountSeries());
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
            ManagerDB.getManagerDB(null).updateTaskCountSeriesDb(getId(), getCountSeries());
        }
        else {
            throw new ManagerDB.TaskNoInitInDB();
        }
    }

    /**
     * init field id
     */
    protected void initIdTask(){
        Cursor c = ManagerDB.getManagerDB(null).getDbReadable().rawQuery(ManagerDB.SEL_STRING_GETTASK,null);
        c.moveToLast();
        setId(c.getInt(0));
        NonLinkedWithDBId = false;
        c.close();
    }

    public abstract boolean isActual();

    public abstract boolean isComplete();

    public abstract List<?> getDateTask();

    public View createView(ViewGroup parent) {
        return getBuilderView().createViewItemTask(parent);
    }



    public ViewDataBinding createBindingViewerHeader(Activity activity){
        getBuilderView().setObject(this);
        return getBuilderView().getBindingViewerHeader(activity);
    }
    public ViewDataBinding createBindingEditorHeader(Activity activity){
        getBuilderView().setObject(this);
        return getBuilderView().getBindingEditorHeader(activity);
    }

    public void onItemSelectedPriority(AdapterView<?> parent, View view, int position, long id){
        Priority_Task priority = Priority_Task.values()[position];
        setPriority(priority);
    }

    static void resetTime(Calendar date){
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }


    @BindingAdapter({"selectSpin"})
    public static void selectSpin(Spinner spin, int pos){
        spin.setSelection(pos);
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

    public enum Type_Complete{
        NO_COMPLETE,
        COMPLETE
    }


    /**
     * This class build view for ListItem, ViewEditorTask, ViewViewerTask
     * @param <T> extend by AbsTask, pick object for storage and build
     */
    public abstract static class BuilderView<T extends AbsTask>{

        private T object;
        private LayoutInflater inflater;
        private boolean attachToRoot;
        BuilderView(T object, LayoutInflater inflater){
            this.object = object;
            this.inflater = inflater;
            this.attachToRoot = false;
        }

        public static Drawable getColorPriority(Resources resources, Priority_Task priority){
            final Drawable drawable;
            if(priority == Priority_Task.MIN){
              drawable = new ColorDrawable(resources.getColor(R.color.TypePriority_Easy));
            }
            else if(priority == Priority_Task.NORMAL){
              drawable = new ColorDrawable(resources.getColor(R.color.TypePriority_Normal));
            }
            else drawable = new ColorDrawable(resources.getColor(R.color.TypePriority_Necessary));

            return drawable;
        }

        public abstract int getIdLayoutViewerHeader();
        public abstract int getIdLayoutEditorHeader();

        public void setObject(AbsTask object){
            this.object =  (T)object;
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

        public boolean isAttachToRoot() {
            return attachToRoot;
        }

        public void setAttachToRoot(boolean attachToRoot) {
            this.attachToRoot = attachToRoot;
        }

        private void setTagItem(View view, float tagSize){
            FlexboxLayout flexboxLayout = view.findViewById(R.id.tagPanel);
            if(object.getIntTags().size() > 0) {
                flexboxLayout.setVisibility(View.VISIBLE);
                float dp = view.getResources().getDisplayMetrics().density;
                final FlexboxLayout.LayoutParams layout =
                        new FlexboxLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                float margin = 8;
                margin = dp * margin;
                layout.setMargins((int) margin, 0, 0, 0);
                flexboxLayout.removeAllViews();
                for (Integer tagId : object.getIntTags()) {
                    TextView textView = new TextView(flexboxLayout.getContext());
                    textView.setText(TagManager.getStringTag(tagId));
                    textView.setLayoutParams(layout);
                    textView.setPadding(0, 0, 0, 0);
                    textView.setSingleLine(true);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tagSize);
                    flexboxLayout.addView(textView);
                }
            }
            else flexboxLayout.setVisibility(View.GONE);
        }
        public void setDateItem(View view){}
        public void setContentItem(View view){
            TextView textName =  view.findViewById(R.id.nameTask);
            textName.setText(getObject().getName());
            TextView textDescription =  view.findViewById(R.id.desriptionTask);
            textDescription.setText(getObject().getDescription());
            CheckListAdapter adapterCheckList = new CheckListAdapter(view.getContext(),getObject().getListUnderTaskChecked(), null, null);
            adapterCheckList.setEditable(false);
            ListView checklistView = view.findViewById(R.id.checkListTask);
            checklistView.setAdapter(adapterCheckList);
            MainAppActivity.setListViewHeightBasedOnChildren(checklistView);
        }
        public void setCountItem(View view,AbsTask task){
            TextView textCount = view.findViewById(R.id.countSeries);
            textCount.setText(String.valueOf(task.getCountSeries()));
        }
        public void setControlItem(View view){
            View panelControl = view.findViewById(R.id.controlItem);
            Drawable color = getColorPriority(view.getResources(), getObject().getPriority());
            panelControl.setBackground(color);
            AbsTask task = getObject();
            CheckBox checkBox = (CheckBox) ((LinearLayout)panelControl).getChildAt(0);
            ManagerDB mdb = ManagerDB.getManagerDB(null);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isComplete());
            // check complete task
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(task.isActual()) {
                    Calendar calendar = Calendar.getInstance();
                    resetTime(calendar);
                    long date = calendar.getTimeInMillis();
                    if (isChecked) {
                        ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(task.getId(), 1);
                        mdb.completeTask(task.getId(), task.getCountSeries() + 1, date, Type_Complete.COMPLETE);
                    }
                    else {
                        int increment = (task.getCountSeries() == 0) ? 0 : -1;
                        ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(task.getId(), increment);
                        if(task.TYPE == Type_Task.GOAL) mdb.uncompleteTask(task.getId());
                        else mdb.uncompleteTask(task.getId(), date);
                    }

                } else checkBox.setChecked(task.isComplete());
            });

        }

        public abstract View createBasicViewItem(ViewGroup parent);

        private View createViewItemTask(ViewGroup parent){
            View view = createBasicViewItem(parent);
            setViewItemTask(view);
            return view;
        }

        public final void setViewItemTask(View view){
            setControlItem(view);
            setContentItem(view);
            setCountItem(view,object);
            setTagItem(view, view.getResources().getDimension(R.dimen.textSize_xsmall));
            setDateItem(view);
            if(!getObject().isActual()){
                PorterDuffColorFilter filterColor = new PorterDuffColorFilter(ContextCompat.getColor(view.getContext(), R.color.MaskItemNoActualTask), PorterDuff.Mode.MULTIPLY);
                view.getBackground().setColorFilter(filterColor);
            }
            else view.getBackground().clearColorFilter();
        }

        public abstract ViewDataBinding getBindingViewerHeader(Activity activity);
        public abstract ViewDataBinding getBindingEditorHeader(Activity activity);

        protected void addCheck(View view, ListView listView, CheckListAdapter checkListAdapter, AbsTask task){
            final QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
            final QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventAddNewUnderTask))
                    .setCancelable(true)
                    .setSetterGetterDialog(dialogSG)
                    .setOnClickPositiveBtn((dialog, which)->{
                        if(!dialogSG.getUserInputString().isEmpty()) {
                            task.getListUnderTaskChecked().add(new CheckTask(dialogSG.getUserInputString()));
                            checkListAdapter.notifyDataSetChanged();
                            MainAppActivity.setListViewHeightBasedOnChildren(listView);
                        }else Toast.makeText(view.getContext(),R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    });
            AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_INPUT_STRING);
            dialogSG.setLabelString(view.getResources().getString(R.string.fieldEnterNameUnderTask));
            alertDialog.show();
        }

        protected void clearCheck(View view, ListView listView, CheckListAdapter checkListAdapter, AbsTask task){
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventClearUnderTask))
                    .setMessage(view.getResources().getString(R.string.askClearUnderTask))
                    .setCancelable(true)
                    .setOnClickPositiveBtn(((dialog, which) -> {
                        task.getListUnderTaskChecked().clear();
                        checkListAdapter.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren(listView);
                    }));
            AlertDialog alertDialog = QDialog.make(builder, view, QDialog.DIALOG_QUATION);
            alertDialog.show();
        }

        protected void  addNotify(View view, ListView listView, NotifyAdapterList notifyAdapterList, AbsTask task){
            Calendar cldDate = Calendar.getInstance();

            QDialog.Builder builder = QDialog.getBuilder();
            QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
            dialogSG.setValuesId(new int[]{
                    R.id.title_notify,
                    R.id.message_notify,
                    R.id.time,
                    R.id.panelDate,
                    R.id.buttonSelectTime});
            builder.setTitle(view.getResources().getString(R.string.eventAddNewNotification))
                    .setSetterGetterDialog(dialogSG)
                    .setIdView(R.layout.dialog_notify_view)
                    .setCancelable(true)
                    .setOnClickPositiveBtn((dialog, which)->{
                        Object result = dialogSG.getValueView(0,"getText",null,null);
                        String title = (result!=null)? result.toString() : "";

                        result = dialogSG.getValueView(1, "getText",null,null);
                        String message = (result!=null)? result.toString() : "";

                        if(title.isEmpty()){
                            Toast.makeText(view.getContext(), R.string.eventEmptyField, Toast.LENGTH_LONG).show();
                            return;
                        }

                        long date = cldDate.getTimeInMillis();
                        NotificationTask notificationTask = new NotificationTask( date, date);
                        notificationTask.setTitle(title);
                        notificationTask.setMessage(message);
                        task.getListNotify().add(notificationTask);
                        notifyAdapterList.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren(listView);
                        dialog.dismiss();
                    });
            int rotation = view.getDisplay().getRotation();
            if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                builder.setFixSize(false)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            }
            AlertDialog dialog = QDialog.make(builder, view, QDialog.DIALOG_CUSTOM);

            dialogSG.setValueView(4,"setOnClickListener",
                    new Class[]{View.OnClickListener.class},
                    new View.OnClickListener[]{(v2) ->{
                        TimePickerDialog pickerDialog = new TimePickerDialog(view.getContext(),
                                (v3,h,m)->{
                                    cldDate.set(Calendar.HOUR_OF_DAY,h);
                                    cldDate.set(Calendar.MINUTE,m);
                                    SimpleDateFormat formater = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String time = formater.format(cldDate.getTime());
                                    dialogSG.setValueView(2,"setText",new Class[]{CharSequence.class},new Object[]{
                                            time});
                                }, cldDate.get(Calendar.HOUR_OF_DAY), cldDate.get(Calendar.MINUTE),true);

                        pickerDialog.show();
                    }});

            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

            dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                    new Object[]{
                            formatterTime.format(cldDate.getTime())});
            View panelDate = dialogSG.getValuesView(3);
            panelDate.setVisibility(View.GONE);
            dialog.show();
        }


        protected void clearNotify(View view, ListView listView, NotifyAdapterList notifyAdapterList, AbsTask task){
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventClearNotification))
                    .setMessage(view.getResources().getString(R.string.askClearNotification))
                    .setCancelable(true)
                    .setOnClickPositiveBtn(((dialog, which) -> {
                        task.getListNotify().clear();
                        notifyAdapterList.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren(listView);
                    }));
            AlertDialog alertDialog = QDialog.make(builder, view, QDialog.DIALOG_QUATION);
            alertDialog.show();
        }

        protected void onClickItemNotifyEditButton(View view, ViewGroup parent, NotificationTask editNotify, NotifyAdapterList adapterList){

            Calendar cldTime= Calendar.getInstance();
            cldTime.setTimeInMillis(editNotify.getTimeAlarm());

            QDialog.Builder builder = QDialog.getBuilder();
            QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
            dialogSG.setValuesId(new int[]{
                    R.id.title_notify,
                    R.id.message_notify,
                    R.id.time,
                    R.id.panelDate,
                    R.id.buttonSelectTime});
            builder.setTitle(view.getResources().getString(R.string.eventEditNotification))
                    .setSetterGetterDialog(dialogSG)
                    .setIdView(R.layout.dialog_notify_view)
                    .setCancelable(true)
                    .setOnClickPositiveBtn((dialog, which)->{
                        Object result = dialogSG.getValueView(0,"getText",null,null);
                        assert result != null;
                        String title = result.toString();

                        result = dialogSG.getValueView(1, "getText",null,null);
                        assert result != null;
                        String message = result.toString();

                        if (title.isEmpty()){
                            Toast.makeText(view.getContext(), R.string.eventEmptyField, Toast.LENGTH_LONG).show();
                            return;
                        }

                        editNotify.setTitle(title);
                        editNotify.setMessage(message);
                        editNotify.setTimeAlarm(cldTime.getTimeInMillis());
                        adapterList.notifyDataSetChanged();
                        dialog.dismiss();
                    });

            int rotation = view.getDisplay().getRotation();
            if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                builder.setFixSize(false)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            }

            AlertDialog dialog = QDialog.make(builder, view, QDialog.DIALOG_CUSTOM);

            dialogSG.setValueView(4,"setOnClickListener",
                    new Class[]{View.OnClickListener.class},
                    new View.OnClickListener[]{(v2) ->{
                        TimePickerDialog pickerDialog = new TimePickerDialog(view.getContext(),
                                (v3,h,m)->{
                                    cldTime.set(Calendar.HOUR_OF_DAY, h);
                                    cldTime.set(Calendar.MINUTE, m);
                                    SimpleDateFormat formater = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String time = formater.format(cldTime.getTime());
                                    dialogSG.setValueView(2,"setText",new Class[]{CharSequence.class},new Object[]{
                                            time});
                                },cldTime.get(Calendar.HOUR_OF_DAY), cldTime.get(Calendar.MINUTE),true);

                        pickerDialog.show();

                    }});

            dialogSG.setValueView(0,"setText", new Class[]{CharSequence.class},
                    new Object[]{editNotify.getTitle()});
            dialogSG.setValueView(1,"setText", new Class[]{CharSequence.class},
                    new Object[]{editNotify.getMessage()});
            dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                    new Object[]{new SimpleDateFormat("HH:mm", Locale.getDefault()).format(cldTime.getTime())} );

            View panelDate = dialogSG.getValuesView(3);
            panelDate.setVisibility(View.GONE);

            dialog.show();
        }

        protected void onClickItemNotifyDeleteButton(View view, ViewGroup parent, NotificationTask deleteNotify, NotifyAdapterList adapterList){
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventDeleteNotification))
                    .setMessage(view.getResources().getString(R.string.askDeleteNotification))
                    .setCancelable(true)
                    .setOnClickPositiveBtn(((dialog, which) -> {
                        adapterList.remove(deleteNotify);
                        adapterList.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                    }));
            AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_QUATION);
            alertDialog.show();
        }

        protected void onClickItemCheckEditButton(View view, ViewGroup parent, CheckTask checkTask, CheckListAdapter listAdapter){
            QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventEditUnderTask))
                    .setCancelable(true)
                    .setSetterGetterDialog(dialogSG)
                    .setOnClickPositiveBtn((dialog, which)->{
                        if(!dialogSG.getUserInputString().isEmpty()) {
                            checkTask.setText(dialogSG.getUserInputString());
                            listAdapter.notifyDataSetChanged();
                        }else Toast.makeText(view.getContext(),R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = QDialog.make(builder,view,QDialog.DIALOG_INPUT_STRING);
            dialogSG.setLabelString(view.getResources().getString(R.string.fieldEnterNameUnderTask));
            dialogSG.setUserInputString(checkTask.getText());
            alertDialog.show();
        }

        protected void onClickItemCheckDeleteCheck(View view, ViewGroup parent, CheckTask checkTask, CheckListAdapter listAdapter){
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventDeleteUnderTask))
                    .setMessage(view.getResources().getString(R.string.askDeleteUnderTask))
                    .setCancelable(true)
                    .setOnClickPositiveBtn((dialog, which) -> {
                        listAdapter.remove(checkTask);
                        listAdapter.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                    });
            AlertDialog alertDialog = QDialog.make(builder, view, QDialog.DIALOG_QUATION);
            alertDialog.show();
        }


        public void setViewerTask(View view){
            AbsTask task = getObject();
            TextView countSeries = view.findViewById(R.id.countSeries);
            countSeries.setText(String.valueOf(task.getCountSeries()));

            View panelTag = view.findViewById(R.id.panelTag);
            panelTag.setVisibility((task.getIntTags().size() < 1)? View.GONE : View.VISIBLE);
            setTagItem(view, view.getResources().getDimension(R.dimen.textSize_large));

            View panelCheckList = view.findViewById(R.id.panelControlCheckList);
            if(task.getListUnderTaskChecked().size() > 0){
                CheckListAdapter adapterCheckList = new CheckListAdapter(view.getContext(),getObject().getListUnderTaskChecked(), null, null);
                adapterCheckList.setEditable(false);
                ListView listViewUnderTask = view.findViewById(R.id.underTaskList);
                listViewUnderTask.setAdapter(adapterCheckList);
                MainAppActivity.setListViewHeightBasedOnChildren(listViewUnderTask);
                panelCheckList.setVisibility(View.VISIBLE);
            }
            else {
                panelCheckList.setVisibility(View.GONE);
            }

            View panelNotify = view.findViewById(R.id.panelNotify);
            if(task.getListNotify().size() > 0){
                NotifyAdapterList adapterListNotification = new NotifyAdapterList(view.getContext(), task.getListNotify(), null, null);
                adapterListNotification.setEditable(false);
                if(task.TYPE == Type_Task.GOAL)adapterListNotification.setShowDate(true);
                ListView listViewNotify = view.findViewById(R.id.notifyList);
                listViewNotify.setAdapter(adapterListNotification);
                MainAppActivity.setListViewHeightBasedOnChildren(listViewNotify);
                panelNotify.setVisibility(View.VISIBLE);
            }
            else {
                panelNotify.setVisibility(View.GONE);
            }

        }




        public void setEditorTask(View view){
            AbsTask task = getObject();

            TextView countSeries = view.findViewById(R.id.countSeries);
            countSeries.setText(String.valueOf(task.getCountSeries()));

            View panelTag = view.findViewById(R.id.panelTag);
            panelTag.setVisibility((TagManager.getTags().size() < 1)? View.GONE : View.VISIBLE);
            ArrayAdapter<String> adapterTag =
                    new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_checked, TagManager.getListNameTag());
            ListView tagList = view.findViewById(R.id.tagList);
            tagList.setAdapter(adapterTag);
            tagList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            List<Integer> keys = TagManager.getListIdTag();
            for(int i=0; i < keys.size(); i++){
                boolean check = task.getIntTags().contains(keys.get(i));
                tagList.setItemChecked(i,check);
            }
            tagList.setOnItemClickListener((parent, v, pos, id)->{
                List<Integer> listKeys = TagManager.getListIdTag();
                CheckedTextView checked = (CheckedTextView) v;
                int key = listKeys.get(pos);
                    if(checked.isChecked()){
                        if(!task.getIntTags().contains(key))
                            task.getIntTags().add(key);
                    }
                    else {
                        task.getIntTags().remove((Integer) key);
                    }
            });
            MainAppActivity.setListViewHeightBasedOnChildren(tagList);


            Spinner spinner = view.findViewById(R.id.spinnerTypePriority);
            ArrayAdapter<String> adapter = new AdapterArrayPriorityType(view.getContext());
            spinner.setAdapter(adapter);

            CheckListAdapter adapterCheckList =
                    new CheckListAdapter(view.getContext(), task.getListUnderTaskChecked(), this::onClickItemCheckEditButton, this::onClickItemCheckDeleteCheck);
            ListView listViewUnderTask = view.findViewById(R.id.underTaskList);
            listViewUnderTask.setAdapter(adapterCheckList);
            MainAppActivity.setListViewHeightBasedOnChildren(listViewUnderTask);

            Button buttonAddCheckBox = view.findViewById(R.id.addCheckTask);
            buttonAddCheckBox.setOnClickListener((v)-> addCheck(view, listViewUnderTask, adapterCheckList, task));
            Button buttonClearCheckBox = view.findViewById(R.id.clearCheckTask);
            buttonClearCheckBox.setOnClickListener((v)-> clearCheck(view, listViewUnderTask, adapterCheckList, task));

            Button buttonAddCount = view.findViewById(R.id.addCounterButton);
            buttonAddCount.setOnClickListener(v ->{
                task.setCountSeries(task.getCountSeries() + 1);
                countSeries.setText(String.valueOf(task.getCountSeries()));
                    });

            Button buttonSubCount = view.findViewById(R.id.subCounterButton);
            buttonSubCount.setOnClickListener(v ->{
                task.setCountSeries(task.getCountSeries() - 1);
                countSeries.setText(String.valueOf(task.getCountSeries()));
            });

            ListView listViewNotify = view.findViewById(R.id.notifyList);
            NotifyAdapterList adapterListNotification =
                    new NotifyAdapterList(view.getContext(), task.getListNotify(), this::onClickItemNotifyEditButton, this::onClickItemNotifyDeleteButton);
            if(task.TYPE == Type_Task.GOAL)adapterListNotification.setShowDate(true);
            listViewNotify.setAdapter(adapterListNotification);
            MainAppActivity.setListViewHeightBasedOnChildren(listViewNotify);

            Button buttonAddNotify = view.findViewById(R.id.addNotify);
            buttonAddNotify.setOnClickListener((v)->addNotify(view, listViewNotify, adapterListNotification, task));
            Button buttonClearNotify = view.findViewById(R.id.clearNotify);
            buttonClearNotify.setOnClickListener((v)->clearNotify(view, listViewNotify,adapterListNotification, task));
        }
    }

}

