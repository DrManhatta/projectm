package dr.manhattan.external.api.objects;

import dr.manhattan.external.api.M;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class MObjectCache {

    private static final Object BUS_SUB = new Object();
    static private ConcurrentHashMap<Long, GameObject> objects = new ConcurrentHashMap<>();

    public MObjectCache() {
        refreshObjects();
    }

    static public List<GameObject> getObjects(){
        List<GameObject> goList = new ArrayList<>();
        for(GameObject go: objects.values()){
            goList.add(go);
        }
        return goList;
    }

    static public void refreshObjects() {
        Client client = M.getInstance().getClient();
        if (client == null){
            log.info(MObjects.class + " - Client is null");
            return;
        }
        if (client.getLocalPlayer() == null){
            log.info(MObjects.class + " - Local player is null");
            return;
        }
        log.info(MObjects.class + " - Refreshing all objects");
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        Collection<GameObject> objectsCache = new ArrayList<>();
        int z = client.getPlane();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];
                if (tile == null) {
                    continue;
                }
                GameObject[] gameObjects = tile.getGameObjects();
                if (gameObjects != null) {
                    objectsCache.addAll(Arrays.asList(gameObjects));
                }
            }
        }
        objects.clear();
        for(GameObject go: objectsCache){
            addObject(go);
        }
    }

    public void start(EventBus eventBus) {
        eventBus.subscribe(GameObjectSpawned.class, BUS_SUB, this::objectSpawned);
        eventBus.subscribe(GameObjectDespawned.class, BUS_SUB, this::objectDepawned);
        eventBus.subscribe(GameObjectChanged.class, BUS_SUB, this::objectChanged);
        eventBus.subscribe(GameStateChanged.class, BUS_SUB, this::gameStateChanged);
    }

    public void stop(EventBus eventBus) {
        eventBus.unregister(BUS_SUB);
    }

    private void gameStateChanged(final GameStateChanged event) {

    }

    private static void addObject(GameObject go){
        if(go == null){
            return;
        }

        ObjectDefinition def = M.getInstance().getClient().getObjectDefinition(go.getId());
        MObjectDefinition.checkID(go.getId(), def);

        if(def.getName().equalsIgnoreCase("null")){
            log.info(MObjects.class + " - Ignoring null object");
            return;
        }
        objects.put(go.getHash(), go);
    }

    private static void removeObject(GameObject go){
        objects.remove(go.getHash(), go);
    }

    private void objectChanged(final GameObjectChanged event) {
        removeObject(event.getPrevious());
        addObject(event.getGameObject());
    }

    private void objectDepawned(final GameObjectDespawned event) {
        removeObject(event.getGameObject());
    }

    private void objectSpawned(final GameObjectSpawned event) {
        addObject(event.getGameObject());
    }
}
