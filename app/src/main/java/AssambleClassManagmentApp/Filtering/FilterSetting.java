package AssambleClassManagmentApp.Filtering;

import java.util.EnumSet;
import java.util.List;

import AssambleClassManagmentApp.AbsTask;

public class FilterSetting{
    public List<Integer> tags = null;
    public EnumSet<AbsTask.Priority_Task> priority_tasks = null;
    public boolean actual = false;
    public boolean nonComplete = false;

    public FilterSetting(List<Integer> tags, EnumSet<AbsTask.Priority_Task> priority_tasks, boolean actual, boolean nonComplete) {
        this.tags = tags;
        this.priority_tasks = priority_tasks;
        this.actual = actual;
        this.nonComplete = nonComplete;
    }
}
