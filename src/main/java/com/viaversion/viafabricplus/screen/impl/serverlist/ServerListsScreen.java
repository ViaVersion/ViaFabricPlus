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

package com.viaversion.viafabricplus.screen.impl.serverlist;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.screen.ScreensImpl;
import com.viaversion.viafabricplus.screen.base.VFPPopup;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public final class ServerListsScreen extends VFPPopup {

    public static final Component TITLE = Component.translatable("screen.viafabricplus.server_lists");

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_MARGIN = 3;

    public ServerListsScreen() {
        super(TITLE, BUTTON_WIDTH + 2 * BUTTON_MARGIN, 2 * (BUTTON_HEIGHT + BUTTON_MARGIN) + BUTTON_MARGIN);
    }

    @Override
    protected void initBody(final ScreenRectangle body) {
        final ScreensImpl screens = ViaFabricPlusImpl.impl().screens();
        final int x = body.left() + (body.width() - BUTTON_WIDTH) / 2;

        // Opened with the parent of the popup, so going back from a server list skips the popup
        this.addRenderableWidget(Button.builder(ClassiCubeServerListScreen.TITLE, _ -> screens.openClassiCubeServerListScreen(this.prevScreen))
            .pos(x, body.top() + BUTTON_MARGIN).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        this.addRenderableWidget(Button.builder(BetaCraftServerListScreen.TITLE, _ -> screens.openBetaCraftServerListScreen(this.prevScreen))
            .pos(x, body.top() + BUTTON_MARGIN + BUTTON_HEIGHT + BUTTON_MARGIN).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

}
