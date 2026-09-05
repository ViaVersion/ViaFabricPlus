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

package com.viaversion.viafabricplus.screen.base.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import org.jspecify.annotations.NonNull;

public class VFPList extends ObjectSelectionList<VFPListEntry> {

    private static final int ENTRIES_PER_SCROLL_STEP = 5;
    private static final int MAX_SCROLL_STEPS = 2;

    public VFPList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraftClient, width, height - top - bottom, top, entryHeight);
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        updateSlotAmount(scrollAmount()); // Ensure value is clamped
    }

    @Override
    protected double scrollRate() {
        final int steps = Math.min(this.getItemCount() / ENTRIES_PER_SCROLL_STEP, MAX_SCROLL_STEPS);
        return Math.max(super.scrollRate(), steps * (double) this.defaultEntryHeight);
    }

    @Override
    protected void extractSelection(final @NonNull GuiGraphicsExtractor graphics, final @NonNull VFPListEntry entry, final int outlineColor) {
        // Remove selection box
    }

    protected void updateSlotAmount(final double amount) {
        // To be overridden
    }

}
