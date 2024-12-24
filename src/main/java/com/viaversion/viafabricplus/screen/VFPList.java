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

package com.viaversion.viafabricplus.screen;

import com.viaversion.viafabricplus.screen.impl.PerServerVersionScreen;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

/**
 * Wrapper class for {@link AlwaysSelectedEntryListWidget} including the following features:
 * <ul>
 *     <li>Changing the constructor arguments to be more readable and customizable</li>
 *     <li>Adds {@link #initScrollY(double)} to save the scroll state after closing the screen, requires static tracking by the implementation</li>
 *     <li>Removes the selection box</li>
 * </ul>
 *
 * @see ProtocolSelectionScreen
 * @see PerServerVersionScreen
 */
public class VFPList extends AlwaysSelectedEntryListWidget<VFPListEntry> {

    public VFPList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraftClient, width, height - top - bottom, top, entryHeight);
    }

    public void initScrollY(final double scrollY) {
        // Needs calling last in init to have data loaded before setting scroll amount
        if (GeneralSettings.INSTANCE.saveScrollPositionInSlotScreens.getValue()) {
            this.setScrollY(scrollY);
        }
    }

    @Override
    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        updateSlotAmount(getScrollY()); // Ensure value is clamped
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        // Remove selection box
    }

    protected void updateSlotAmount(final double amount) {
        // To be overridden
    }

}
