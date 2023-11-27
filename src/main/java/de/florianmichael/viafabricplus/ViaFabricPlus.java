/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus;

import de.florianmichael.viafabricplus.event.PostGameLoadCallback;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.save.SaveManager;
import de.florianmichael.viafabricplus.settings.SettingsManager;
import de.florianmichael.viafabricplus.util.ClassLoaderPriorityUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/*
 * TODO | General
 *  - Make recipe fixes dynamic instead of a data dump in java classes
 *  - Check if relevant for protocol translation: TakeItemEntityPacket isEmpty case (1.20 -> 1.20.1 change)
 *  - Window interactions in <= 1.16.5 has changed and can be detected by the server
 *  - Most CTS protocol features aren't supported (see https://github.com/ViaVersion/ViaFabricPlus/issues/181)
 *  - Most CPE features aren't implemented correctly (see https://github.com/ViaVersion/ViaFabricPlus/issues/152)
 *  - Check if MixinPlayerScreenHandler.injectTransferSlot is needed? Check git log
 *
 * TODO | Movement
 *  - X/Z Face based jump movement in <= 1.13.2 is broken (https://github.com/ViaVersion/ViaFabricPlus/issues/189)
 *  - Collision hit boxes has been changed (https://github.com/ViaVersion/ViaFabricPlus/issues/195)
 *  - Blit-jump is not supported in <= 1.8.9 (https://github.com/ViaVersion/ViaFabricPlus/issues/225)
 *
 * TODO | Migration v3
 *  - Use ViaProxy config patch for some clientside fixes options (Remove ViaFabricPlusVLViaConfig and MixinViaLegacyConfig)
 *  - Add setting for VFP AlphaInventoryProvider
 *  - Fix MixinAbstractDonkeyEntity
 *  - Check TO DO in MixinEntity
 *  - Fix bedrock online mode
 */
public class ViaFabricPlus {
    private static final ViaFabricPlus instance = new ViaFabricPlus();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final File directory = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus").toFile();

    private SettingsManager settingsManager;
    private SaveManager saveManager;

    private CompletableFuture<Void> loadingFuture;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void bootstrap() {
        directory.mkdir();
        ClassLoaderPriorityUtil.loadOverridingJars(directory); // Load overriding jars first so other code can access the new classes

        ClientsideFixes.init(); // Init clientside related fixes
        loadingFuture = ProtocolHack.init(directory); // Init ViaVersion protocol translator platform

        settingsManager = new SettingsManager();
        saveManager = new SaveManager(settingsManager);
        PostGameLoadCallback.EVENT.register(() -> {
            saveManager.init();
            loadingFuture.join();
        }); // Has to wait for Minecraft because of the translation system usages
    }

    public static ViaFabricPlus global() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDirectory() {
        return directory;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }
}
