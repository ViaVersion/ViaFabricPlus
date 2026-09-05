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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.screen.ScreensImpl;
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen;
import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ViaFabricPlusScreen extends AbstractProtocolSelectionScreen {

    public ViaFabricPlusScreen() {
        super(Component.nullToEmpty("ViaFabricPlus"), true);
    }

    @Override
    protected void init() {
        super.init();

        final ScreensImpl screens = ViaFabricPlusImpl.impl().screens();

        // The server lists connect to a server, which is not possible while already being connected to one
        final boolean disconnected = Minecraft.getInstance().getConnection() == null;

        final Button classiCube = Button.builder(ClassiCubeServerListScreen.TITLE, _ -> {
            if (ClassiCubeServerListScreen.loggedIn()) {
                screens.classiCubeServerListScreen().open(this);
            } else {
                screens.classiCubeLoginScreen().open(this);
            }
        }).build();
        classiCube.active = disconnected;

        final Button betaCraft = Button.builder(BetaCraftServerListScreen.TITLE, _ -> screens.betaCraftServerListScreen().open(this)).build();
        betaCraft.active = disconnected;

        this.addFooter(classiCube, betaCraft,
            Button.builder(Component.translatable("base.viafabricplus.settings"), _ -> screens.settingsScreen().open(this)).build(),
            Button.builder(Component.translatable("report.viafabricplus.button"), _ -> screens.reportIssuesScreen().open(this)).build());
    }

    @Override
    protected void select(final ProtocolVersion version) {
        ViaFabricPlus.api().setTargetVersion(version);
    }

    @Override
    protected boolean selected(final ProtocolVersion version) {
        return ViaFabricPlus.api().targetVersion().equals(version);
    }

    @Override
    protected boolean selectable() {
        // Setting the target version while connected to a server is not allowed as this will
        // literally break our code away.
        return Minecraft.getInstance().getConnection() == null;
    }

}
