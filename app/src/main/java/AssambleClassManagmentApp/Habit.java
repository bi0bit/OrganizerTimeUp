package AssambleClassManagmentApp;

import android.annotation.SuppressLint;
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
import by.ilago_project.timeUp_ManagerTime.databinding.EditorHeaderHabitBinding;
import by.ilago_project.timeUp_ManagerTime.databinding.ViewerHeaderHabitBinding;
import by.ilago_project.timeUp_ManagerTime.ManagerDB;
import by.ilago_project.timeUp_ManagerTime.R;

public class Habit extends AbsTask{

    @SuppressLint("StaticFieldLeak")
    private static BuilderView builderView;
    private EnumSet<Type_Habit> typeHabit;

    public Habit(int id) {
        super(id, Type_Task.HABIT);
        typeHabit = EnumSet.allOf(Type_Habit.class);
    }

    static{
       builderView = new Habit.BuilderView(null,null,null);
    }


    protected Habit(Parcel in) {
        super(in,Type_Task.HABIT);
        EnumSet<Type_Habit> typeHabits = EnumSet.noneOf(Type_Habit.class);
        List<Type_Habit> listHabits = new ArrayList<>();
        in.readList(listHabits,Type_Habit.class.getClassLoader());
        typeHabits.addAll(listHabits);
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

    public EnumSet<Type_Habit> getTypeHabit() {
        return typeHabit;
    }

    public void setTypeHabit(EnumSet<Type_Habit> typeHabit) {
        this.typeHabit = typeHabit;
    }

    public static BuilderView getBuilder(){
        return  builderView;
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
        List<Type_Habit> list = new ArrayList<>(typeHabit);
        dest.writeList(list);
    }

    @Override
    public Cursor getCursorOnTask(){
        return ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETHABIT,new String[]{String.valueOf(getId())});
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
        intTypeHabit += (typeHabit.contains(Type_Habit.NEGATIVE))? -1 : 0;
        intTypeHabit += (typeHabit.contains(Type_Habit.POSITIVE))? 1 : 0;
        return intTypeHabit;
    }

    public static void initTypeHabit(int type, EnumSet<Type_Habit> type_habits){
        type_habits.clear();
        if(type >= 0){
            type_habits.add(Type_Habit.POSITIVE);
        }
        if(type <= 0){
            type_habits.add(Type_Habit.NEGATIVE);
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

    @BindingAdapter({"selectTypeHabit"})
    public static void selectTypeHabit(Spinner spin, int type){
        int pos = (type==0)? 2 : (type<0)? 0 : 1;
        spin.setSelection(pos);
    }

    public enum Type_Habit{
        POSITIVE,
        NEGATIVE
    }

    /**
     * Build view habit for listItem, ViewViewerTask, ViewEditorTask
     */
    public static class BuilderView extends AbsTask.BuilderView<Habit>{

        BuilderView(Habit object, LayoutInflater inflater, ViewGroup parent) {
            super(object, inflater, parent);
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
            Button buttonN = view.findViewById(R.id.AddNegativyButton);
            Button buttonP = view.findViewById(R.id.AddPossitivyButton);
            if(getObject().typeHabit.contains(Type_Habit.NEGATIVE)){
                buttonN.setVisibility(View.VISIBLE);
                buttonN.setOnClickListener((v)->{
                    habit.setCountSeries(habit.getCountSeries() - 1);
                    ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(habit.getId(),-1);
                    setCountItem(view,habit);
                });
            }
            else
                buttonN.setVisibility(View.GONE);

            if(getObject().typeHabit.contains(Type_Habit.POSITIVE)){
                buttonP.setVisibility(View.VISIBLE);
                buttonP.setOnClickListener((v)->{
                    habit.setCountSeries(habit.getCountSeries() + 1);
                    ManagerDB.getManagerDB(null).incrementTaskCountSeriesDb(habit.getId(),+1);
                    setCountItem(view,habit);
                });
            }
            else
                buttonP.setVisibility(View.GONE);
        }

        @Override
        public void setViewerTask(View view) {
            super.setViewerTask(view);
        }

        @Override
        public void setEditorTask(View view) {
            super.setEditorTask(view);
            ArrayAdapter strArrAdapter =
                    ArrayAdapter.createFromResource(view.getContext(),R.array.strings_type_habit,R.layout.item_spinner_simple_string);
            strArrAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_simple_string);
            Spinner typeHabitSpinner = view.findViewById(R.id.spinnerTypeHabit);
            typeHabitSpinner.setAdapter(strArrAdapter);
        }
    }
}
