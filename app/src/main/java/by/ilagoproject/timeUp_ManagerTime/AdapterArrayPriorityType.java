package by.ilagoproject.timeUp_ManagerTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterArrayPriorityType extends ArrayAdapter<String> {
    private final List<String> strings;
    private final LayoutInflater inflater;

    private AdapterArrayPriorityType(Context context, List<String> objects){
        super(context,0,objects);
        this.strings = objects;
        this.inflater = LayoutInflater.from(context);
    }

    public AdapterArrayPriorityType(Context context){
        this(context, Arrays.asList(context.getResources().getStringArray(R.array.strings_priority_type_task)));
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
        imageView.setImageResource(getIdImageResourceByPriority(position));
        return view;
    }

    @DrawableRes
    private int getIdImageResourceByPriority(int position){
         return ((position == 0)? R.drawable.rectengle_priority_common : (position==1)? R.drawable.rectengle_priority_impotant : R.drawable.rectengle_priority_necessary);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_spinner_priority_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
        holder.textView.setText(strings.get(position));
        int resourceDrawablePriority = (position == 0)? R.drawable.rectengle_priority_common : (position==1)?
                R.drawable.rectengle_priority_impotant : R.drawable.rectengle_priority_necessary;
        holder.imgView.setImageResource(resourceDrawablePriority);
        return convertView;
    }

    class ViewHolder{
        @BindView(R.id.textDropdownPriority) TextView textView;
        @BindView(R.id.imageDropdownPriority) ImageView imgView;
        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
