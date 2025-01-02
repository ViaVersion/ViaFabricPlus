/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.api.settings;

import com.viaversion.viafabricplus.util.ChatUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a group of settings. It is used to group settings in the settings screen.
 *
 * @see AbstractSetting
 */
public class SettingGroup {

    private final List<AbstractSetting<?>> settings = new ArrayList<>();
    private final Text name;

    public SettingGroup(Text name) {
        this.name = name;
    }

    /**
     * This list is used to store the settings of this group. It should not be touched directly by developers.
     * The list gets filled automatically when creating a new setting (see {@link AbstractSetting}).
     *
     * @return The list of settings.
     */
    public List<AbstractSetting<?>> getSettings() {
        return settings;
    }

    /**
     * Returns a setting by its translation key.
     *
     * @param translationKey The translation key of the setting.
     * @return The setting or null if no setting with the given translation key was found.
     */
    public AbstractSetting<?> getSetting(final String translationKey) {
        for (AbstractSetting<?> setting : settings) {
            if (ChatUtil.uncoverTranslationKey(setting.getName()).equals(translationKey)) {
                return setting;
            }
        }
        return null;
    }

    public Text getName() {
        return name;
    }

}
