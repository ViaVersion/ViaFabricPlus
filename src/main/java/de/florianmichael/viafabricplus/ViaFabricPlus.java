/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.florianmichael.viafabricplus.base.ViaJarReplacer;
import de.florianmichael.viafabricplus.base.event.FinishMinecraftLoadCallback;
import de.florianmichael.viafabricplus.base.event.PreLoadCallback;
import de.florianmichael.viafabricplus.base.settings.SettingsSystem;
import de.florianmichael.viafabricplus.definition.ClientsideFixes;
import de.florianmichael.viafabricplus.definition.account.BedrockAccountHandler;
import de.florianmichael.viafabricplus.definition.account.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.classic.CustomClassicProtocolExtensions;
import de.florianmichael.viafabricplus.information.InformationSystem;
import de.florianmichael.viafabricplus.mappings.CharacterMappings;
import de.florianmichael.viafabricplus.mappings.ItemReleaseVersionMappings;
import de.florianmichael.viafabricplus.mappings.PackFormatsMappings;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.screen.ClassicItemSelectionScreen;
import net.raphimc.vialoader.util.VersionEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/*
 * TODO | Port
 *  - AllayEntity, VexEntity and BoatEntity height offsets are missing?
 *
 * TODO | General
 *  - Check if relevant for protocol translation: TakeItemEntityPacket isEmpty case (1.20 -> 1.20.1 change)
 *  - Window interactions in <= 1.16.5 has changed and can be detected by the server
 *  - Entity hit boxes and eye heights has changed in almost all versions
 *  - Crafting Recipes are missing in ViaVersion (see https://github.com/ViaVersion/ViaFabricPlus/issues/60)
 *  - Most CTS protocol features aren't supported (see https://github.com/ViaVersion/ViaFabricPlus/issues/181)
 *  - Most CPE features aren't implemented correctly (see https://github.com/ViaVersion/ViaFabricPlus/issues/152)
 *  - Bedrock scaffolding should be added as soon as ViaBedrock supports block placement (see https://github.com/ViaVersion/ViaFabricPlus/issues/204)
 *
 * TODO | Movement
 *  - Cobwebs in <= b1.7.3 are broken (movement has been changed)
 *  - X/Z Face based jump movement in <= 1.13.2 is broken (https://github.com/ViaVersion/ViaFabricPlus/issues/189)
 *  - Collision hit boxes has been changed (https://github.com/ViaVersion/ViaFabricPlus/issues/195)
 *  - Blit-jump is not supported in <= 1.8.9 (https://github.com/ViaVersion/ViaFabricPlus/issues/225)
 */
public class ViaFabricPlus {
    public final static VersionEnum NATIVE_VERSION = VersionEnum.r1_20_2;

    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static Logger LOGGER = LogManager.getLogger("ViaFabricPlus");
    public final static File RUN_DIRECTORY = new File("ViaFabricPlus");

    public final static ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final SettingsSystem settingsSystem = new SettingsSystem();
    private final InformationSystem informationSystem = new InformationSystem();

    public void init() {
        if (!RUN_DIRECTORY.exists()) RUN_DIRECTORY.mkdir();

        // Load overriding jars first so other code can access the new classes
        ViaJarReplacer.loadOverridingJars();

        PreLoadCallback.EVENT.invoker().onLoad();

        // Classic Stuff
        CustomClassicProtocolExtensions.create();

        // Account Handler
        ClassiCubeAccountHandler.create();
        BedrockAccountHandler.create();

        // Fixes which requires to be loaded pre
        ClientsideFixes.init();
        CharacterMappings.load();

        // Protocol Translator
        ProtocolHack.init();

        // Stuff which requires Minecraft to be initialized
        FinishMinecraftLoadCallback.EVENT.register(() -> {
            // Has to be loaded before the settings system in order to catch the ChangeProtocolVersionCallback call
            ClassicItemSelectionScreen.create();

            // General settings
            settingsSystem.init();
            informationSystem.init();

            // Version related mappings
            PackFormatsMappings.load();
            ItemReleaseVersionMappings.create();
        });
    }

    public SettingsSystem getSettingsSystem() {
        return settingsSystem;
    }

    public InformationSystem getInformationSystem() {
        return informationSystem;
    }
}
