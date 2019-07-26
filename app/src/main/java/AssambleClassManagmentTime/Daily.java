package AssambleClassManagmentTime;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import by.ilagoproject.timeUp_ManagerTime.databinding.EditorHeaderDailyBinding;
import by.ilagoproject.timeUp_ManagerTime.databinding.ViewerHeaderDailyBinding;
import by.ilagoproject.timeUp_ManagerTime.ManagerDB;
import by.ilagoproject.timeUp_ManagerTime.R;


public class Daily extends AbsTask {


    private static BuilderView builderView;
    private List<DateDaily> dates;
    private Type_Daily typeDaily;
    private List<DateDaily> buffDateWeekly;

    public Daily(int id) {
        super(id, Type_Task.DAILY);
        typeDaily = Type_Daily.EVERDAY;
        dates = new ArrayList<>();
        setCountSeries(0);
        buffDateWeekly = null;
    }

    static{
        builderView = new Daily.BuilderView(null,null);
    }

    protected Daily(Parcel in) {
        super(in,Type_Task.DAILY);
        dates = new ArrayList<>();
        in.readList(dates,DateDaily.class.getClassLoader());
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

    public static BuilderView getBuilder(){
        return  builderView;
    }

    @Override
    public Cursor getCursorOnTask(){
        return ManagerDB.getManagerDB(null).getDbReadable()
                .rawQuery(ManagerDB.SEL_STRING_GETDAILY,new String[]{String.valueOf(getId())});
    }


    @Override
    boolean isActualTask(long date) {
        Calendar dateC = Calendar.getInstance(Locale.getDefault());
        dateC.setTimeInMillis(date);
        resetTime(dateC);
        List<DateDaily> dateLong = dates;
        if(dateLong.size() < 1) return false;
        switch (typeDaily){
            case EVERDAY: {
                DateDaily dateDaily = dateLong.get(0);
                Calendar dateT = Calendar.getInstance();
                dateT.setTimeInMillis(dateDaily.longTime);
                long difference = dateC.getTimeInMillis() - dateT.getTime().getTime();
                long day =  difference / (24 * 60 * 60 * 1000);
                if (day % dateDaily.every == 0) return true;
                break;
            }
            case EVERWEEK: {
                int dayWeak = dateC.get(Calendar.DAY_OF_WEEK);
                Log.d("testFilterEverWeek", this.getName() + " weekday " + dateC.get(Calendar.DAY_OF_WEEK) + ", " + dateC.getTime().toString());
                for (DateDaily dateDaily : dateLong) {
                    Calendar dateT = Calendar.getInstance(Locale.getDefault());
                    dateT.setTimeInMillis(dateDaily.longTime);
                    Log.d("testFilterEverWeek", "weekday dateDaily " + dateT.get(Calendar.DAY_OF_WEEK) + "," + dateT.getTime().toString());
                    if (dayWeak == dateT.get(Calendar.DAY_OF_WEEK)) return true;
                }
                break;
            }
            case EVERMONTH: {
                DateDaily dateDaily = dateLong.get(0);
                Calendar dateT = Calendar.getInstance();
                dateT.setTimeInMillis(dateDaily.longTime);
                int diffYear = dateC.get(Calendar.YEAR) - dateT.get(Calendar.YEAR);
                int diffMonth = diffYear * 12 + dateT.get(Calendar.MONTH) - dateC.get(Calendar.MONTH);
                if (diffMonth % dateDaily.every == 0) return true;
                break;
            }
            case EVERYEAR: {
                DateDaily dateDaily = dateLong.get(0);
                Calendar dateT = Calendar.getInstance();
                dateT.setTimeInMillis(dateDaily.longTime);
                int diffYear = dateC.get(Calendar.YEAR) - dateT.get(Calendar.YEAR);
                if (diffYear % dateDaily.every == 0) return true;
                break;
            }
        }
        return false;
    }

    public String getStringTypeDailyPeriod(Context c){
        Resources r = c.getResources();
        return (typeDaily == Type_Daily.EVERDAY)? r.getString(R.string.fieldDay) :
                (typeDaily == Type_Daily.EVERMONTH) ? r.getString(R.string.fieldMonth) :
                        (typeDaily == Type_Daily.EVERYEAR) ? r.getString(R.string.fieldYear) :
                                r.getString(R.string.fieldWeak);
    }

    public String getStringDatesWeekly(){
        StringBuilder stringBuilder = new StringBuilder();
        List<DateDaily> sortedDateDaily = new ArrayList<>(dates);
        Collections.sort(sortedDateDaily,DateDaily::compareTo);
        for(DateDaily d: sortedDateDaily){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(d.longTime);
            String[] weekDays = DateFormatSymbols.getInstance().getWeekdays();
            stringBuilder.append(weekDays[calendar.get(Calendar.DAY_OF_WEEK)]).append(", ");
        }
        stringBuilder.trimToSize();
        String result = (stringBuilder.length() > 0)? stringBuilder.substring( 0 , stringBuilder.lastIndexOf(",")) : "";
        return result;
    }

    public boolean isHaveDaysWeekInDates(int dayWeek){
        Calendar cldr = Calendar.getInstance();
        //log
        for(DateDaily d: dates){
            cldr.setTimeInMillis(d.longTime);
            Log.d("test2","dates:" + cldr.getTime().toString() +" dayWeek:" +String.valueOf(cldr.get(Calendar.DAY_OF_WEEK) + " methodDayWeek:"+ dayWeek));
        }
        for(DateDaily d : dates){
            cldr.setTimeInMillis(d.longTime);
            if(dayWeek == cldr.get(Calendar.DAY_OF_WEEK)) return true;
        }
        return false;
    }

    @Override
    boolean isCompleteTask(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        resetTime(calendar);

        if(typeDaily == Type_Daily.EVERMONTH){
            Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByIdTask(getId());
            long dateC = c.moveToPosition(c.getCount()-1)? c.getLong(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_DATE_COLUMNAME)) : -1;
            Calendar calendarC = Calendar.getInstance();
            calendarC.setTimeInMillis(dateC);
            resetTime(calendarC);
            return calendarC.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
        }
        if(typeDaily == Type_Daily.EVERYEAR){
            Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByIdTask(getId());
            long dateC = c.moveToPosition(c.getCount()-1)? c.getLong(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_DATE_COLUMNAME)) : -1;
            Calendar calendarC = Calendar.getInstance();
            calendarC.setTimeInMillis(dateC);
            resetTime(calendarC);
            return calendarC.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
        }
        else{
            Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByDate(getId(), calendar.getTimeInMillis());
            return c.getCount()>0;
        }
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
    public void initialTaskInDB(){
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.initialTaskInDB();
        dbLite.execSQL(ManagerDB.INSERT_STRING_DAILY,new String[]{
                String.valueOf(getId()),String.valueOf(getTypeDaily().ordinal())});
        initDateDailyInDb(dbLite);
    }

    @Override
    public void updateInDb() {
        SQLiteDatabase dbLite = ManagerDB.getManagerDB(null).getDbWriteble();
        super.updateInDb();
        dbLite.execSQL(ManagerDB.UPDATE_STRING_DAILY, new String[]{
                String.valueOf(getTypeDaily().ordinal()),  String.valueOf(getId())
        });

        updateDateDailyInDb(dbLite);
    }

    void updateDateDailyInDb(SQLiteDatabase dbLite){
        dbLite.execSQL(ManagerDB.DEL_STRING_DATEDAILYBYID, new String[]{
                String.valueOf(getId())
        });
        initDateDailyInDb(dbLite);
    }

    void initDateDailyInDb(SQLiteDatabase dbLite){
        List<DateDaily> dateDailies = (List<DateDaily>) getDateTask();
        for(DateDaily dateDaily : dateDailies)
            dbLite.execSQL(ManagerDB.INSERT_STRING_DATEDAILY, new String[]{
                    String.valueOf(getId()), String.valueOf(dateDaily.longTime), String.valueOf(dateDaily.every)
            });
    }



    public static AbsTask initTaskByCursor(Cursor cur){
        Daily daily = new Daily(0);
        initTaskByCursor(cur,daily);
        Type_Daily typeDaily = Type_Daily.values()[cur.getInt(cur.getColumnIndex(ManagerDB.DAILYTYPE_COLUMNNAME))];
        daily.setTypeDaily(typeDaily);
        Cursor c =
                ManagerDB.getManagerDB(null).getDbReadable().rawQuery(ManagerDB.SEL_STRING_DATEDAILYBYTASK,
                        new String[]{ String.valueOf(daily.getId())});
        List<DateDaily> dateDailies = (List<DateDaily>) daily.getDateTask();
        while(c.moveToNext()) {
            DateDaily dateDaily = new DateDaily(c.getInt(c.getColumnIndex(ManagerDB.DAILYEVERY_COLUMNNAME)),
                    c.getLong(c.getColumnIndex(ManagerDB.DAILYDATE_COLUMNNAME)));
            dateDailies.add(dateDaily);
        }
        return daily;
    }


    public Type_Daily getTypeDaily() {
        return typeDaily;
    }

    public void setTypeDaily(Type_Daily typeDaily) {
        this.typeDaily = typeDaily;
    }

    public int getEvery(){
        return (getDateTask().size() == 0)? 0 : ((DateDaily)getDateTask().get(0)).every;
    }

    @Override
    public List<?> getDateTask() {
        return dates;
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

    public void onNumberEveryChanges(CharSequence s, int start, int before, int count){
        if(s.length() <= 0 || dates.size() < 1) return;
        DateDaily dateDaily = dates.get(0);
        dateDaily.every = Integer.valueOf(String.valueOf(s));
    }

    public void onItemSelectedTypeDaily(AdapterView<?> parent, View view, int pos, long id){
        Type_Daily typeDaily = Type_Daily.values()[pos];
        Type_Daily prevTypeDaily = getTypeDaily();
        setTypeDaily(typeDaily);
        ExpandableLinearLayout expandableDay = view.getRootView().findViewById(R.id.expand_day);
        ExpandableLinearLayout expandableWeak = view.getRootView().findViewById(R.id.expand_weak);
        ExpandableLinearLayout expandableMonth = view.getRootView().findViewById(R.id.expand_month);
        ExpandableLinearLayout expandableYear = view.getRootView().findViewById(R.id.expand_year);
        expandableDay.setExpanded(false);
        expandableWeak.setExpanded(false);
        expandableMonth.setExpanded(false);
        expandableYear.setExpanded(false);
        if(typeDaily != Type_Daily.EVERWEEK && dates.size() < 1){
            Calendar calendar = Calendar.getInstance();
            resetTime(calendar);
            dates.add(new DateDaily(1, calendar.getTimeInMillis()));
        }
        if(typeDaily != Type_Daily.EVERWEEK && prevTypeDaily == Type_Daily.EVERWEEK){
            dates.clear();
            Calendar calendar = Calendar.getInstance();
            resetTime(calendar);
            dates.add(new DateDaily(1, calendar.getTimeInMillis()));
            updateTextEvery(view.getRootView(), 1);
        }
        else if(typeDaily == Type_Daily.EVERWEEK && prevTypeDaily != Type_Daily.EVERWEEK){
            dates.clear();
            initCheckTextWeekDay(view.getRootView());
        }
        else if(typeDaily != Type_Daily.EVERWEEK && prevTypeDaily != Type_Daily.EVERWEEK){
            updateTextEvery(view.getRootView(), dates.get(0).every);
        }
        switch (typeDaily){
            case EVERDAY:
                expandableDay.expand();
                break;
            case EVERWEEK:
                expandableWeak.expand();
                break;
            case EVERMONTH:
                expandableMonth.expand();
                break;
            case EVERYEAR:
                expandableYear.expand();
                break;
        }
    }


    private void updateTextEvery(View view, int every){
        EditText editText = view.findViewById(R.id.editTextEveryDay);
        editText.setText(String.valueOf(every));
        editText = view.findViewById(R.id.editTextEveryMonth);
        editText.setText(String.valueOf(every));
        editText = view.findViewById(R.id.editTextEveryYear);
        editText.setText(String.valueOf(every));
    }

    private void initCheckTextWeekDay(View view){
        CheckedTextView checkedTextView = view.findViewById(R.id.chMonday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.MONDAY));
        checkedTextView = view.findViewById(R.id.chTuesday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.TUESDAY));
        checkedTextView = view.findViewById(R.id.chWednesday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.WEDNESDAY));
        checkedTextView = view.findViewById(R.id.chThursday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.THURSDAY));
        checkedTextView = view.findViewById(R.id.chFriday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.FRIDAY));
        checkedTextView = view.findViewById(R.id.chSaturday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.SATURDAY));
        checkedTextView = view.findViewById(R.id.chSunday);
        checkedTextView.setChecked(isHaveDaysWeekInDates(Calendar.SUNDAY));
    }

    public Date[] getDateCurrentWeek(){
        Date[] daysOfWeek = new Date[7];
        Calendar now = Calendar.getInstance();
        resetTime(now);
        now.set(Calendar.DAY_OF_WEEK, now.getFirstDayOfWeek());
        for(int i = 0; i < 7; i++){
            daysOfWeek[i] = now.getTime();
            now.add(Calendar.DAY_OF_MONTH,1);
        }
        for(Date d : daysOfWeek){
            Log.d("testGetDateCurrentWeek",d.toString());
        }
        return daysOfWeek;
    }

    public void onClickItemDayWeekly(View v){
        CheckedTextView checkedTextView = (CheckedTextView) v;
        checkedTextView.setChecked(!checkedTextView.isChecked());
        Date[] daysOfWeek = getDateCurrentWeek();
        DateDaily dateDaily = null;
        switch (v.getId()){
            case R.id.chMonday:
                dateDaily = new DateDaily(0, daysOfWeek[0].getTime());
                break;
            case R.id.chTuesday:
                dateDaily = new DateDaily(0, daysOfWeek[1].getTime());
                break;
            case R.id.chWednesday:
                dateDaily = new DateDaily(0, daysOfWeek[2].getTime());
                break;
            case R.id.chThursday:
                dateDaily = new DateDaily(0, daysOfWeek[3].getTime());
                break;
            case R.id.chFriday:
                dateDaily = new DateDaily(0, daysOfWeek[4].getTime());
                break;
            case R.id.chSaturday:
                dateDaily = new DateDaily(0, daysOfWeek[5].getTime());
                break;
            case R.id.chSunday:
                dateDaily = new DateDaily(0, daysOfWeek[6].getTime());
                break;
        }
        if(checkedTextView.isChecked()){
            dates.add(dateDaily);
        }
        else dates.remove(dates.indexOf(dateDaily));
    }

    public enum Type_Daily{
        EVERDAY,
        EVERWEEK,
        EVERMONTH,
        EVERYEAR
    }

    public static class DateDaily implements Serializable,Comparable {
        int every;
        long longTime;
        DateDaily(int every, long longTime){
            this.every = (every < 1)? 1 : every;
            this.longTime = longTime;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return this.longTime == ((DateDaily)obj).longTime;
        }


        @Override
        public int compareTo(Object o) {
            return Long.compare(longTime, ((DateDaily) o).longTime);
        }
    }

    /**
     * Build view daily for listItem, ViewViewerTask, ViewEditorTask
     */
    public static class BuilderView extends AbsTask.BuilderView<Daily>{

        BuilderView(@Nullable Daily object, @Nullable LayoutInflater inflater) {
            super(object, inflater);
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
        public View createBasicViewItem(ViewGroup parent) {
            View view = getInflater().inflate(R.layout.daily_item, parent, isAttachToRoot());
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
