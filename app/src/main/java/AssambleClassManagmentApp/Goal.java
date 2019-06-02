package AssambleClassManagmentApp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
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
        dateStart = System.currentTimeMillis();
        dateDeadLine = System.currentTimeMillis();
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

    public void setStartDate(long dateStart) {
        this.dateStart = dateStart;
    }

    public static AbsTask initTaskByCursor(Cursor cur){
        Goal goal = new Goal(0);
        AbsTask.initTaskByCursor(cur,goal);
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
    public List<Date> getDateTask() {
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
        }

        @Override
        public void setCountItem(View view,AbsTask task) {
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
