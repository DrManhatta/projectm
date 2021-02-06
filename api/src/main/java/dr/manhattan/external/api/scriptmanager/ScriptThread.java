package dr.manhattan.external.api.scriptmanager;

import dr.manhattan.external.api.M;
import dr.manhattan.external.api.MScript;
import net.runelite.api.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptThread implements Runnable {
    private boolean runScript = true;
    public synchronized void kill(){ this.runScript = false;}
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public void run() {
        while (runScript) {
            ScriptManager.manageScripts();
            try {
                MScript activeScript = ScriptManager.getActiveScript();
                if (activeScript == null) Thread.sleep(1000);
                else {
                    if(M.getInstance().getClient().getGameState() == GameState.LOGGED_IN)
                        Thread.sleep(activeScript.loop());
                    else{
                        Thread.sleep(1000);
                        log.info("Not looping - logged out");
                    }
                }
            } catch (InterruptedException e) {
                log.error("Script thread", e);
            }
        }
    }
}
