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

package com.viaversion.viafabricplus.screen.base;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.util.Mth;

public final class VFPTabBar extends MenuTabBar {

    private static final int LABEL_MARGIN = 4;
    private static final int SCREEN_MARGIN = 28; // Same as vanilla

    public VFPTabBar(final int y, final int width, final int height, final TabManager tabManager, final ImmutableList<TabButton> tabButtons, final ImmutableList<Tab> tabs) {
        super(0, y, width, height, tabManager, tabButtons, tabs);
    }

    @Override
    public void arrangeElements(final int width) {
        super.arrangeElements(width);

        // Vanilla spreads a fixed width over all tabs, which leaves too little room for longer labels with many tabs
        final Font font = Minecraft.getInstance().font;
        final int labelWidth = this.tabButtons.stream().mapToInt(button -> font.width(button.getMessage())).max().orElse(0);
        final int tabWidth = Math.min(labelWidth + LABEL_MARGIN * 2, (width - SCREEN_MARGIN) / this.tabButtons.size()) & ~1; // Even like vanilla
        if (tabWidth > this.tabButtons.getFirst().getWidth()) {
            this.tabButtons.forEach(button -> button.setWidth(tabWidth));
            this.layout.arrangeElements();
            this.layout.setX(Mth.roundToward((width - this.layout.getWidth()) / 2, 2));
        }

        this.layout.setY(this.getY());
    }

}
