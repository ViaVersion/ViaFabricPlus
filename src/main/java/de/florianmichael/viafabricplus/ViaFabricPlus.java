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
import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import de.florianmichael.viafabricplus.definition.ItemReleaseVersionDefinition;
import de.florianmichael.viafabricplus.definition.PackFormatsDefinition;
import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountManager;
import de.florianmichael.viafabricplus.definition.c0_30.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus.definition.c0_30.protocol.CustomClassicProtocolExtensions;
import de.florianmichael.viafabricplus.definition.c0_30.command.ClassicProtocolCommands;
import de.florianmichael.viafabricplus.definition.v1_8_x.ArmorPointsDefinition;
import de.florianmichael.viafabricplus.event.FinishMinecraftLoadCallback;
import de.florianmichael.viafabricplus.event.PreLoadCallback;
import de.florianmichael.viafabricplus.information.InformationSystem;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.SettingsSystem;

import java.io.File;

public class ViaFabricPlus {
    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public final static File RUN_DIRECTORY = new File("ViaFabricPlus");

    public final static ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final SettingsSystem settingsSystem = new SettingsSystem();
    private final InformationSystem informationSystem = new InformationSystem();

    public void init() {
        PreLoadCallback.EVENT.invoker().onLoad();

        CustomClassicProtocolExtensions.create();
        new ProtocolHack();

        FinishMinecraftLoadCallback.EVENT.register(() -> {
            // General settings
            settingsSystem.init();
            informationSystem.init();

            // General definitions
            PackFormatsDefinition.load();
            ItemReleaseVersionDefinition.create();
            ArmorPointsDefinition.load();

            // Classic Stuff
            ChatLengthDefinition.create();
            ClassicItemSelectionScreen.create();
            ClassicProtocolCommands.create();

            // Bedrock Stuff
            BedrockAccountManager.INSTANCE.load();
        });
    }

    public SettingsSystem getSettingsSystem() {
        return settingsSystem;
    }

    public InformationSystem getInformationSystem() {
        return informationSystem;
    }
}
