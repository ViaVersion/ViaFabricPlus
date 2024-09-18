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

package de.florianmichael.viafabricplus.save.impl;

import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.save.AbstractSave;
import de.florianmichael.viafabricplus.settings.SettingsManager;
import de.florianmichael.viafabricplus.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;

public class SettingsSave extends AbstractSave {

    private final SettingsManager settingsManager;
    private String selectedProtocolVersion;

    public SettingsSave(final SettingsManager settingsManager) {
        super("settings");

        this.settingsManager = settingsManager;
    }

    @Override
    public void write(JsonObject object) {
        for (SettingGroup group : settingsManager.getGroups()) {
            final JsonObject groupObject = new JsonObject();
            for (AbstractSetting<?> setting : group.getSettings()) {
                setting.write(groupObject);
            }

            object.add(AbstractSetting.mapTranslationKey(ChatUtil.uncoverTranslationKey(group.getName())), groupObject);
        }

        object.addProperty("selected-protocol-version", ProtocolTranslator.getTargetVersion().getName());
    }

    @Override
    public void read(JsonObject object) {
        for (SettingGroup group : settingsManager.getGroups()) {
            final String translationKey = ChatUtil.uncoverTranslationKey(group.getName());

            final JsonObject groupObject = object.getAsJsonObject(AbstractSetting.mapTranslationKey(translationKey));
            for (AbstractSetting<?> setting : group.getSettings()) {
                if (groupObject.has(setting.getTranslationKey())) {
                    setting.read(groupObject);
                }
            }
        }

        if (object.has("selected-protocol-version")) {
            selectedProtocolVersion = object.get("selected-protocol-version").getAsString();
        }
    }

    @Override
    public void postInit() {
        if (selectedProtocolVersion == null) {
            return;
        }
        // Set target version AFTER protocol loading, so we can reach all versions
        if (GeneralSettings.global().saveSelectedProtocolVersion.getValue()) {
            final ProtocolVersion protocolVersion = protocolVersionByName(selectedProtocolVersion);
            if (protocolVersion != null) {
                ProtocolTranslator.setTargetVersion(protocolVersion);
            }
        } else {
            ProtocolTranslator.setTargetVersion(ProtocolTranslator.NATIVE_VERSION);
        }
    }

    public static ProtocolVersion protocolVersionByName(final String name) {
        if (name.contains("Bedrock")) { // Always return latest bedrock since the version often changes
            return BedrockProtocolVersion.bedrockLatest;
        } else {
            return ProtocolVersion.getClosest(name);
        }
    }

}
