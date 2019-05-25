package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterArrayPriorityType extends ArrayAdapter<String> {
    List<String> strings;
    LayoutInflater inflater;

    public AdapterArrayPriorityType(Context context, List<String> objects){
        super(context,0,objects);
        this.strings = objects;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public String getItem(int position) {
        return strings.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_spinner_dropdown_priority_task, parent, false);
        CheckedTextView textview = view.findViewById(R.id.textDropdownPriority);
        textview.setText(getItem(position));
        ImageView imageView = view.findViewById(R.id.imageDropdownPriority);
        imageView.setImageResource(
                ((position == 0)? R.drawable.rectengle_priority_common : (position==1)? R.drawable.rectengle_priority_impotant : R.drawable.rectengle_priority_necessary)
        );
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_spinner_priority_task, parent, false);
        TextView textview = view.findViewById(R.id.textDropdownPriority);
        textview.setText(strings.get(position));
        ImageView imageView = view.findViewById(R.id.imageDropdownPriority);
        imageView.setImageResource(
                ((position == 0)? R.drawable.rectengle_priority_common : (position==1)? R.drawable.rectengle_priority_impotant : R.drawable.rectengle_priority_necessary)
        );
        return view;
    }
}
