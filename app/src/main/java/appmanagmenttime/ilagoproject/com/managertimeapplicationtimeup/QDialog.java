package appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup;

import android.content.DialogInterface;
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

public final class QDialog {

    @IntDef({DIALOG_INPUT_STRING,DIALOG_QUATION,DIALOG_LIST,DIALOG_CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType{}

    public static final int DIALOG_INPUT_STRING = 0b01;
    public static final int DIALOG_QUATION = 0b10;
    public static final int DIALOG_LIST = 0b11;
    public static final int DIALOG_CUSTOM = 0b100;
    private static final int STANDART_STYLEDIALOG = R.style.appThemeDialog;
    private static final DialogInterface.OnClickListener STANDART_ONCLICK_CANCEL = (dialog, which) -> dialog.cancel();
    private static int DIALOG_BTN_STR_OK = R.string.dialog_OK;
    private static int DIALOG_BTN_STR_CANCEL = R.string.dialog_CANCEL;
    private static int DIALOG_BTN_STR_CONFIRM = R.string.dialog_CONFIRM;
    private static int DIALOG_BTN_STR_BACK = R.string.dialog_BACK;
    private static int DIALOG_BTN_STR_YES = R.string.dialog_YES;
    private static int DIALOG_BTN_STR_NO = R.string.dialog_NO;

    private static final Builder builder = new Builder();

    public static Builder getBuilder() {
        return builder;
    }
    public static AlertDialog.Builder make(View view, @DialogType int dialogType) {
        return getBuilder().buildDialog(view, dialogType);
    }

    public static class Builder{
        protected boolean cancelable = false;
        protected String message;
        protected String title;
         @StringRes
        protected int neutralBtnStr,positiveBtnStr,negativeBtnStr;
         @LayoutRes
        protected int idView;
         @StyleRes
        protected int styleDialog;


        protected int color;

        protected Drawable iconDialog;

        protected SetterGetterDialog setterGetterDialog;
        protected DialogInterface.OnClickListener positiveBtn;
        protected DialogInterface.OnClickListener negativeBtn;
        protected DialogInterface.OnClickListener neutralBtn;
        protected DialogInterface.OnClickListener onClickItem;

        Builder(){
            reset();
        }

        private void reset(){
            cancelable = false;
            message = "";
            title = "";
            color = R.color.GreenTextApp;
            styleDialog = STANDART_STYLEDIALOG;
            negativeBtn = STANDART_ONCLICK_CANCEL;
            positiveBtn = null;
            neutralBtn = null;
            onClickItem = null;
            neutralBtnStr = DIALOG_BTN_STR_BACK;
            positiveBtnStr = DIALOG_BTN_STR_OK;
            negativeBtnStr = DIALOG_BTN_STR_CANCEL;
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

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getNeutralBtnStr() {
            return neutralBtnStr;
        }

        public Builder setNeutralBtnStr(int neutralBtnStr) {
            this.neutralBtnStr = neutralBtnStr;
            return this;
        }

        public int getPositiveBtnStr() {
            return positiveBtnStr;
        }

        public Builder setPositiveBtnStr(int positiveBtnStr) {
            this.positiveBtnStr = positiveBtnStr;
            return this;
        }

        public int getNegativeBtnStr() {
            return negativeBtnStr;
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

        public int getStyleDialog() {
            return styleDialog;
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

        public DialogInterface.OnClickListener getPositiveBtn() {
            return positiveBtn;
        }

        public Builder setPositiveBtn(DialogInterface.OnClickListener positiveBtn) {
            this.positiveBtn = positiveBtn;
            return this;
        }

        public DialogInterface.OnClickListener getNegativeBtn() {
            return negativeBtn;
        }

        public Builder setNegativeBtn(DialogInterface.OnClickListener negativeBtn) {
            this.negativeBtn = negativeBtn;
            return this;
        }

        public DialogInterface.OnClickListener getOnClickItem() {
            return onClickItem;
        }

        public Builder setOnClickItem(DialogInterface.OnClickListener onClickItem) {
            this.onClickItem = onClickItem;
            return this;
        }

        public DialogInterface.OnClickListener getNeutralBtn() {
            return neutralBtn;
        }

        public Builder setNeutralBtn(DialogInterface.OnClickListener neutralBtn) {
            this.neutralBtn = neutralBtn;
            return this;
        }

        public AlertDialog.Builder buildDialog(View viewParent, @DialogType int createDialog){
            AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(viewParent.getContext(),styleDialog);;
            View viewInflated;
            SpannableStringBuilder ssbTitle = new SpannableStringBuilder(title);
            ssbTitle.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(viewParent.getContext(),color)),
                    0,
                    title.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            dialogBuilder.setTitle(ssbTitle);
            switch (createDialog){
                case DIALOG_INPUT_STRING:
                    viewInflated = LayoutInflater.from(viewParent.getContext()).inflate(R.layout.dialog_input_string, (ViewGroup) viewParent,false);
                    if(setterGetterDialog != null)
                        setterGetterDialog.setView(viewInflated);
                    dialogBuilder.setView(viewInflated)
                            .setCancelable(cancelable)
                            .setPositiveButton(positiveBtnStr, positiveBtn)
                            .setNegativeButton(negativeBtnStr, negativeBtn);
                    break;
                case DIALOG_QUATION:
                    dialogBuilder.setMessage(message)
                            .setCancelable(true)
                            .setNegativeButton(negativeBtnStr,negativeBtn);
                    if(positiveBtn != null) dialogBuilder.setPositiveButton(positiveBtnStr,positiveBtn);
                    if(neutralBtn != null) dialogBuilder.setNeutralButton(neutralBtnStr, neutralBtn);
                    break;
                case DIALOG_LIST:
                    break;
                case DIALOG_CUSTOM:
                    viewInflated = LayoutInflater.from(viewParent.getContext()).inflate(getIdView(), (ViewGroup) viewParent,false);
                    if(setterGetterDialog != null)
                        setterGetterDialog.setView(viewInflated);
                    dialogBuilder.setView(viewInflated)
                            .setCancelable(cancelable)
                            .setPositiveButton(positiveBtnStr, positiveBtn)
                            .setNegativeButton(negativeBtnStr, negativeBtn);
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

        public View getValuesView(int pos) {
            return valuesView[pos];
        }

        public int getLengthValues(){
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
            Method method = null;
            try {
                method = cls.getMethod(mutatorName,paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
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
        public Object getValueView(int pos, String accesorName, Class<?>[] paramTypes, Object[] objectSet){
            View view = getValuesView(pos);
            Class<?> cls = view.getClass();
            Method method = null;
            try {
                method = cls.getMethod(accesorName,paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Object obj = null;
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
        public String getLabelString(){
           return (label != null)? label.getText().toString() : null;
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
        public SetterGetterDialog(){}
        protected void setView(View view){
            this.view = view;
        }
    }
}
