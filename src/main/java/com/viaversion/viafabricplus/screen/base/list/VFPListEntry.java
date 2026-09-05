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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public abstract class VFPListEntry extends ObjectSelectionList.Entry<VFPListEntry> {

    protected static final int SCISSORS_OFFSET = 4;
    public static final int SLOT_MARGIN = 3;

    private GuiGraphicsExtractor context;

    public void mappedRender(GuiGraphicsExtractor context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // To be overridden
    }

    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        // To be overridden
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent click, final boolean doubled) {
        mappedMouseClicked(click.x(), click.y(), click.button());
        AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
        return super.mouseClicked(click, doubled);
    }

    public void renderScrollableText(final Component name, final int offset) {
        final Font font = Minecraft.getInstance().font;

        renderScrollableText(name, getContentHeight() / 2 - font.lineHeight / 2, offset);
    }

    public void renderScrollableText(final Component text, final int textY, final int offset) {
        final Font font = Minecraft.getInstance().font;

        final int fontWidth = font.width(text);
        if (fontWidth > (getContentWidth() - offset)) {
            final double time = (double) Util.getMillis() / 1000.0;
            final double interpolateEnd = fontWidth - (getContentWidth() - offset - (SCISSORS_OFFSET + SLOT_MARGIN));

            final double interpolatedValue = Math.sin((Math.PI / 2) * Math.cos(Math.PI * 2 * time / Math.max(interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5;

            context.enableScissor(0, 0, getContentWidth() - offset - SCISSORS_OFFSET, getContentHeight());
            context.text(font, text, SLOT_MARGIN - (int) Mth.lerp(interpolatedValue, 0.0, interpolateEnd), textY, -1);
            context.disableScissor();
        } else {
            context.text(font, text, SLOT_MARGIN, textY, -1);
        }
    }

    @Override
    public void extractContent(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
        this.context = graphics; // Allows cross-sharing between util methods

        final Matrix3x2fStack matrices = this.context.pose();

        matrices.pushMatrix();
        matrices.translate(getContentX(), getContentY());
        this.context.fill(0, 0, getContentWidth(), getContentHeight(), Integer.MIN_VALUE);
        mappedRender(this.context, getContentX(), getContentY(), getContentWidth(), getContentHeight(), mouseX, mouseY, hovered, deltaTicks);
        matrices.popMatrix();
    }

}
