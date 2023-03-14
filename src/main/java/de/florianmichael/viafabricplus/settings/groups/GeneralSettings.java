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
import de.florianmichael.viafabricplus.settings.type_impl.ModeSetting;

public class GeneralSettings extends SettingGroup {
    public final static GeneralSettings INSTANCE = new GeneralSettings();

    public final ModeSetting mainButtonOrientation = new ModeSetting(this, "Main button orientation", "Left; Top", "Right; Top", "Left; Bottom", "Right: Bottom");
    public final BooleanSetting removeNotAvailableItemsFromCreativeTab = new BooleanSetting(this, "Remove not available items from creative tab", true);
    public final BooleanSetting allowClassicProtocolCommandUsage = new BooleanSetting(this, "Allow classic protocol command usage", true);
    public final BooleanSetting automaticallyChangeValuesBasedOnTheCurrentVersion = new BooleanSetting(this, "Automatically change Settings based on the current version", true);

    public GeneralSettings() {
        super("General");
        mainButtonOrientation.setValue(1); // Default value
    }
}
