package by.ilagoproject.timeUp_ManagerTime;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import static android.content.DialogInterface.OnClickListener;

public final class QDialog {

    @IntDef({DIALOG_INPUT_STRING,DIALOG_QUATION,DIALOG_LIST,DIALOG_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DialogType{}

    public static final int DIALOG_INPUT_STRING = 0b01;
    public static final int DIALOG_QUATION = 0b10;
    public static final int DIALOG_LIST = 0b11;
    public static final int DIALOG_CUSTOM = 0b100;
    public static final OnClickListener STANDARD_ONCLICK_BUTTON = (dialog, which) -> dialog.cancel();
    private static final int standard_STYLEDIALOG = R.style.appThemeDialog;

    public static Builder getBuilder() {
        return new Builder();
    }

    @NonNull
    public static AlertDialog.Builder make(@NonNull Builder builder, View view, @DialogType int dialogType) {
        return builder.buildDialog(view, dialogType);
    }

    public static class Builder{
        private boolean cancelable = false;
        private String message;
        private String title;
         @StringRes
        private int neutralBtnStr,positiveBtnStr,negativeBtnStr;
         @LayoutRes
        private int idView;
         @StyleRes
        private int styleDialog;
        private String[] items;


        private int color;

        private Drawable iconDialog;

        private SetterGetterDialog setterGetterDialog;
        private OnClickListener onClickPositiveBtn;
        private OnClickListener onClickNegativeBtn;
        private OnClickListener onClickNeutralBtn;
        private OnClickListener onClickItem;

        Builder(){
            reset();
        }

        private void reset(){
            cancelable = false;
            message = "";
            title = "";
            color = R.color.GreenTextApp;
            styleDialog = standard_STYLEDIALOG;
            iconDialog = null;
            onClickNegativeBtn = STANDARD_ONCLICK_BUTTON;
            onClickPositiveBtn = null;
            onClickNeutralBtn = null;
            onClickItem = null;
            neutralBtnStr = R.string.dialog_CANCEL;
            positiveBtnStr = R.string.dialog_OK;
            negativeBtnStr = R.string.dialog_BACK;
        }

        public int getColorTitle() {
            return color;
        }

        public Builder setColorTitle(@ColorRes int color) {
            this.color = color;
            return this;
        }

        public Drawable getIconDialog() {
            return iconDialog;
        }

        public Builder setIconDialog(Drawable iconDialog) {
            this.iconDialog = iconDialog;
            return this;
        }

        public boolean isCancelable() {
            return cancelable;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setItems(String... items) {
            this.items = items;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setNeutralBtnStr(int neutralBtnStr) {
            this.neutralBtnStr = neutralBtnStr;
            return this;
        }

        public Builder setPositiveBtnStr(int positiveBtnStr) {
            this.positiveBtnStr = positiveBtnStr;
            return this;
        }

        public Builder setNegativeBtnStr(int negativeBtnStr) {
            this.negativeBtnStr = negativeBtnStr;
            return this;
        }

        public int getIdView() {
            return idView;
        }

        public Builder setIdView(int idView) {
            this.idView = idView;
            return this;
        }

        public Builder setStyleDialog(int styleDialog) {
            this.styleDialog = styleDialog;
            return this;
        }

        public SetterGetterDialog getSetterGetterDialog() {
            return setterGetterDialog;
        }

        public Builder setSetterGetterDialog(SetterGetterDialog setterGetterDialog) {
            this.setterGetterDialog = setterGetterDialog;
            return this;
        }

        public Builder setOnClickPositiveBtn(OnClickListener onClickPositiveBtn) {
            this.onClickPositiveBtn = onClickPositiveBtn;
            return this;
        }

        public Builder setOnClickNegativeBtn(OnClickListener onClickNegativeBtn) {
            this.onClickNegativeBtn = onClickNegativeBtn;
            return this;
        }


        public Builder setOnClickItem(OnClickListener onClickItem) {
            this.onClickItem = onClickItem;
            return this;
        }

        public Builder setOnClickNeutralBtn(OnClickListener onClickNeutralBtn) {
            this.onClickNeutralBtn = onClickNeutralBtn;
            return this;
        }

        public AlertDialog.Builder buildDialog(View viewParent, @DialogType int createDialog){
            AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(viewParent.getContext(),styleDialog);
            View viewInflated;
            SpannableStringBuilder ssbTitle = new SpannableStringBuilder(getTitle());
            ssbTitle.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(viewParent.getContext(),getColorTitle())),
                    0,
                    getTitle().length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dialogBuilder.setTitle(ssbTitle)
                    .setCancelable(isCancelable())
                    .setPositiveButton(positiveBtnStr,(onClickPositiveBtn != null)? onClickPositiveBtn : STANDARD_ONCLICK_BUTTON)
                    .setNegativeButton(negativeBtnStr, onClickNegativeBtn);

            if (getIconDialog() != null) {
                dialogBuilder.setIcon(getIconDialog());
            }

            if(onClickNeutralBtn != null){
                dialogBuilder.setNeutralButton(neutralBtnStr, onClickNeutralBtn);
            }
            switch (createDialog){
                case DIALOG_INPUT_STRING:
                    viewInflated = LayoutInflater.from(viewParent.getContext()).inflate(R.layout.dialog_input_string, (ViewGroup) viewParent,false);
                    if(setterGetterDialog == null)
                        setSetterGetterDialog(new SetterGetterDialogEdit());
                    setterGetterDialog.setView(viewInflated);
                    dialogBuilder.setView(viewInflated);
                    break;
                case DIALOG_QUATION:
                    dialogBuilder.setMessage(message);
                    break;
                case DIALOG_LIST:
                    dialogBuilder.setItems(items, (onClickItem != null)? onClickItem : STANDARD_ONCLICK_BUTTON);
                    break;
                case DIALOG_CUSTOM:
                    viewInflated = LayoutInflater.from(viewParent.getContext()).inflate(getIdView(), (ViewGroup) viewParent,false);
                    if(setterGetterDialog == null)
                        setSetterGetterDialog(new SetterGetterDialogCustom());
                    setterGetterDialog.setView(viewInflated);
                    dialogBuilder.setView(viewInflated);
                    break;
            }
            return dialogBuilder;
        }
    }

    public static class SetterGetterDialogCustom extends SetterGetterDialog{
        int[] valuesId;
        View[] valuesView;

        public int getValuesId(int pos) {
            return valuesId[pos];
        }

        public void setValuesId(int[] valuesId) {
            this.valuesId = valuesId;
        }

        @NonNull
        public View getValuesView(int pos) {
            if (pos<0 || pos > getLengthValues() - 1) throw new NullPointerException();
            return valuesView[pos];
        }

        private int getLengthValues(){
            return (valuesView != null)? valuesView.length : 0;
        }

        private void setValuesView() {
            valuesView = new View[valuesId.length];
            View rootView = super.view;
            for(int i = 0; i < valuesId.length; i++){
                valuesView[i] = rootView.findViewById(valuesId[i]);
            }
        }

        public void setValueView(int pos, String mutatorName, @NonNull Class<?>[] paramTypes, @NonNull Object[] objectSet){
            View view = getValuesView(pos);
            Class<?> cls = view.getClass();
            Method method;
            try {
                method = cls.getMethod(mutatorName,paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new NullPointerException(e.getMessage());
            }

            try {
                method.invoke(view,objectSet);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Nullable
        public Object getValueView(int pos, String accesorName, @Nullable Class<?>[] paramTypes, @Nullable Object[] objectSet){
            View view = getValuesView(pos);
            Class<?> cls = view.getClass();
            Method method = null;
            try {
                method = cls.getMethod(accesorName,paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Object obj = null;
            if(method != null)
            try {
                obj = method.invoke(view,objectSet);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected void setView(View view) {
            super.setView(view);
            if(valuesId != null && valuesId.length > 0)
                setValuesView();
        }
    }

    public static class SetterGetterDialogEdit extends SetterGetterDialog{
        EditText userInput;
        TextView label;

        public void setUserInputString(String text){
          if(userInput != null)
              userInput.setText(text);
        }

        public String getUserInputString(){
          return  (userInput != null)? userInput.getText().toString() : null;
        }

        public void setLabelString(String text){
          if(label != null)
              label.setText(text);
        }

        @Override
        protected void setView(View view){
            super.setView(view);
            userInput = view.findViewById(R.id.input_text);
            label = view.findViewById(R.id.text1);
        }
    }

    public static abstract class SetterGetterDialog{
        View view;
        protected void setView(View view){
            this.view = view;
        }
    }
}
