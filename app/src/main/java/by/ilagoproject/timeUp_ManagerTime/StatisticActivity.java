package by.ilagoproject.timeUp_ManagerTime;

import AssambleClassManagmentTime.AbsTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        AbsTask task = getIntent().getParcelableExtra("object");
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        //init statistic
        LineChart chartLine = findViewById(R.id.lineDiagramCompleteTask);
        List<Entry> entries = new ArrayList<>();
        Cursor c = ManagerDB.getManagerDB(null).getCursorOnHistoryCompleteByIdTask(task.getId());
        int maxCount = 0;
        while(c.moveToNext()){
            long date = c.getLong(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_DATE_COLUMNAME));
            int count = c.getInt(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_COUNT_COLUMNAME));
            if(count > maxCount) maxCount = count;
            Log.d("StatisticTest","count: " + count + " data: " + new Date(date).toString());
            int typeComplete = c.getInt(c.getColumnIndex(ManagerDB.HISTORYCOMPLETE_TYPE_COLUMNAME));
            AbsTask.Type_Complete type_complete = AbsTask.Type_Complete.values()[typeComplete];
            entries.add(new Entry(date,count));
        }
        LineDataSet dataSet = new LineDataSet(entries, task.getName());
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(getResources().getColor(R.color.FieldApp));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(getResources().getColor(R.color.FieldApp));
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
        dataSet.setValueTextSize(12f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setHighlightEnabled(false);
        XAxis xAxis = chartLine.getXAxis();
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                return dateFormat.format(new Date((long) value));
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        LimitLine limitLine = new LimitLine(maxCount, "max:"+maxCount);
        limitLine.setTextSize(10f);
        limitLine.setTextColor(Color.RED);
        limitLine.setTextStyle(Paint.Style.FILL);
        limitLine.setLineColor(Color.RED);

        YAxis yAxis = chartLine.getAxisLeft();
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);

        yAxis.addLimitLine(limitLine);

        chartLine.getAxisRight().setEnabled(false);
        chartLine.getDescription().setEnabled(false);
        chartLine.setData(new LineData(dataSet));
        chartLine.setDragEnabled(false);
        chartLine.setScaleEnabled(false);
        chartLine.setTouchEnabled(false);
        chartLine.setDoubleTapToZoomEnabled(false);
//        chartLine.se
        chartLine.invalidate();
    }
}
