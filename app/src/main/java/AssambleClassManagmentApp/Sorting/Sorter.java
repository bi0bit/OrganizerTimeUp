package AssambleClassManagmentApp.Sorting;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import AssambleClassManagmentApp.AbsTask;

public interface Sorter {
    void sort(List<AbsTask> tasks);
}

