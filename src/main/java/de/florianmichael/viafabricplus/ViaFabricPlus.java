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
 * TODO | General
 *  - Make recipe fixes dynamic instead of a data dump in java classes
 *  - Check if relevant for protocol translation: TakeItemEntityPacket isEmpty case (1.20 -> 1.20.1 change)
 *  - Check previous Donkey interaction fix (see git logs)
 *  - Window interactions in <= 1.16.5 has changed and can be detected by the server
 *  - Most CTS protocol features aren't supported (see https://github.com/ViaVersion/ViaFabricPlus/issues/181)
 *  - Most CPE features aren't implemented correctly (see https://github.com/ViaVersion/ViaFabricPlus/issues/152)
 *  - Check if MixinPlayerScreenHandler.injectTransferSlot is needed? Check git log
 *
 * TODO | Movement
 *  - Collision hit boxes has been changed (https://github.com/ViaVersion/ViaFabricPlus/issues/195)
 *  - Blip-jumping is not supported in <= 1.13.2 (https://github.com/ViaVersion/ViaFabricPlus/issues/225)
 *  - Older versions don't clamp positions when teleporting (Is this important?)
 *
 * TODO | Port
 *  - Readd MixinCustomPayloadS2CPacket, MixinInGameHud
 *  - Test 1.20.1 riding offsets
 *  - EntityDimensionDiff: Add eye height and attachment point
 *  - Test bedrock transfer
 *  - Readd item fixes: MixinItemStack, MixinDrawContext, MixinPacketByteBuf
 *  - Figure out how to fix 32k enchantments with codecs (MixinEnchantmentHelper)
 */
public class ViaFabricPlus {

    private static final ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final File directory = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus").toFile();

    private SettingsManager settingsManager;
    private SaveManager saveManager;

    private CompletableFuture<Void> loadingFuture;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void bootstrap() {
        directory.mkdir();
        ClassLoaderPriorityUtil.loadOverridingJars(directory); // Load overriding jars first so other code can access the new classes

        settingsManager = new SettingsManager();
        saveManager = new SaveManager(settingsManager);

        ClientsideFixes.init(); // Init clientside related fixes
        loadingFuture = ProtocolTranslator.init(directory); // Init ViaVersion protocol translator platform

        PostGameLoadCallback.EVENT.register(() -> loadingFuture.join()); // Block game loading until ViaVersion has loaded
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
