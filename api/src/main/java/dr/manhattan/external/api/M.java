package dr.manhattan.external.api;

import dr.manhattan.external.api.interact.MenuEntryInterceptor;
import dr.manhattan.external.api.objects.MObjectCache;
import dr.manhattan.external.api.player.MInventory;
import dr.manhattan.external.api.scriptmanager.ScriptManager;
import dr.manhattan.external.api.scriptmanager.ScriptThread;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

import javax.inject.Inject;

@Slf4j
@Extension
@PluginDescriptor(
        name = "Project:M",
        description = "Automation Programming Interface",
        tags = {"OpenOSRS", "ProjectM", "Bot"},
        type = PluginType.SYSTEM
)
public class M extends Plugin {
    private static final ScriptThread scriptThread = new ScriptThread();
    private static final ScriptManager scriptManager = new ScriptManager();
    private static final MObjectCache mObjectCache = new MObjectCache();
    private static final MInventory mInventory = new MInventory();
    private static final MenuEntryInterceptor menuEntryProcessor = new MenuEntryInterceptor();
    private static M m;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private EventBus eventBus;
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;


    public static M getInstance() {
        if (m != null) return m;
        m = new M();
        return m;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
    public Client getClient() {
        return client;
    }
    public EventBus getEventBus(){ return eventBus;}
    public ItemManager getItemManager(){ return itemManager;}


    @Override
    protected void startUp() {
        m = this;
        log.info("EventBus: " + eventBus);
        new Thread(scriptThread, "ProjectM script thread").start();
        ScriptManager.manageScripts();
        mObjectCache.start(eventBus);
        mInventory.start(eventBus);
        menuEntryProcessor.start(eventBus);
    }

    @Override
    protected void shutDown() throws Exception {
        mObjectCache.stop(eventBus);
        mInventory.stop(eventBus);
        menuEntryProcessor.stop(eventBus);
        scriptThread.kill();
        super.shutDown();
        m = null;
    }
}
