package by.ilagoproject.timeUp_ManagerTime;

import AssambleClassManagmentTime.AbsTask;
import AssambleClassManagmentTime.Habit;
import butterknife.BindView;
import butterknife.ButterKnife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticActivity extends AppCompatActivity {

    AbsTask task;
    List<HistoryComplete> history = new ArrayList<>();

    @BindView(R.id.appBar) Toolbar toolbar;
    @BindView(R.id.lineDiagramCompleteTask) LineChart chartLine;
    @BindView(R.id.listHistory) ListView listHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        task = getIntent().getParcelableExtra("object");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init statistic
        List<Entry> entries = new ArrayList<>();
        Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByIdTask(task.getId());

        int maxCount = 0;
        while(c.moveToNext()){
            long date = c.getLong(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_DATE_COLUMNAME));
            int count = c.getInt(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_COUNT_COLUMNAME));
            if(count > maxCount) maxCount = count;
            int intComplete = c.getInt(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_TYPE_COLUMNAME));
            AbsTask.Type_Complete type_complete = AbsTask.Type_Complete.values()[intComplete];
            history.add(new HistoryComplete(date, count, type_complete));
        }



        List<HistoryComplete> history1 = new ArrayList<>(history);
        Collections.sort(history1, (o1,o2)->Long.compare(o2.date,o1.date));
        listHistory.setAdapter(new HistoryCompleteAdapter(this, history1));

        Collections.sort(history, (o1,o2)->Long.compare(o1.date,o2.date));

        int i = 0;
        for(HistoryComplete h : history){
            entries.add(new Entry(i,h.count));
            i++;
        }

        if(history.size()<=1) return;

        LineDataSet dataSet = new LineDataSet(entries, task.getName());
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        int colorLine = (task.TYPE == AbsTask.Type_Task.HABIT && ((Habit)task).getTypeHabitInt() < 0)? getResources().getColor(R.color.RedComponent) :
                getResources().getColor(R.color.GreenComponent);
        dataSet.setColor(colorLine);
        dataSet.setCircleRadius(4f);

        dataSet.setCircleColor(colorLine);
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setHighlightEnabled(false);
        int colorFill = (task.TYPE == AbsTask.Type_Task.HABIT && ((Habit)task).getTypeHabitInt() < 0)? getResources().getColor(R.color.DiagramColor_FillNegative) :
                getResources().getColor(R.color.DiagramColor_Fill);
        dataSet.setFillColor(colorFill);
        dataSet.setDrawFilled(true);
        XAxis xAxis = chartLine.getXAxis();
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Log.d("StatisticTest", "value: " + value);
                return dateFormat.format(new Date(history.get((int) value).date));
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);
        xAxis.setXOffset(-120f);
        xAxis.setLabelCount(5, true);

        LimitLine limitLine = new LimitLine(maxCount, "Max");
        limitLine.setLineWidth(1f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setTypeface(Typeface.SANS_SERIF);
        limitLine.setTextStyle(Paint.Style.STROKE);
        limitLine.setTextSize(12f);
        limitLine.setTextColor(getResources().getColor(R.color.DiagramColor_Max));
        limitLine.setLineColor(getResources().getColor(R.color.DiagramColor_Max));
        limitLine.enableDashedLine(8f, 8f, 8f);

        YAxis yAxis = chartLine.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        yAxis.addLimitLine(limitLine);

        chartLine.getAxisRight().setEnabled(false);
        chartLine.getDescription().setEnabled(false);
        chartLine.setData(new LineData(dataSet));
        chartLine.setDragEnabled(false);
        chartLine.setScaleEnabled(false);
        chartLine.setTouchEnabled(false);
        chartLine.setDoubleTapToZoomEnabled(false);
        chartLine.setBackground(new ColorDrawable(getResources().getColor(R.color.BackgroundLight)));
        chartLine.setExtraLeftOffset(38f);
        chartLine.setExtraRightOffset(38f);
        chartLine.animateXY(3000, 3000);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    static class HistoryComplete{
        long date;
        int count;
        AbsTask.Type_Complete complete;
        HistoryComplete(long date, int count, AbsTask.Type_Complete complete){
            this.date = date;
            this.count = count;
            this.complete = complete;
        }
    }

    class HistoryCompleteAdapter extends ArrayAdapter<HistoryComplete> {

        LayoutInflater inflater;
        List<HistoryComplete> objects;

        public HistoryCompleteAdapter(@NonNull Context context, @NonNull List<HistoryComplete> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            HistoryComplete history = objects.get(position);
            ViewHolder holder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_history_complete, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else holder = (ViewHolder) convertView.getTag();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            holder.dateComplete.setText(dateFormat.format(new Date(history.date)));

            holder.countSeries.setText(String.valueOf(history.count));

            String typeComplete = history.complete == AbsTask.Type_Complete.COMPLETE ?
                    convertView.getResources().getStringArray(R.array.typeComplete)[0] : convertView.getResources().getStringArray(R.array.typeComplete)[1];
            holder.typeComplete.setText(typeComplete);
            boolean isBadHabit = task.TYPE == AbsTask.Type_Task.HABIT && ((Habit)task).getTypeHabit().contains(Habit.Type_Habit.NEGATIVE);
            int colorGood = convertView.getResources().getColor(R.color.GreenComponent);
            int colorBad = convertView.getResources().getColor(R.color.RedComponent);
            int colorTypeComplete =  history.complete == AbsTask.Type_Complete.COMPLETE ? (isBadHabit ? colorBad : colorGood) :
                    (isBadHabit ? colorGood : colorBad);
            holder.typeComplete.setText(typeComplete);
            holder.typeComplete.setTextColor(colorTypeComplete);

            return convertView;
        }

        class ViewHolder{
            @BindView(R.id.dateComplete) TextView dateComplete;
            @BindView(R.id.countSeries) TextView countSeries;
            @BindView(R.id.typeComplete) TextView typeComplete;
            ViewHolder(View v){
                ButterKnife.bind(this, v);
            }
        }
    }
}
