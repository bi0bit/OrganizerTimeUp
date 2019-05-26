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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.ManagerDB;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.R;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.EditorHeaderHabitBinding;
import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.databinding.ViewerHeaderHabitBinding;

public class Habit extends AbsTask{

    private EnumSet<Type_Habit> typeHabit;
    private static AbsTask.BuilderView builderView;

    public Habit(int id) {
        super(id, Type_Task.HABIT);
        typeHabit = EnumSet.allOf(Type_Habit.class);
    }

    static{
       builderView = new Habit.BuilderView(null,null,null,false);
    }


    protected Habit(Parcel in) {
        super(in,Type_Task.HABIT);
        EnumSet<Type_Habit> typeHabits = EnumSet.noneOf(Type_Habit.class);
        List<Type_Habit> listHabits = new ArrayList<>();
        in.readList(listHabits,Type_Habit.class.getClassLoader());
        for(Type_Habit t : listHabits)
        {
            typeHabits.add(t);
        }
        setTypeHabit(typeHabits);
    }

    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    public EnumSet getTypeHabit() {
        return typeHabit;
    }

    public void setTypeHabit(EnumSet typeHabit) {
        this.typeHabit = typeHabit;
    }

    public static AbsTask initTaskByCursor(Cursor cur){
        Habit habit = new Habit(0);
        AbsTask.initTaskByCursor(cur,habit);
        int type = cur.getInt(cur.getColumnIndex(ManagerDB.HABITTYPE_COLUMNNAME));
        initTypeHabit(type,habit.getTypeHabit());
        return habit;
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
        List<Type_Habit> list = new ArrayList<>();
        for(Type_Habit t : typeHabit){
            list.add(t);
        }
        dest.writeList(list);
    }

    @Override
    public Cursor getCursorOnTask(){
        Cursor c = ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETHABIT,new String[]{String.valueOf(getId())});
        return c;
    }

    @Override
    public void initialTaskInDB(){
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.initialTaskInDB();
        dbLite.execSQL(ManagerDB.INSERT_STRING_HABIT,new String[]{
                String.valueOf(getId()),String.valueOf(getTypeHabitInt())});
    }

    @Override
    public void updateInDb() {
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.updateInDb();
        dbLite.execSQL(ManagerDB.UPDATE_STRING_HABIT, new String[]{
                String.valueOf(getTypeHabitInt()),String.valueOf(getId())
        });
    }

    public int getTypeHabitInt(){
        int intTypeHabit = 0;
        intTypeHabit += (typeHabit.contains(Type_Habit.NEGATIVY))? -1 : 0;
        intTypeHabit += (typeHabit.contains(Type_Habit.POSITIVY))? 1 : 0;
        return intTypeHabit;
    }

    public static void initTypeHabit(int type, EnumSet<Type_Habit> type_habits){
        type_habits.clear();
        if(type >= 0){
            type_habits.add(Type_Habit.POSITIVY);
        }
        if(type <= 0){
            type_habits.add(Type_Habit.NEGATIVY);
        }
    }

    public void onItemSelectedTypeHabit(AdapterView<?> parent, View view, int pos, long id){
        int typeHabit = (pos == 0)? -1 : (pos == 1)? 1 : 0;
        Habit.initTypeHabit(typeHabit, getTypeHabit());
        View btnAddCount = view.getRootView().findViewById(R.id.addCounterButton);
        View btnSubCount = view.getRootView().findViewById(R.id.subCounterButton);
        if(typeHabit >= 0)
            btnAddCount.setVisibility(View.VISIBLE);
        else
            btnAddCount.setVisibility(View.GONE);

        if(typeHabit <= 0)
            btnSubCount.setVisibility(View.VISIBLE);
        else
            btnSubCount.setVisibility(View.GONE);
    }

    @BindingAdapter({"app:selectTypeHabit"})
    public static void selectTypeHabit(Spinner spin, int type){
        int pos = (type==0)? 2 : (type<0)? 0 : 1;
        spin.setSelection(pos);
    }

    public enum Type_Habit{
        POSITIVY,
        NEGATIVY;
    }

    /**
     * Build view habit for listItem, ViewViewerTask, ViewEditorTask
     */
    public static class BuilderView extends AbsTask.BuilderView<Habit>{

        BuilderView(Habit object, LayoutInflater inflater, ViewGroup parent, boolean attachToRoot) {
            super(object, inflater, parent, attachToRoot);
        }

        @Override
        public int getIdLayoutViewerHeader() {
            return R.layout.viewer_header_habit;
        }

        @Override
        public int getIdLayoutEditorHeader() {
            return R.layout.editor_header_habit;
        }

        @Override
        public View createBasicViewItem() {
            View view = getInflater().inflate(R.layout.habit_item, getParent(),isAttachToRoot());
            LinearLayout linearLayout = view.findViewById(R.id.contentItem);
            View content = getInflater().inflate(R.layout.content_habit_item, linearLayout, isAttachToRoot());
            linearLayout.addView(content);
            return view;
        }

        @Override
        public ViewDataBinding getBindingViewerHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutViewerHeader());
            ((ViewerHeaderHabitBinding)binding).setTask(getObject());
            return binding;
        }

        @Override
        public ViewDataBinding getBindingEditorHeader(Activity activity) {
            ViewDataBinding binding = DataBindingUtil.setContentView(activity,getIdLayoutEditorHeader());
            ((EditorHeaderHabitBinding)binding).setTask(getObject());
            return binding;
        }


        @Override
        public void setControlItem(View view){
            Habit habit = getObject();
            if(getObject().typeHabit.contains(Type_Habit.NEGATIVY)){
                Button button = view.findViewById(R.id.AddNegativyButton);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener((v)->{
                    habit.setCountSerias(habit.getCountSerias() - 1);
                    ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(habit.getId(),-1);
                    setCountItem(view,habit);
                });
            }
            if(getObject().typeHabit.contains(Type_Habit.POSITIVY)){
                Button button = view.findViewById(R.id.AddPossitivyButton);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener((v)->{
                    habit.setCountSerias(habit.getCountSerias() + 1);
                    ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(habit.getId(),+1);
                    setCountItem(view,habit);
                });
            }
        }

        @Override
        public void setViewerTask(View view) {
            super.setViewerTask(view);
        }

        @Override
        public void setEditorTask(View view) {
            super.setEditorTask(view);
            Habit habit = getObject();
            ArrayAdapter strArrAdapter =
                    ArrayAdapter.createFromResource(view.getContext(),R.array.strings_type_habit,R.layout.item_spinner_simple_string);
            strArrAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_simple_string);
            Spinner typeHabitSpinner = view.findViewById(R.id.spinnerTypeHabit);
            typeHabitSpinner.setAdapter(strArrAdapter);
        }
    }
}
