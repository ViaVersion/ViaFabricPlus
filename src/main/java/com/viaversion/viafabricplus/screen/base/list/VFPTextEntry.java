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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public final class VFPTextEntry extends VFPListEntry {

    private final Component text;

    public VFPTextEntry(final Component text) {
        this.text = text;
    }

    @Override
    public @NonNull Component getNarration() {
        return this.text;
    }

    @Override
    public void extractContent(final @NonNull GuiGraphicsExtractor context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
        final Matrix3x2fStack matrices = context.pose();

        // The text isn't clickable, so it's rendered without the background of a normal entry
        matrices.pushMatrix();
        matrices.translate(getContentX(), getContentY());
        mappedRender(context, getContentX(), getContentY(), getContentWidth(), getContentHeight(), mouseX, mouseY, hovered, deltaTicks);
        matrices.popMatrix();
    }

    @Override
    public void mappedRender(final GuiGraphicsExtractor context, final int x, final int y, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
        final Font font = Minecraft.getInstance().font;

        context.centeredText(font, this.text, entryWidth / 2, entryHeight / 2 - font.lineHeight / 2, -1);
    }

}
