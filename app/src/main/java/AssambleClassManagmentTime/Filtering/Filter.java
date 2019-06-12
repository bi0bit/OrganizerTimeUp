package AssambleClassManagmentTime.Filtering;

import android.util.Log;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import AssambleClassManagmentTime.AbsTask;

public interface Filter {
    void filter(List<AbsTask> tasks);
}

class NonFilter implements Filter {
    @Override
    public void filter(List<AbsTask> tasks) {

    }
}

class ActualTaskFilter implements Filter {

    final Filter filter;
    final boolean nonComplete;
    final long date;

    public ActualTaskFilter(Filter filter, boolean complete){
        this.filter = filter;
        this.nonComplete = complete;
        date = -1;
    }

    public ActualTaskFilter(Filter filter, boolean complete, long date){
        this.filter = filter;
        this.nonComplete = complete;
        this.date = date;
    }

    @Override
    public void filter(List<AbsTask> tasks) {
        if(filter != null) filter.filter(tasks);
        Iterator itr = tasks.iterator();
        while(itr.hasNext()){
            AbsTask task = (AbsTask) itr.next();


            boolean fit = (date == -1)? (nonComplete && (!task.isActual() || task.isComplete())) || (!nonComplete && !task.isActual()) :
                    ((nonComplete && (!task.isActual(date) || task.isComplete(date))) || (!nonComplete && !task.isActual(date)));
            if(fit){
                itr.remove();
            }
        }
    }
}

class PriorityTaskFilter implements Filter {
    final Filter filter;
    private final EnumSet<AbsTask.Priority_Task> priority_tasks;

    public PriorityTaskFilter(Filter filter, EnumSet<AbsTask.Priority_Task> priority_tasks) {
        this.filter = filter;
        this.priority_tasks = priority_tasks;
    }

    @Override
    public void filter(List<AbsTask> tasks) {
        if(filter != null) filter.filter(tasks);
        if(tasks.size()>0 && tasks.get(0).TYPE == AbsTask.Type_Task.HABIT) return;
        Iterator itr = tasks.iterator();
        while(itr.hasNext()){
            AbsTask task = (AbsTask) itr.next();
            if(!priority_tasks.contains(task.getPriority())){
                itr.remove();
            }
        }
    }
}

class TagTaskFilter implements Filter {
    private final Filter filter;
    private final List<Integer> tags;

    public TagTaskFilter(Filter filter, List<Integer> tags){
        this.filter = filter;
        this.tags = tags;
    }

    @Override
    public void filter(List<AbsTask> tasks) {
        filter.filter(tasks);
        Iterator itr = tasks.iterator();
        while(itr.hasNext()){
            AbsTask task = (AbsTask) itr.next();
            boolean isHave = false;
            for(Integer key : tags)
                if(task.getIntTags().contains(key)){
                    isHave = true;
                    break;
                }
            if(!isHave) itr.remove();
        }
    }
}

