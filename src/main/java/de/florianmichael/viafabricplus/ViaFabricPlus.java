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
import de.florianmichael.viafabricplus.definition.ChatLengthCalculation;
import de.florianmichael.viafabricplus.definition.v1_12_2.FontCacheFix;
import de.florianmichael.viafabricplus.mappings.ItemReleaseVersionMappings;
import de.florianmichael.viafabricplus.mappings.PackFormatsMappings;
import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountHandler;
import de.florianmichael.viafabricplus.screen.impl.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus.definition.c0_30.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.protocol.CustomClassicProtocolExtensions;
import de.florianmichael.viafabricplus.definition.v1_8.ArmorPointCalculation;
import de.florianmichael.viafabricplus.base.event.FinishMinecraftLoadCallback;
import de.florianmichael.viafabricplus.base.event.PreLoadCallback;
import de.florianmichael.viafabricplus.information.InformationSystem;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.base.settings.SettingsSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * TODO | Port
 *   - TakeItemEntityPacket isEmpty case
 * <p>
 * TODO | Protocol translation
 *  - BucketItem#use canPlaceOn in <= 1.13 is missing
 *  - Cobwebs in <= b1.7.3 are broken
 *  - Window interactions in <= 1.16.5 are unlegit
 *  - Entity hitboxes and eye heights has changed
 *  - Crafting Recipes are missing in ViaVersion
 */
public class ViaFabricPlus {
    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static Logger LOGGER = LogManager.getLogger("ViaFabricPlus");
    public final static File RUN_DIRECTORY = new File("ViaFabricPlus");

    public final static ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final SettingsSystem settingsSystem = new SettingsSystem();
    private final InformationSystem informationSystem = new InformationSystem();

    public void init() {
        PreLoadCallback.EVENT.invoker().onLoad();

        // Classic Stuff
        CustomClassicProtocolExtensions.create();

        // Account Handler
        ClassiCubeAccountHandler.create();
        BedrockAccountHandler.create();

        // Fixes which requires to be loaded pre
        ChatLengthCalculation.create();
        FontCacheFix.init();

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
            ArmorPointCalculation.load();
        });
    }

    public SettingsSystem getSettingsSystem() {
        return settingsSystem;
    }

    public InformationSystem getInformationSystem() {
        return informationSystem;
    }
}
