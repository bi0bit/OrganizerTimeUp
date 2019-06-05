package AssambleClassManagmentApp.Sorting;

import java.util.Collections;
import java.util.List;

import AssambleClassManagmentApp.AbsTask;

public class SortByIdTask implements Sorter{
    @Override
    public void sort(List<AbsTask> tasks) {
        Collections.sort(tasks, (o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
    }
}
