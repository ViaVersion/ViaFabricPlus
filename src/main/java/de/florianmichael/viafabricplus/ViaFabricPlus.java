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

package de.florianmichael.viafabricplus;

import de.florianmichael.viafabricplus.event.PostGameLoadCallback;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.save.SaveManager;
import de.florianmichael.viafabricplus.settings.SettingsManager;
import de.florianmichael.viafabricplus.util.ClassLoaderPriorityUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/*
 * TODO | Port 1.20.6
 *  - ClientPlayerInteractionManager#interactBlockInternal added new condition
 *  - MouseHandling changed (not sure if relevant)
 *  - Command arguments (Probably not everything worth, but least them with nbt)
 *  - Entity attachment calculation got changed completely
 *  - Particle handling has slightly changed
 *
 * TODO | Port 1.21
 *  - ClientPlayerEntity#tickMovement nether portal logic has new screen conditions and changed
 *  - Entity#interact now handles leashables interface which was previously handled in MobEntity
 *  - shouldCancelInteraction condition in ChestBoatEntity#interact is new
 *  - HangingEntity/ItemFrame/Painting bounding box calculation changed
 *  - PlayerEntity#attack got refactored (?)
 *  - LivingEntity#takeKnockback with 1.0E-5F for loop is new
 *  - KnowledgeBookItem#use decrementUnlessCreative is new
 *  - JukeboxBlock#onUse override is new (added state.get(HAS_RECORD) condition)
 *  - HangingEntity bounding box calculation changes
 *  - BoatEntity#updateVelocity isSpaceEmpty condition new
 *  - LivingEntity#remove -> added activeEffects.clear call
 *  - PlayerEntity#tickMovement getSaturationLevel/setSaturationLevel handling is new
 *  - Check WorldBorder bounds check
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
 *  - Collision hit boxes has been changed (https://github.com/ViaVersion/ViaFabricPlus/issues/195)
 *  - Blip-jumping is not supported in <= 1.13.2 (https://github.com/ViaVersion/ViaFabricPlus/issues/225)
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
