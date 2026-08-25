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

package com.viaversion.viafabricplus.screen;

import com.viaversion.viafabricplus.api.screen.Screens;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.screen.impl.SettingsScreen;
import net.minecraft.client.gui.screens.Screen;

public final class ScreensImpl implements Screens {

    private final ProtocolSelectionScreen protocolSelectionScreen = new ProtocolSelectionScreen();
    private final SettingsScreen settingsScreen = new SettingsScreen();

    @Override
    public void openProtocolSelectionScreen(final Screen parent) {
        this.protocolSelectionScreen.open(parent);
    }

    public ProtocolSelectionScreen protocolSelectionScreen(final Screen parent) {
        return this.protocolSelectionScreen;
    }

    @Override
    public void openSettingsScreen(final Screen parent) {
        this.settingsScreen.open(parent);
    }

    public SettingsScreen settingsScreen(final Screen parent) {
        return this.settingsScreen;
    }

}
