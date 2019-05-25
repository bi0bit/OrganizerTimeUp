package AssambleClassManagmentApp;

import java.io.Serializable;

public class CheckTask implements Serializable {
    private boolean complete;
    private String text;
    public CheckTask(String textUnderTask){
        text = textUnderTask;
        complete = false;
    }

    public void setCompleteTask(boolean completeTask){
        complete = completeTask;
    }

    public boolean isCompleteTask(){
        return complete;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
