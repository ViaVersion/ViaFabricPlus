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
package de.florianmichael.viafabricplus.base.settings.groups;

import de.florianmichael.viafabricplus.base.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.base.settings.type_impl.BooleanSetting;
import de.florianmichael.viafabricplus.base.settings.type_impl.ModeSetting;
import net.minecraft.text.Text;

public class GeneralSettings extends SettingGroup {
    public final static GeneralSettings INSTANCE = new GeneralSettings();

    public final ModeSetting mainButtonOrientation = new ModeSetting(this, Text.translatable("general.viafabricplus.main"),
            Text.translatable("words.viafabricplus.lt"),
            Text.translatable("words.viafabricplus.rt"),
            Text.translatable("words.viafabricplus.lb"),
            Text.translatable("words.viafabricplus.rb")
    );
    public final BooleanSetting removeNotAvailableItemsFromCreativeTab = new BooleanSetting(this, Text.translatable("general.viafabricplus.creative"), true);
    public final BooleanSetting allowClassicProtocolCommandUsage = new BooleanSetting(this, Text.translatable("general.viafabricplus.classiccommands"), true);
    public final BooleanSetting automaticallyChangeValuesBasedOnTheCurrentVersion = new BooleanSetting(this, Text.translatable("general.viafabricplus.protocolsync"), true);
    public final BooleanSetting showSuperSecretSettings = new BooleanSetting(this, Text.translatable("general.viafabricplus.secret"), true);
    public final BooleanSetting showExtraInformationInDebugHud = new BooleanSetting(this, Text.translatable("general.viafabricplus.extrainformation"), true);
    public final BooleanSetting showClassicLoadingProgressInConnectScreen = new BooleanSetting(this, Text.translatable("general.viafabricplus.classicloading"), true);
    public final BooleanSetting autoDetectVersion = new BooleanSetting(this, Text.translatable("general.viafabricplus.autodetect"), false);

    public GeneralSettings() {
        super("settings.viafabricplus.general");
        mainButtonOrientation.setValue(1); // Default value
    }
}
