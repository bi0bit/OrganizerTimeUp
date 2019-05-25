package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import AssambleClassManagmentApp.NotificationTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class NotifyAdapterList extends ArrayAdapter<NotificationTask> {

    List<NotificationTask> objects;
    LayoutInflater inflater;

    public NotifyAdapterList(@NonNull  Context context, @NonNull List<NotificationTask> objects){
        super(context,0,objects);
        this.objects = objects;
        inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public NotificationTask getItem(int position) {
        return objects.get(position);
    }

    public void onClickEditButton(View view, ViewGroup parent, NotificationTask notificationTask){
        Calendar cldDate = Calendar.getInstance();
        cldDate.setTimeInMillis(notificationTask.getDateAlarm());

        Calendar cldTime= Calendar.getInstance();
        cldTime.setTimeInMillis(notificationTask.getTimeAlarm());

        QDialog.Builder builder = QDialog.getBuilder();
        QDialog.SetterGetterDialogCustom dialogSG = new QDialog.SetterGetterDialogCustom();
        dialogSG.setValuesId(new int[]{
                R.id.title_notify,
                R.id.message_notify,
                R.id.time,
                R.id.date,
                R.id.buttonSelectTime,
                R.id.buttonSelectDate});
        builder.setTitle(view.getResources().getString(R.string.eventEditNotification))
                .setSetterGetterDialog(dialogSG)
                .setIdView(R.layout.dialog_notify_view)
                .setCancelable(true)
                .setPositiveBtn((dialog,which)->{
                    Object result = dialogSG.getValueView(0,"getText",null,null);
                    String title = result.toString();

                    result = dialogSG.getValueView(1, "getText",null,null);
                    String message = result.toString();

                    if (title.isEmpty()){
                        Toast.makeText(view.getContext(), R.string.eventEmptyField, Toast.LENGTH_LONG).show();
                        return;
                    }

                    notificationTask.setTitle(title);
                    notificationTask.setMessage(message);
                    notificationTask.setDateAlarm(cldDate.getTimeInMillis());
                    notificationTask.setTimeAlarm(cldTime.getTimeInMillis());

                    notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);

                    dialog.dismiss();
                });
        AlertDialog.Builder dialog = builder.buildDialog(view,QDialog.DIALOG_CUSTOM);
        dialogSG.setValueView(4,"setOnClickListener",
                new Class[]{View.OnClickListener.class},
                new View.OnClickListener[]{(v2) ->{
                    TimePickerDialog pickerDialog = new TimePickerDialog(view.getContext(),
                            (v3,h,m)->{
                                String hours = (h >= 10)? String.valueOf(h) : "0" + h;
                                String min = (m >= 10)? String.valueOf(m) : "0" + m;
                                dialogSG.setValueView(2,"setText",new Class[]{CharSequence.class},new Object[]{
                                        hours + ":" + min});
                                cldTime.set(Calendar.HOUR_OF_DAY, h);
                                cldTime.set(Calendar.MINUTE, m);
                            },cldTime.get(Calendar.HOUR_OF_DAY), cldTime.get(Calendar.MINUTE),true);

                    pickerDialog.show();

                }});
        dialogSG.setValueView(5,"setOnClickListener",
                new Class[]{View.OnClickListener.class},
                new View.OnClickListener[]{(v2) ->{
                    DatePickerDialog pickerDialog = new DatePickerDialog(view.getContext(),
                            (v3,y,m,d)->{
                                String mouth  = (m >= 10)? String.valueOf(m) : "0" + m;
                                String day = (d >= 10)? String.valueOf(d) : "0" + d;
                                String date = day + "." + mouth + "." + y;
                                dialogSG.setValueView(3,"setText",
                                        new Class[]{CharSequence.class},
                                        new Object[]{date});
                                cldDate.set(y,m,d);
                            },cldDate.get(Calendar.YEAR), cldDate.get(Calendar.MONTH),cldDate.get(Calendar.DAY_OF_MONTH));
                    pickerDialog.show();
                }});


        dialogSG.setValueView(0,"setText", new Class[]{CharSequence.class},
                new Object[]{notificationTask.getTitle()});
        dialogSG.setValueView(1,"setText", new Class[]{CharSequence.class},
                new Object[]{notificationTask.getMessage()});
        dialogSG.setValueView(2,"setText", new Class[]{CharSequence.class},
                new Object[]{new SimpleDateFormat("HH:mm").format(cldTime.getTime())} );
        dialogSG.setValueView(3,"setText", new Class[]{CharSequence.class},
                new Object[]{new SimpleDateFormat("dd.MM.yyyy").format(cldDate.getTime())} );
        dialog.show();
    }

    public void onClickDeleteButton(View view, ViewGroup parent, NotificationTask notificationTask){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventDeleteNotification))
                .setMessage(view.getResources().getString(R.string.askDeleteNotification))
                .setCancelable(true)
                .setPositiveBtn(((dialog, which) -> {
                    objects.remove(notificationTask);
                    notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                }));
        AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_QUATION);
        alertDialog.show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NotificationTask notificationTask = getItem(position);

        View view = inflater.inflate(R.layout.item_notify_list,parent,false);

        TextView viewTitle = view.findViewById(R.id.title);
        viewTitle.setText(notificationTask.getTitle());
        TextView viewTime = view.findViewById(R.id.time);

        String time = new SimpleDateFormat("HH:mm").format(new Date(notificationTask.getTimeAlarm()));
        String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date(notificationTask.getDateAlarm()));
        date = time + " " + date;
        viewTime.setText(date);

        Button buttonEdit = view.findViewById(R.id.editButton);
        buttonEdit.setOnClickListener(v -> onClickEditButton(view,parent,notificationTask));
        Button buttonDel = view.findViewById(R.id.deleteButton);
        buttonDel.setOnClickListener(v -> onClickDeleteButton(view,parent,notificationTask));

        return view;
    }
}
