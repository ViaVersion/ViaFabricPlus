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

    public final ModeSetting multiplayerScreenButtonOrientation = new ModeSetting(this, Text.translatable("general.viafabricplus.multiplayerscreenbutton"), 1,
            Text.translatable("misc.viafabricplus.lt"),
            Text.translatable("misc.viafabricplus.rt"),
            Text.translatable("misc.viafabricplus.lb"),
            Text.translatable("misc.viafabricplus.rb")
    );
    public final ModeSetting addServerScreenButtonOrientation = new ModeSetting(this, Text.translatable("general.viafabricplus.addserverscreenbutton"), 1,
            Text.translatable("misc.viafabricplus.lt"),
            Text.translatable("misc.viafabricplus.rt"),
            Text.translatable("misc.viafabricplus.lb"),
            Text.translatable("misc.viafabricplus.rb")
    );
    public final ModeSetting removeNotAvailableItemsFromCreativeTab = new ModeSetting(this, Text.translatable("general.viafabricplus.creative"),
            Text.translatable("misc.viafabricplus.all"),
            Text.translatable("misc.viafabricplus.vanillaonly"),
            Text.translatable("misc.viafabricplus.off")
    );
    public final BooleanSetting showSuperSecretSettings = new BooleanSetting(this, Text.translatable("general.viafabricplus.secret"), true);
    public final BooleanSetting showExtraInformationInDebugHud = new BooleanSetting(this, Text.translatable("general.viafabricplus.extrainformation"), true);
    public final BooleanSetting showClassicLoadingProgressInConnectScreen = new BooleanSetting(this, Text.translatable("general.viafabricplus.classicloading"), true);
    public final BooleanSetting autoDetectVersion = new BooleanSetting(this, Text.translatable("general.viafabricplus.autodetect"), false);
    public final BooleanSetting showAdvertisedServerVersion = new BooleanSetting(this, Text.translatable("general.viafabricplus.advertised"), true);
    public final ModeSetting ignorePacketTranslationErrors = new ModeSetting(this, Text.translatable("general.viafabricplus.ignoreerrors"),
            Text.translatable("misc.viafabricplus.kick"),
            Text.translatable("misc.viafabricplus.cancelnotify"),
            Text.translatable("misc.viafabricplus.cancel")
    );

    public GeneralSettings() {
        super(Text.translatable("settings.viafabricplus.general"));
    }
}
