/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package com.viaversion.viafabricplus;

import com.viaversion.viafabricplus.event.PostGameLoadCallback;
import com.viaversion.viafabricplus.fixes.ClientsideFixes;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.settings.SettingsManager;
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/*
 * TODO | Port 1.21.3
 *  - VehicleMovePacket handling now has distance check in ClientPlayNetworkHandler
 *  - Illusioner/Sniffer don't override visibility bounding box anymore
 *  - AbstractFireabll/EyeOfEnder#shouldRender new
 *  - Entity#baseTick doesn't set prev rotation anymore at top
 *  - LivingEntity movement code got refactored completely
 *  - BundleItem#use, ConsumableComponent behaviour in use functions
 *  - Check all screen handlers for changes
 *
 * TODO | Port 1.20.6
 *  - ClientPlayerInteractionManager#interactBlockInternal added new condition
 *  - Command arguments (Probably not everything worth, but least them with nbt)
 *  - Entity attachment calculation got changed completely
 *  - Particle handling has slightly changed
 *  - BookViewScreen/BookEditScreen networking handling
 *  - SetEquipment packet now only accepts living entities
 *  - Wolf interaction
 *
 * TODO | General
 *  - Make recipe fixes dynamic instead of a data dump in java classes
 *  - Window interactions in <= 1.16.5 has changed and can be detected by the server
 *  - Most CTS protocol features aren't supported (see https://github.com/ViaVersion/ViaFabricPlus/issues/181)
 *  - Most CPE features aren't implemented correctly (see https://github.com/ViaVersion/ViaFabricPlus/issues/152)
 *  - Via: 1.13 -> 1.12.2 block entities recode
 *  - OXYGEN_BONUS 1.21 -> 1.20.5 handling is missing (only visual)
 *
 * TODO | Movement
 *  - 1.8 lava movement
 *  - 1.13.2 water movement
 */
public class ViaFabricPlus {

    private static final ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final File directory = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus").toFile();

    private SettingsManager settingsManager;
    private SaveManager saveManager;

    private CompletableFuture<Void> loadingFuture;

    public void init() {
        directory.mkdir();
        ClassLoaderPriorityUtil.loadOverridingJars(directory); // Load overriding jars first so other code can access the new classes

        settingsManager = new SettingsManager();
        saveManager = new SaveManager(settingsManager);

        ClientsideFixes.init(); // Init clientside related fixes
        loadingFuture = ProtocolTranslator.init(directory); // Init ViaVersion protocol translator platform

        // Block game loading until ViaVersion has loaded
        PostGameLoadCallback.EVENT.register(() -> {
            loadingFuture.join();
            saveManager.postInit();
        });
    }

    public static ViaFabricPlus global() {
        return INSTANCE;
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
