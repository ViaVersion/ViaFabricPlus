/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.api.settings.impl;

import com.viaversion.viafabricplus.api.settings.base.EnumSetting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public enum Orientation implements EnumSetting.EnumValue {

    NONE((_, _, _) -> {
    }),
    LEFT_TOP((button, _, _) -> button.setPosition(5, 5)),
    RIGHT_TOP((button, width, _) -> button.setPosition(width - 98 - 5, 5)),
    LEFT_BOTTOM((button, _, height) -> button.setPosition(5, height - 20 - 5)),
    RIGHT_BOTTOM((button, width, height) -> button.setPosition(width - 98 - 5, height - 20 - 5));

    final Component component;
    final Positioner positioner;

    Orientation(final Positioner positioner) {
        this.component = Component.translatable("base.viafabricplus." + this.name().toLowerCase());
        this.positioner = positioner;
    }

    public Positioner getPositioner() {
        return this.positioner;
    }

    @Override
    public Component component() {
        return this.component;
    }

    @FunctionalInterface
    public interface Positioner {

        void setPosition(final Button button, final int width, final int height);

    }

}
