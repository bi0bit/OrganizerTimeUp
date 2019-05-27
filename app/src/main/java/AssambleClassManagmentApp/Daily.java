package AssambleClassManagmentApp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.ManagerDB;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.R;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.EditorHeaderDailyBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.ViewerHeaderDailyBinding;

public class Daily extends AbsTask {


    private static AbsTask.BuilderView builderView;
    private List<Date> dates;
    private Type_Daily typeDaily;

    public Daily(int id) {
        super(id, Type_Task.DAILY);
        typeDaily = Type_Daily.EVERDAY;
        dates = new ArrayList<>();
        setCountSerias(0);
    }

    static{
        builderView = new Daily.BuilderView(null,null,null,false);
    }

    protected Daily(Parcel in) {
        super(in,Type_Task.DAILY);
        in.readList(dates,Date.class.getClassLoader());
        setTypeDaily(Type_Daily.valueOf(in.readString()));
    }

    public static final Creator<Daily> CREATOR = new Creator<Daily>() {
        @Override
        public Daily createFromParcel(Parcel in) {
            return new Daily(in);
        }

        @Override
        public Daily[] newArray(int size) {
            return new Daily[size];
        }
    };

    @Override
    public Cursor getCursorOnTask(){
        Cursor c = ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETDAILY,new String[]{String.valueOf(getId())});
        return c;
    }

    @Override
    public AbsTask.BuilderView getBuilderView() {
        return builderView;
    }

    @Override
    public void setBuilderView(AbsTask.BuilderView<? extends AbsTask> builder) {
        builderView = builder;
    }

    @Override
    public void initialTaskInDB(){
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.initialTaskInDB();
        dbLite.execSQL(ManagerDB.INSERT_STRING_DAILY,new String[]{
                String.valueOf(getId()),String.valueOf(getTypeDaily())});
    }

    @Override
    public void updateInDb() {
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.updateInDb();
        dbLite.execSQL(ManagerDB.UPDATE_STRING_DAILY, new String[]{
                String.valueOf(getTypeDaily().ordinal()),  String.valueOf(getId())
        });
    }

    public static AbsTask initTaskByCursor(Cursor cur){
        Daily daily = new Daily(0);
        AbsTask.initTaskByCursor(cur,daily);
        return daily;
    }


    public Type_Daily getTypeDaily() {
        return typeDaily;
    }

    public void setTypeDaily(Type_Daily typeDaily) {
        this.typeDaily = typeDaily;
    }

    @Override
    public List<java.util.Date> getDateTask() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeInToParcel(dest,flags);
        dest.writeList(dates);
        dest.writeString(getTypeDaily().name());
    }

    public void onItemSelectedTypeDaily(AdapterView<?> parent, View view, int pos, long id){
        Type_Daily typeDaily = Type_Daily.values()[pos];
        setTypeDaily(typeDaily);
    }

    public enum Type_Daily{
        EVERDAY,
        EVERWEEK,
        EVERMONTH,
        EVERYEAR
    }

    /**
     * Build view daily for listItem, ViewViewerTask, ViewEditorTask
     */
    public static class BuilderView extends AbsTask.BuilderView<Daily>{

        BuilderView(Daily object,  LayoutInflater inflater, ViewGroup parent, boolean attachToRoot) {
            super(object, inflater, parent, attachToRoot);
        }

        @Override
        public int getIdLayoutViewerHeader() {
            return  R.layout.viewer_header_daily;
        }

        @Override
        public int getIdLayoutEditorHeader() {
            return R.layout.editor_header_daily;
        }

        @Override
        public View createBasicViewItem() {
            View view = getInflater().inflate(R.layout.daily_item,getParent(),isAttachToRoot());
            LinearLayout linearLayout = view.findViewById(R.id.contentItem);
            View content = getInflater().inflate(R.layout.content_task_item, linearLayout, isAttachToRoot());
            linearLayout.addView(content);
            return view;
        }

        @Override
        public ViewDataBinding getBindingViewerHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutViewerHeader());
            ((ViewerHeaderDailyBinding)binding).setTask(getObject());
            return binding;
        }

        @Override
        public ViewDataBinding getBindingEditorHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutEditorHeader());
            ((EditorHeaderDailyBinding)binding).setTask(getObject());
            return binding;
        }

        @Override
        public void setViewerTask(View view) {
            super.setViewerTask(view);
        }

        @Override
        public void setEditorTask(View view) {
            super.setEditorTask(view);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.strings_type_daily, R.layout.item_spinner_simple_string);
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_simple_string);
            Spinner spinner = view.findViewById(R.id.spinnerTypeDaily);
            spinner.setAdapter(adapter);
        }
    }
}
