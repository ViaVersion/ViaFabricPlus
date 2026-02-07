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

import com.viaversion.viafabricplus.screen.impl.PerServerVersionScreen;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

/**
 * Wrapper class for {@link ObjectSelectionList} including the following features:
 * <ul>
 *     <li>Changing the constructor arguments to be more readable and customizable</li>
 *     <li>Adds {@link #initScrollY(double)} to save the scroll state after closing the screen, requires static tracking by the implementation</li>
 *     <li>Removes the selection box</li>
 * </ul>
 *
 * @see ProtocolSelectionScreen
 * @see PerServerVersionScreen
 */
public class VFPList extends ObjectSelectionList<VFPListEntry> {

    public VFPList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraftClient, width, height - top - bottom, top, entryHeight);
    }

    public void initScrollY(final double scrollY) {
        // Needs calling last in init to have data loaded before setting scroll amount
        if (GeneralSettings.INSTANCE.saveScrollPositionInSlotScreens.getValue()) {
            this.setScrollAmount(scrollY);
        }
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        updateSlotAmount(scrollAmount()); // Ensure value is clamped
    }

    @Override
    protected void renderSelection(final GuiGraphics context, final VFPListEntry entry, final int color) {
        // Remove selection box
    }

    protected void updateSlotAmount(final double amount) {
        // To be overridden
    }

}
