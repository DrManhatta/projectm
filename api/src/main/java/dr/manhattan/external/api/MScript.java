package dr.manhattan.external.api;

import net.runelite.client.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MScript extends Plugin {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public MScript() {
    }

    public abstract int loop();
}
