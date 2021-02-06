package dr.manhattan.external.api.player;

import dr.manhattan.external.api.M;
import dr.manhattan.external.api.calc.MCalc;
import dr.manhattan.external.api.interact.MenuEntryInterceptor;
import dr.manhattan.external.api.items.MItemDefinition;
import dr.manhattan.external.api.mouse.MMouse;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MInventory {
    @Inject
    public static int getEmptySlots()
    {    log.info("Items in inventory: " + inventory.size());
        return 28 - inventory.size();
    }


    public static void openInventory()
    {
        Client client = M.getInstance().getClient();

        if (client == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        client.runScript(915, 3); //open inventory
    }
    public static boolean dropAllExcept(String...items){
        List<String> dontDrop = Arrays.asList(items);
        inventory.forEach((slot, widget) ->{
            ItemDefinition def = MItemDefinition.getDef(widget.getId());
            if(def == null) return;
            if(dontDrop.contains(def.getName())) return;

            MenuEntryInterceptor.setMenuEntry(
                    new MenuEntry(
                            "",
                            "",
                            widget.getId(),
                            MenuOpcode.ITEM_DROP.getId(),
                            widget.getIndex(),
                            9764864,
                            false)
            );

            MMouse.delayMouseClick(widget.getCanvasBounds(), 0);
            try {
                Thread.sleep(MCalc.nextInt(500, 1000));
            } catch (InterruptedException e) {
                log.error("Drop all", e);
            }
        });
        return true;
    }

    public static boolean isFull()
    {
        return getEmptySlots() <= 0;
    }

    public static boolean isEmpty()
    {
        return getEmptySlots() >= 28;
    }

    public static boolean isOpen()
    {
        Client client = M.getInstance().getClient();

        if (client.getWidget(WidgetInfo.INVENTORY) == null)
        {
            return false;
        }
        return !client.getWidget(WidgetInfo.INVENTORY).isHidden();
    }

    private Object BUS_SUB = new Object();
    public void start(EventBus eventBus) {
        eventBus.subscribe(ItemContainerChanged.class, BUS_SUB, this::itemContainerChanged);
    }

    private static void refreshInventory(){

        Collection<WidgetItem> items = getInvWidgets();
        inventory.clear();
        for(WidgetItem item: items){
            inventory.put(item.getIndex(), item);
            MItemDefinition.checkID(item.getId());
        }
        inventory.forEach((index, item) ->{
            ItemDefinition def = MItemDefinition.getDef(item.getId());
            log.info(index + " - " + def.getName() + "(" + item.getQuantity() + ")");
        });
        log.info("Items to put to inv: " + items.size());
        log.info("Items in inv: " + inventory.size());
    }


    private static Collection<WidgetItem> getInvWidgets(){
        Widget inv = getInvWidget();
        if(inv == null) return null;
        return inv.getWidgetItems();
    }

    private static Widget getInvWidget(){
        Client client = M.getInstance().getClient();
        if(client == null) return null;
        return client.getWidget(WidgetInfo.INVENTORY);
    }

    public void stop(EventBus eventBus) {
        eventBus.unregister(BUS_SUB);
    }


    private static ConcurrentHashMap<Integer, WidgetItem> inventory = new ConcurrentHashMap<>();

    private void itemContainerChanged(ItemContainerChanged event){
        if(event.getContainerId() == InventoryID.INVENTORY.getId()){
            log.info("Inventory change");
            refreshInventory();
        }
    }
}
