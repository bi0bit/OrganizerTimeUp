package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;


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
    List<AbsTask> tasks;
    LayoutInflater inflater;


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

    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            AbsTask.BuilderView builder = tasks.get(position).getBuilderView();
            builder.setObject(tasks.get(position));
        if(convertView == null) {
            builder.setInflater(inflater);
            builder.setParent(parent);
            convertView = tasks.get(position).createView();
        }
        else {
            builder.setViewItemTask(convertView);
        }

        return convertView;
    }

    class ViewHolder{

        ViewHolder(ViewHolder holder){

        }

    }

}

