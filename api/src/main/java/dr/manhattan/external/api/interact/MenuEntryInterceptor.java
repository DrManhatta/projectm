package dr.manhattan.external.api.interact;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.EventBus;
@Slf4j
public class MenuEntryInterceptor {
    private static final Object BUS_SUB = new Object();
    private static MenuEntry entry = null;

    public void start(EventBus eventBus) {
        eventBus.subscribe(MenuOptionClicked.class, BUS_SUB, this::menuOptionClicked);
    }
    private void menuOptionClicked(MenuOptionClicked event){
        if (entry != null){
            event.setMenuEntry(entry);
        }
        entry = null;
    }

    public void stop(EventBus eventBus) {
        eventBus.unregister(BUS_SUB);
    }


    public static void setMenuEntry(MenuEntry setToEntry)
    {
        entry = setToEntry;
    }
}
