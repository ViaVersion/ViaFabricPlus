/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.save.AbstractSave;
import de.florianmichael.viafabricplus.settings.SettingsManager;
import de.florianmichael.viafabricplus.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.settings.base.ButtonSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.raphimc.vialoader.util.VersionEnum;

public class SettingsSave extends AbstractSave {

    private final SettingsManager settingsManager;

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

        object.addProperty("selected-protocol-version", ProtocolHack.getTargetVersion().getOriginalVersion());
    }

    @Override
    public void read(JsonObject object) {
        for (SettingGroup group : settingsManager.getGroups()) {
            final JsonObject groupObject = object.getAsJsonObject(AbstractSetting.mapTranslationKey(ChatUtil.uncoverTranslationKey(group.getName())));
            for (AbstractSetting<?> setting : group.getSettings()) {
                if (!groupObject.has(setting.getTranslationKey()) && !(setting instanceof ButtonSetting)) {
                    continue;
                }
                setting.read(groupObject);
            }
        }

        if (GeneralSettings.global().saveSelectedProtocolVersion.getValue() && object.has("selected-protocol-version")) {
            final VersionEnum protocolVersion = VersionEnum.fromProtocolId(object.get("selected-protocol-version").getAsInt());

            if (protocolVersion != VersionEnum.UNKNOWN) {
                ProtocolHack.setTargetVersion(protocolVersion);
            }
        } else {
            ProtocolHack.setTargetVersion(ProtocolHack.NATIVE_VERSION);
        }
    }

}
