package de.florianmichael.viafabricplus.platform;

import com.viaversion.viaversion.api.Via;
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j;
import net.raphimc.viaaprilfools.platform.ViaAprilFoolsPlatform;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.logging.Logger;

public class ViaAprilFoolsPlatformImpl implements ViaAprilFoolsPlatform {
    private static final Logger LOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaAprilFools"));

    public ViaAprilFoolsPlatformImpl() {
        this.init(this.getDataFolder());
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public File getDataFolder() {
        return Via.getPlatform().getDataFolder();
    }
}
