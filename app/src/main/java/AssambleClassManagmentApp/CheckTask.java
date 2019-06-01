package AssambleClassManagmentApp;

import java.io.Serializable;

public class CheckTask implements Serializable {
    private int id;
    private boolean complete;
    private String text;
    public CheckTask(String textUnderTask){
        text = textUnderTask;
        complete = false;
    }

    public CheckTask(int id, String textUnderTask, boolean complete){
        this.id = id;
        text = textUnderTask;
        this.complete = complete;
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
