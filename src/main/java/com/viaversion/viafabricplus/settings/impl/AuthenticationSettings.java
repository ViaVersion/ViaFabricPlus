/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.settings.impl;

import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import net.minecraft.text.Text;

public final class AuthenticationSettings extends SettingGroup {

    public static final AuthenticationSettings INSTANCE = new AuthenticationSettings();

    public final BooleanSetting useBetaCraftAuthentication = new BooleanSetting(this, Text.translatable("authentication_settings.viafabricplus.use_beta_craft_authentication"), true);
    public final BooleanSetting verifySessionForOnlineModeServers = new BooleanSetting(this, Text.translatable("authentication_settings.viafabricplus.verify_session_for_online_mode"), true);
    public final BooleanSetting automaticallySelectCPEInClassiCubeServerList = new BooleanSetting(this, Text.translatable("authentication_settings.viafabricplus.automatically_select_cpe_when_using_classicube"), true);
    public final BooleanSetting setSessionNameToClassiCubeNameInServerList = new BooleanSetting(this, Text.translatable("authentication_settings.viafabricplus.set_session_name_to_classicube_name"), true);

    public AuthenticationSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.authentication"));
    }

}
