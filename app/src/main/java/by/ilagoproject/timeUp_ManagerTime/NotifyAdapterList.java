package by.ilagoproject.timeUp_ManagerTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import AssambleClassManagmentTime.NotificationTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotifyAdapterList extends ArrayAdapter<NotificationTask> {

    private final List<NotificationTask> tasks;
    private final LayoutInflater inflater;
    private final ClickItemButton onClickEditButton;
    private final ClickItemButton onClickDeleteButton;

    private boolean showDate = false;
    private boolean editable = true;

    public NotifyAdapterList(@NonNull  Context context, @NonNull List<NotificationTask> objects, ClickItemButton editButton, ClickItemButton delButton){
        super(context,0,objects);
        this.tasks = objects;
        inflater = LayoutInflater.from(context);
        onClickEditButton = editButton;
        onClickDeleteButton = delButton;
    }


    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }



    @NonNull
    @Override
    public NotificationTask getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public void remove(@Nullable NotificationTask object) {
        tasks.remove(object);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @NonNull NotificationTask notificationTask = getItem(position);
        ViewHolder holder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_notify_list,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(notificationTask.getTimeAlarm()));
        String date = (showDate)? new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(notificationTask.getDateAlarm())) : "";
        date = time + " " + date;
        holder.title.setText(notificationTask.getTitle());
        holder.time.setText(date);
        View finalConvertView = convertView;
        holder.editButton.setOnClickListener(v -> onClickEditButton.click(finalConvertView, parent, notificationTask, this));
        holder.deleteButton.setOnClickListener(v -> onClickDeleteButton.click(finalConvertView, parent, notificationTask, this));
        if(!editable) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        @BindView(R.id.title) TextView title;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.editButton) Button editButton;
        @BindView(R.id.deleteButton) Button deleteButton;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickItemButton{
        void click(View view, ViewGroup parent, NotificationTask notify, NotifyAdapterList adapterList);
    }
}
