package dr.manhattan.external.api.interact;

import dr.manhattan.external.api.mouse.MMouse;
import dr.manhattan.external.api.objects.MObjectDefinition;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.ObjectDefinition;

import java.util.Arrays;
import java.util.List;
@Slf4j
public class MInteract {
    public static boolean GameObject(GameObject go, String... actions) {
        ObjectDefinition def = MObjectDefinition.getDef(go.getId());
        String[] goActions = def.getActions();
        int actionIndex = -1;
        List<String> actionList = Arrays.asList(actions);
        for (int op = 0; op < goActions.length; op++) {
            if (actionList.contains(goActions[op])) {
                actionIndex = op;
                break;
            }
        }

        log.info("Action index: " + actionIndex);
        MenuOpcode actionOp = null;
        switch (actionIndex) {
            case 0:
                actionOp = MenuOpcode.GAME_OBJECT_FIRST_OPTION;
                break;
            case 1:
                actionOp = MenuOpcode.GAME_OBJECT_SECOND_OPTION;
                break;
            case 2:
                actionOp = MenuOpcode.GAME_OBJECT_THIRD_OPTION;
                break;
            case 3:
                actionOp = MenuOpcode.GAME_OBJECT_FOURTH_OPTION;
                break;
            case 4:
                actionOp = MenuOpcode.GAME_OBJECT_FIFTH_OPTION;
                break;
            default:
                return false;
        }
        log.info("Action op: " + actionOp.getId());
        MenuEntryInterceptor.setMenuEntry(
                new MenuEntry(
                        "",
                        "",
                        go.getId(),
                        actionOp.getId(),
                        go.getSceneMinLocation().getX(),
                        go.getSceneMinLocation().getY(),
                        false
                )
        );
        log.info("Click as well..");
        MMouse.delayMouseClick(go.getConvexHull(), 0);
        return true;
    }
}
