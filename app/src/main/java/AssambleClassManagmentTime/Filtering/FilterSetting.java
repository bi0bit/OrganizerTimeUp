package AssambleClassManagmentTime.Filtering;

import java.util.EnumSet;
import java.util.List;

import AssambleClassManagmentTime.AbsTask;

public class FilterSetting{
    public List<Integer> tags;
    public EnumSet<AbsTask.Priority_Task> priority_tasks;
    public boolean actual;
    public boolean nonComplete;
    public long date;

    public FilterSetting(List<Integer> tags, EnumSet<AbsTask.Priority_Task> priority_tasks, boolean actual, boolean nonComplete, long date) {
        this.tags = tags;
        this.priority_tasks = priority_tasks;
        this.actual = actual;
        this.nonComplete = nonComplete;
        this.date = date;
    }
    public FilterSetting(List<Integer> tags, EnumSet<AbsTask.Priority_Task> priority_tasks, boolean actual, boolean nonComplete) {
        this.tags = tags;
        this.priority_tasks = priority_tasks;
        this.actual = actual;
        this.nonComplete = nonComplete;
        this.date = -1;
    }
}
