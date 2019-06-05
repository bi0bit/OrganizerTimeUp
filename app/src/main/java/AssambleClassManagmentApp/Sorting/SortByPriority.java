package AssambleClassManagmentApp.Sorting;

import java.util.Collections;
import java.util.List;

import AssambleClassManagmentApp.AbsTask;

public class SortByPriority implements Sorter{
    @Override
    public void sort(List<AbsTask> tasks) {
        Collections.sort(tasks, (o1, o2) -> o2.getPriority().compareTo(o1.getPriority()));
    }
}
