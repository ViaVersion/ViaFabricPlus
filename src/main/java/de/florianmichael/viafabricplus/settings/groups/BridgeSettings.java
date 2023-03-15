/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.settings.groups;

import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.type_impl.BooleanSetting;

public class BridgeSettings extends SettingGroup {
    public final static BridgeSettings INSTANCE = new BridgeSettings();

    public final BooleanSetting showSuperSecretSettings = new BooleanSetting(this, "Show Super Secret Settings", true);
    public final BooleanSetting showExtraInformationInDebugHud = new BooleanSetting(this, "Show extra information in Debug Hud", true);
    public final BooleanSetting showClassicLoadingProgressInConnectScreen = new BooleanSetting(this, "Show classic loading progress in connect screen", true);

    public BridgeSettings() {
        super("Bridge");
    }
}
