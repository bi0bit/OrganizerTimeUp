package AssambleClassManagmentApp;

import android.database.Cursor;

import java.util.Collection;
import java.util.HashMap;

import appmanagmenttime.ilagoproject.com.managertimeapplicationtimeup.ManagerDB;

public class TagManager {
    private volatile static TagManager object = null;

    HashMap<Integer, String> tags;

    public static String getTag(int id) {
        return object.tags.get(id);
    }

    public static Collection<String> getTags(){
        return object.tags.values();
    }


    private TagManager() {
        tags = new HashMap<>();
        update();
    }

    public synchronized static void initTags() {
        if (object == null) {
            object = new TagManager();
        }
    }

    public static void update(){
        Cursor  c = ManagerDB.getManagerDB(null).getCursorTag();
        while(c.moveToNext()){
            object.tags.put(c.getInt(c.getColumnIndex(ManagerDB.ID_COLUMN)),
                    c.getString(c.getColumnIndex(ManagerDB.NAME_COLUMN)));
        }
    }
}
