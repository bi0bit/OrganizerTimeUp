package by.ilago_project.timeUp_ManagerTime;

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
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckListAdapter extends ArrayAdapter<CheckTask> {

    private List<CheckTask> tasks;
    private LayoutInflater inflater;

    private boolean editable = true;

    public CheckListAdapter(@NonNull Context context, @NonNull List<CheckTask> objects) {
        super(context, 0, objects);
        tasks = objects;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CheckTask getItem(int position) {
        return tasks.get(position);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    private void onClickEditCheck(View view, ViewGroup parent, CheckTask checkTask){
        QDialog.SetterGetterDialogEdit dialogSG = new QDialog.SetterGetterDialogEdit();
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventEditUnderTask))
                .setCancelable(true)
                .setSetterGetterDialog(dialogSG)
                .setOnClickPositiveBtn((dialog, which)->{
                    if(!dialogSG.getUserInputString().isEmpty()) {
                        checkTask.setText(dialogSG.getUserInputString());
                        this.notifyDataSetChanged();
                        MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                    }else Toast.makeText(view.getContext(),R.string.eventEmptyField,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
        AlertDialog.Builder alertDialog = QDialog.make(builder,view,QDialog.DIALOG_INPUT_STRING);
        dialogSG.setLabelString(view.getResources().getString(R.string.fieldEnterNameUnderTask));
        dialogSG.setUserInputString(checkTask.getText());
        alertDialog.show();
    }

    private void onClickDeleteCheck(View view, ViewGroup parent, CheckTask checkTask){
        QDialog.Builder builder = QDialog.getBuilder();
        builder.setTitle(view.getResources().getString(R.string.eventDeleteUnderTask))
                .setMessage(view.getResources().getString(R.string.askDeleteUnderTask))
                .setCancelable(true)
                .setOnClickPositiveBtn((dialog, which) -> {
                    tasks.remove(checkTask);
                    this.notifyDataSetChanged();
                    MainAppActivity.setListViewHeightBasedOnChildren((ListView) parent);
                });
        AlertDialog.Builder alertDialog = QDialog.make(builder, view, QDialog.DIALOG_QUATION);
        alertDialog.show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckTask checkTask = getItem(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_check_list,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setChecked(checkTask.isCompleteTask());
        holder.checkBox.setText(checkTask.getText());
        holder.checkBox.setOnCheckedChangeListener((v,c)->{});
        View finalConvertView = convertView;
        holder.editButton.setOnClickListener((v)-> onClickEditCheck(finalConvertView, parent, checkTask));
        holder.deleteButton.setOnClickListener((v)-> onClickDeleteCheck(finalConvertView, parent, checkTask));
        if(!editable){
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.editButton) Button editButton;
        @BindView(R.id.deleteButton) Button deleteButton;
        @BindView(R.id.checkbox) CheckBox checkBox;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
