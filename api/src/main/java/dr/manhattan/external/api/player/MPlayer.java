package dr.manhattan.external.api.player;

import dr.manhattan.external.api.M;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class MPlayer {
    public static boolean isMoving()
    {
        Client client = M.getInstance().getClient();
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return false;
        }
        return player.getIdlePoseAnimation() != player.getPoseAnimation();
    }

    public static boolean isAnimating()
    {
        Client client = M.getInstance().getClient();

        return client.getLocalPlayer().getAnimation() != -1;
    }
    public static Player get()
    {
        Client client = M.getInstance().getClient();

        return client.getLocalPlayer();
    }

    public static WorldPoint location()
    {
        Client client = M.getInstance().getClient();

        return client.getLocalPlayer().getWorldLocation();
    }

    public static boolean isIdle()
    {
        Client client = M.getInstance().getClient();

        return !isAnimating() && !isMoving();
    }
}
