package AssambleClassManagmentTime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import by.ilagoproject.timeUp_ManagerTime.MainAppActivity;
import by.ilagoproject.timeUp_ManagerTime.NotifyAdapterList;
import by.ilagoproject.timeUp_ManagerTime.QDialog;
import by.ilagoproject.timeUp_ManagerTime.databinding.EditorHeaderGoalBinding;
import by.ilagoproject.timeUp_ManagerTime.databinding.ViewerHeaderGoalBinding;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;
import by.ilagoproject.timeUp_ManagerTime.R;

public class Goal  extends AbsTask{

    private static BuilderView builderView;
    private long dateDeadLine;
    private long dateStart;
    public Goal(int id) {
        super(id, Type_Task.GOAL);
        Calendar calendar = Calendar.getInstance();
        resetTime(calendar);
        dateStart = calendar.getTimeInMillis();
        dateDeadLine = calendar.getTimeInMillis();
    }

    static{
        builderView = new Goal.BuilderView(null,null);
    }


    public static BuilderView getBuilder(){
        return builderView;
    }

    protected Goal(Parcel in) {
        super(in,Type_Task.GOAL);
        setStartDate(in.readLong());
        setEndDate(in.readLong());
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    public long getEndDate() {
        return dateDeadLine;
    }

    public String getEndDate(String stringPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(stringPattern, Locale.getDefault());
        Date date = new Date(getEndDate());
        return dateFormat.format(date);
    }

    public void setEndDate(long dateDeadLine) {
        this.dateDeadLine = dateDeadLine;
    }

    public long getStartDate() {
        return dateStart;
    }

    public String getStartDate(String stringPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(stringPattern, Locale.getDefault());
        Date date = new Date(getStartDate());
        return dateFormat.format(date);
    }

    @Override
    public boolean isActual() {
        boolean nonActualAfterEndDate = false;//TODO: Realization in setting choice parametr nonActualAfterEndDate
        Calendar dateNow = Calendar.getInstance();
        resetTime(dateNow);
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(this.getStartDate());
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(this.getEndDate());
        return !(dateNow.compareTo(endDate) > 0 && isComplete()) && dateNow.compareTo(startDate) >= 0; //TODO:realization separate by nonActualAfterEndDate: (!nonActualAfterEndDate || dateNow.compareTo(endDate) <= 0);
    }

    @Override
    public boolean isComplete() {
        Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByIdTask(getId());
        return c.getCount() > 0;
    }

    public void setStartDate(long dateStart) {
        this.dateStart = dateStart;
    }

    public static AbsTask initTaskByCursor(Cursor cur){
        Goal goal = new Goal(0);
        initTaskByCursor(cur,goal);
        long startDate = cur.getLong(cur.getColumnIndex(ManagerDB.GOALSTARTDATE_COLUMNNAME));
        goal.setStartDate(startDate);
        long endDate = cur.getLong(cur.getColumnIndex(ManagerDB.GOALENDDATE_COLUMNNAME));
        goal.setEndDate(endDate);
        return goal;
    }

    @Override
    public Cursor getCursorOnTask(){
        return ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETGOAL,new String[]{String.valueOf(getId())});
    }

    @Override
    public AbsTask.BuilderView getBuilderView() {
        return builderView;
    }

    @Override
    public void setBuilderView(AbsTask.BuilderView<? extends AbsTask> builder) {
        builderView = (BuilderView) builder;
    }

    @Override
    public List<?> getDateTask() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeInToParcel(dest,flags);
        dest.writeLong(getStartDate());
        dest.writeLong(getEndDate());
    }

    @Override
    public void initialTaskInDB(){
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.initialTaskInDB();
        dbLite.execSQL(ManagerDB.INSERT_STRING_GOAL,new String[]{
                String.valueOf(getId()), String.valueOf(getStartDate()), String.valueOf(getEndDate())});
    }

    @Override
    public void updateInDb() {
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.updateInDb();
        dbLite.execSQL(ManagerDB.UPDATE_STRING_GOAL, new String[]{
                String.valueOf(getStartDate()), String.valueOf(getEndDate()), String.valueOf(getId())
        });
    }

    public void onClickSelectStartDate(View view){
        View rootView = view.getRootView();
        Calendar cldStart = Calendar.getInstance();
        cldStart.setTimeInMillis(getStartDate());
        DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                (v2,y,m,d)->{
                    cldStart.set(y,m,d);
                    TextView textView = rootView.findViewById(R.id.textStartDateDaily);
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    textView.setText(format.format(cldStart.getTime()));
                    resetTime(cldStart);
                    setStartDate(cldStart.getTimeInMillis());
                },
                cldStart.get(Calendar.YEAR),
                cldStart.get(Calendar.MONTH),
                cldStart.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }

    public void onClickSelectEndDate(View view){
        View rootView = view.getRootView();
        Calendar cldEnd = Calendar.getInstance();
        cldEnd.setTimeInMillis(getEndDate());
        DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                (v2,y,m,d)->{
                    cldEnd.set(y,m,d);
                    TextView textView = rootView.findViewById(R.id.textEndDateDaily);
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    textView.setText(format.format(cldEnd.getTime()));
                    resetTime(cldEnd);
                    setEndDate(cldEnd.getTimeInMillis());
                },
                cldEnd.get(Calendar.YEAR),
                cldEnd.get(Calendar.MONTH),
                cldEnd.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show();
    }



    /**
     * Build view goal for listItem, ViewViewerTask, ViewEditorTask
     */
    public static class BuilderView extends AbsTask.BuilderView<Goal>{


        BuilderView(Goal object, LayoutInflater inflater) {
            super(object,inflater);
        }

        @Override
        public int getIdLayoutViewerHeader() {
            return R.layout.viewer_header_goal;
        }

        @Override
        public int getIdLayoutEditorHeader() {
            return R.layout.editor_header_goal;
        }

        @Override
        public View createBasicViewItem(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.task_item, parent, isAttachToRoot());
            LinearLayout linearLayout = view.findViewById(R.id.contentItem);
            View content = getInflater().inflate(R.layout.content_task_item, linearLayout, isAttachToRoot());
            linearLayout.addView(content);
            return view;
        }

        @Override
        public ViewDataBinding getBindingViewerHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutViewerHeader());
            ((ViewerHeaderGoalBinding)binding).setTask(getObject());
            return binding;
        }

        @Override
        public ViewDataBinding getBindingEditorHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutEditorHeader());
            ((EditorHeaderGoalBinding)binding).setTask(getObject());
            return binding;
        }




        @Override
        public void setDateItem(View view) {
            super.setDateItem(view);
            TextView dateView = view.findViewById(R.id.dateDeadLineTask);
            String date = view.getResources().getString(R.string.fieldExecute) + " " + getObject().getEndDate("dd.MM.yyyy");
            dateView.setText(date);
            Calendar calendar = Calendar.getInstance();
            resetTime(calendar);
            if(calendar.getTimeInMillis() > getObject().dateDeadLine && !getObject().isComplete()){
                Drawable color = new ColorDrawable(view.getResources().getColor(R.color.MarkLateDate));
                dateView.setBackground(color);
            }
            else if(getObject().isComplete()){
                Drawable color = new ColorDrawable(view.getResources().getColor(R.color.MarkNormalDate));
                dateView.setBackground(color);
            }
            else{
                Drawable color = new ColorDrawable(view.getResources().getColor(R.color.BackgroundLight));
                dateView.setBackground(color);
            }
        }

        @Override
        public void setCountItem(View view,AbsTask task) {
        }

        @Override
        protected void addNotify(View view, ListView listView, NotifyAdapterList notifyAdapterList, AbsTask task) {
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
            dialogSG.setValueView(5,"setOnClickListener",
                    new Class[]{View.OnClickListener.class},
                    new View.OnClickListener[]{(v2) ->{
                        DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                                (v3,y,m,d)->{
                                    cldDate.set(y,m,d);
                                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                    dialogSG.setValueView(3,"setText",new Class[]{CharSequence.class},new Object[]{
                                            format.format(cldDate.getTime())});
                                }, cldDate.get(Calendar.YEAR), cldDate.get(Calendar.MONTH), cldDate.get(Calendar.DAY_OF_MONTH));
                        pickerDialog.show();
                        }
                    });

            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

            dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                    new Object[]{
                            formatterTime.format(cldDate.getTime())});
            dialogSG.setValueView(3,"setText", new Class[]{CharSequence.class},
                    new Object[]{
                            formatterDate.format(cldDate.getTime())});
            dialog.show();
        }

        @Override
        protected void onClickItemNotifyEditButton(View view, ViewGroup parent, NotificationTask editNotify, NotifyAdapterList adapterList) {
            Calendar cldDate = Calendar.getInstance();
            cldDate.setTimeInMillis(editNotify.getDateAlarm());

            Calendar cldTime= Calendar.getInstance();
            cldTime.setTimeInMillis(editNotify.getTimeAlarm());

            QDialog.Builder builder = QDialog.getBuilder();
            QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
            dialogSG.setValuesId(new int[]{
                    R.id.title_notify,
                    R.id.message_notify,
                    R.id.time,
                    R.id.date,
                    R.id.buttonSelectTime,
                    R.id.buttonSelectDate});
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
                        editNotify.setDateAlarm(cldDate.getTimeInMillis());
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
            dialogSG.setValueView(5,"setOnClickListener",
                    new Class[]{View.OnClickListener.class},
                    new View.OnClickListener[]{(v2) ->{
                        DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                                (v3,y,m,d)->{
                                    cldDate.set(y, m, d);
                                    SimpleDateFormat formatterDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                    String date = formatterDate.format(cldDate.getTime());
                                    dialogSG.setValueView(3,"setText",
                                            new Class[]{CharSequence.class},
                                            new Object[]{date});
                                },cldDate.get(Calendar.YEAR), cldDate.get(Calendar.MONTH),cldDate.get(Calendar.DAY_OF_MONTH));
                        pickerDialog.show();
                    }});


            dialogSG.setValueView(0,"setText", new Class[]{CharSequence.class},
                    new Object[]{editNotify.getTitle()});
            dialogSG.setValueView(1,"setText", new Class[]{CharSequence.class},
                    new Object[]{editNotify.getMessage()});
            dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                    new Object[]{new SimpleDateFormat("HH:mm", Locale.getDefault()).format(cldTime.getTime())} );
            dialogSG.setValueView(3,"setText", new Class[]{CharSequence.class},
                    new Object[]{new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault()).format(cldDate.getTime())} );
            dialog.show();
        }

        @Override
        public void setViewerTask(View view) {
            super.setViewerTask(view);
            View panelCounter = view.findViewById(R.id.panelCounter);
            panelCounter.setVisibility(View.GONE);
        }

        @Override
        public void setEditorTask(View view) {
            super.setEditorTask(view);
            View panelCounter = view.findViewById(R.id.panelCounter);
            panelCounter.setVisibility(View.GONE);
        }
    }
}
