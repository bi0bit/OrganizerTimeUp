package AssambleClassManagmentApp;

import java.util.List;

public interface FilterTask {
    void filter(List<AbsTask> tasks);
}

class NonFilter implements FilterTask{
    @Override
    public void filter(List<AbsTask> tasks) {

    }
}

class RealTaskFilter implements FilterTask{

    @Override
    public void filter(List<AbsTask> tasks) {

    }
}

class PriorityTaskFilter implements FilterTask{
    @Override
    public void filter(List<AbsTask> tasks) {

    }
}

class OrderedFilter implements FilterTask{
    boolean backOrder = false;
    @Override
    public void filter(List<AbsTask> tasks) {

    }
}