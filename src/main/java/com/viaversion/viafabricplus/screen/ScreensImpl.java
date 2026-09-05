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
import com.viaversion.viafabricplus.features.classic.creative_menu.GridItemSelectionScreen;
import com.viaversion.viafabricplus.screen.impl.ViaFabricPlusScreen;
import com.viaversion.viafabricplus.screen.impl.ReportIssuesScreen;
import com.viaversion.viafabricplus.screen.impl.SettingsScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeLoginScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeMFAScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen;
import net.minecraft.client.gui.screens.Screen;

public final class ScreensImpl implements Screens {

    private final ViaFabricPlusScreen viaFabricPlusScreen = new ViaFabricPlusScreen();
    private final SettingsScreen settingsScreen = new SettingsScreen();
    private final ReportIssuesScreen reportIssuesScreen = new ReportIssuesScreen();
    private final ClassiCubeLoginScreen classiCubeLoginScreen = new ClassiCubeLoginScreen();
    private final ClassiCubeMFAScreen classiCubeMFAScreen = new ClassiCubeMFAScreen();
    private final ClassiCubeServerListScreen classiCubeServerListScreen = new ClassiCubeServerListScreen();
    private final BetaCraftServerListScreen betaCraftServerListScreen = new BetaCraftServerListScreen();
    private final GridItemSelectionScreen gridItemSelectionScreen = new GridItemSelectionScreen();

    @Override
    public void openViaFabricPlusScreen(final Screen parent) {
        this.viaFabricPlusScreen.open(parent);
    }

    @Override
    public void openSettingsScreen(final Screen parent) {
        this.settingsScreen.open(parent);
    }

    public ViaFabricPlusScreen protocolSelectionScreen() {
        return this.viaFabricPlusScreen;
    }

    public SettingsScreen settingsScreen() {
        return this.settingsScreen;
    }

    public ReportIssuesScreen reportIssuesScreen() {
        return this.reportIssuesScreen;
    }

    public ClassiCubeLoginScreen classiCubeLoginScreen() {
        return this.classiCubeLoginScreen;
    }

    public ClassiCubeMFAScreen classiCubeMFAScreen() {
        return this.classiCubeMFAScreen;
    }

    public ClassiCubeServerListScreen classiCubeServerListScreen() {
        return this.classiCubeServerListScreen;
    }

    public BetaCraftServerListScreen betaCraftServerListScreen() {
        return this.betaCraftServerListScreen;
    }

    public GridItemSelectionScreen gridItemSelectionScreen() {
        return this.gridItemSelectionScreen;
    }

}
