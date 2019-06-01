package by.ilagoproject.timeUp_ManagerTime;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import AssambleClassManagmentApp.AbsTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TaskAdapterList extends ArrayAdapter<AbsTask> {
    private List<AbsTask> tasks;
    private final LayoutInflater inflater;


    public TaskAdapterList(Context context, List<AbsTask> objects) {
        super(context, 0, objects);
        this.tasks = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public AbsTask getItem(int position) {
        return tasks.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            AbsTask.BuilderView builder = tasks.get(position).getBuilderView();
            builder.setObject(tasks.get(position));
        if(convertView == null) {
            builder.setInflater(inflater);
            convertView = tasks.get(position).createView(parent);
        }
        else {
            builder.setViewItemTask(convertView);
        }

        return convertView;
    }

}

