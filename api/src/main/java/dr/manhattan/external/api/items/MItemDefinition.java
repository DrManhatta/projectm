package dr.manhattan.external.api.items;

import dr.manhattan.external.api.M;
import net.runelite.api.ItemDefinition;

import java.util.concurrent.ConcurrentHashMap;

public class MItemDefinition {
    private static ConcurrentHashMap<Integer, ItemDefinition> defCache =  new ConcurrentHashMap<>();
    public static void checkID(int id){
        if(defCache.containsKey(id)) return;
        ItemDefinition def = M.getInstance().getClient().getItemDefinition(id);
        defCache.put(id, def);
    }

    public static void checkID(int id, ItemDefinition def){
        if(defCache.containsKey(id)) return;
        defCache.put(id, def);
    }

    public static ItemDefinition getDef(int id){
        return defCache.get(id);
    }
}
