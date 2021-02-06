package dr.manhattan.external.api.objects;

import dr.manhattan.external.api.M;
import net.runelite.api.ObjectDefinition;

import java.util.concurrent.ConcurrentHashMap;

public class MObjectDefinition {
    private static ConcurrentHashMap<Integer, ObjectDefinition> defCache =  new ConcurrentHashMap<>();
    public static void checkID(int id){
        if(defCache.containsKey(id)) return;
        ObjectDefinition def = M.getInstance().getClient().getObjectDefinition(id);
        defCache.put(id, def);
    }

    public static void checkID(int id, ObjectDefinition def){
        if(defCache.containsKey(id)) return;
        defCache.put(id, def);
    }

    public static ObjectDefinition getDef(int id){
        return defCache.get(id);
    }
}
