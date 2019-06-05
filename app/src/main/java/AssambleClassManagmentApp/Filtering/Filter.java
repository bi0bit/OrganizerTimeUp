package AssambleClassManagmentApp.Filtering;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import AssambleClassManagmentApp.AbsTask;

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
    final boolean nonComlete;

    public ActualTaskFilter(Filter filter, boolean comlete){
        this.filter = filter;
        this.nonComlete = comlete;
    }

    @Override
    public void filter(List<AbsTask> tasks) {
        if(filter != null) filter.filter(tasks);
        for(AbsTask task : tasks){
            if(!task.isActual() && (!nonComlete || !task.isComplete())){
                tasks.remove(task);
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

