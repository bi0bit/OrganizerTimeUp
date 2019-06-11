package AssambleClassManagmentTime;

import android.database.Cursor;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import by.ilagoproject.timeUp_ManagerTime.ManagerDB;

public class TagManager {
    private volatile static TagManager object = null;

    private final SparseArray<String> tags;


    public static SparseArray<String> getTags(){
        return object.tags;
    }

    public static String getStringTag(int idTag){
        return getTags().get(idTag);
    }

    public static List<String> getListNameTag(){
        List<String> names = new ArrayList<>();
        for(int i = 0; i < getTags().size(); i++){
            int key = getTags().keyAt(i);
            names.add(getTags().get(key));
        }
        return names;
    }

    public static List<Integer> getListIdTag(){
        List<Integer> keys = new ArrayList<>();
        for(int i = 0; i < getTags().size(); i++){
            keys.add(getTags().keyAt(i));
        }
        return keys;
    }


    private TagManager() {
        tags = new SparseArray<>();
    }

    public synchronized static void initTags() {
        if (object == null) {
            object = new TagManager();
        }
    }

    public static void addTag(String nameTag){
        ManagerDB.getManagerDB(null).addTag(nameTag);
        Cursor c = ManagerDB.getManagerDB(null).getCursorTag();
        c.moveToLast();
        object.tags.put(c.getInt(c.getColumnIndex(ManagerDB.ID_COLUMN)), c.getString(c.getColumnIndex(ManagerDB.NAME_COLUMN)));
    }

    public static void deleteTag(int idTag){
        ManagerDB.getManagerDB(null).deleteTag(idTag);
        getTags().clear();
        update();
    }

    public static void initTagByTask(AbsTask task){
        Cursor c = ManagerDB.getManagerDB(null).getCursorTagByTask(task.getId());
        List<Integer> list = task.getIntTags();
        while(c.moveToNext()){
            list.add(c.getInt(c.getColumnIndex(ManagerDB.IDTAG_COLUMN)));
        }
    }

    public static void update(){
        Cursor  c = ManagerDB.getManagerDB(null).getCursorTag();
        while(c.moveToNext()){
            getTags().put(c.getInt(c.getColumnIndex(ManagerDB.ID_COLUMN)),
                    c.getString(c.getColumnIndex(ManagerDB.NAME_COLUMN)));
        }
    }

    public static void rename(int idTag, String newName){
        ManagerDB.getManagerDB(null).updateTag(idTag, newName);
        getTags().put(idTag, newName);
    }
}
