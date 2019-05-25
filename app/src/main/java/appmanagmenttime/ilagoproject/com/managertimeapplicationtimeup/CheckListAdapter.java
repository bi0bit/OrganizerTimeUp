package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import AssambleClassManagmentApp.CheckTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class CheckListAdapter extends ArrayAdapter<CheckTask> {

    List<CheckTask> tasks;
    LayoutInflater inflater;

    private boolean editable = true;

    public CheckListAdapter(@NonNull Context context, @NonNull List<CheckTask> objects) {
        super(context, 0, objects);
        tasks = objects;
        inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public CheckTask getItem(int position) {
        return tasks.get(position);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckTask checkTask = getItem(position);
        View view = inflater.inflate(R.layout.item_check_list,parent,false);
        CheckBox chBox = view.findViewById(R.id.checkbox);
        chBox.setChecked(checkTask.isCompleteTask());
        chBox.setText(checkTask.getText());
        chBox.setOnCheckedChangeListener((v,c)->{});
        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener((v)->{
            QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventEditUnderTask))
                    .setCancelable(true)
                    .setSetterGetterDialog(dialogSG)
                    .setPositiveBtn((dialog,which)->{
                        if(!dialogSG.getUserInputString().isEmpty()) {
                            checkTask.setText(dialogSG.getUserInputString());
                            this.notifyDataSetChanged();
                            MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                        }else Toast.makeText(view.getContext(),R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    });
            AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_INPUT_STRING);
            dialogSG.setLabelString(view.getResources().getString(R.string.fieldEnterNameUnderTask));
            dialogSG.setUserInputString(checkTask.getText());
            alertDialog.show();
        });
        Button deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener((v)->{
            QDialog.Builder builder = QDialog.getBuilder();
            builder.setTitle(view.getResources().getString(R.string.eventDeleteUnderTask))
                    .setMessage(view.getResources().getString(R.string.askDeleteUnderTask))
                    .setCancelable(true)
                    .setPositiveBtn((dialog, which) -> {
                        tasks.remove(checkTask);
                        this.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                    });
            AlertDialog.Builder alertDialog = builder.buildDialog(view,QDialog.DIALOG_QUATION);
            alertDialog.show();
        });
        if(!editable){
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        return view;
    }


}
