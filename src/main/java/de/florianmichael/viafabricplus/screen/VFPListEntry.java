/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry} class.
 * Features included:
 * <ul>
 *     <li>Add wrapper function {@link #mappedRender(DrawContext, int, int, int, int, int, int, int, boolean, float)} for:
 *     <ul>
 *         <li>cross-sharing entry position/dimension between other helper functions</li>
 *         <li>Setting the entry position as start inside the {@link MatrixStack}</li>
 *         <li>rendering a default background</li>
 *     </ul>
 *     <li>Adds {@link #mappedMouseClicked(double, double, int)} to automatically play a click sound</li>
 *     <li>Adds some more utility functions, see {@link #renderScrollableText(Text, int, int)} and {@link #renderTooltip(Text, int, int)}</li>
 *     </li>
 * </ul>
 */
public abstract class VFPListEntry extends AlwaysSelectedEntryListWidget.Entry<VFPListEntry> {

    protected static final int SCISSORS_OFFSET = 4;
    public static final int SLOT_MARGIN = 3;

    private DrawContext context;
    private int x;
    private int y;
    private int entryWidth;
    private int entryHeight;

    public void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // To be overridden
    }

    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        // To be overridden
    }

    /**
     * Automatically plays a click sound and calls the {@link #mappedMouseClicked(double, double, int)} method
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mappedMouseClicked(mouseX, mouseY, button);
        VFPScreen.playClickSound();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void renderScrollableText(final Text name, final int offset) {
        final TextRenderer font = MinecraftClient.getInstance().textRenderer;

        renderScrollableText(name, entryHeight / 2 - font.fontHeight / 2, offset);
    }

    /**
     * Automatically scrolls the text if it is too long to be displayed in the slot. The text will be scrolled from right to left
     *
     * @param name   The text which should be displayed
     * @param textY  The Y position of the text
     * @param offset The offset of the text from the left side of the slot, this is used to calculate the width of the text, which should be scrolled (Scrolling is enabled when entryWidth - offset < textWidth)
     */
    public void renderScrollableText(final Text name, final int textY, final int offset) {
        final TextRenderer font = MinecraftClient.getInstance().textRenderer;

        final int fontWidth = font.getWidth(name);
        if (fontWidth > (entryWidth - offset)) {
            final double time = (double) Util.getMeasuringTimeMs() / 1000.0;
            final double interpolateEnd = fontWidth - (entryWidth - offset - (SCISSORS_OFFSET + SLOT_MARGIN));

            final double interpolatedValue = Math.sin((Math.PI / 2) * Math.cos(Math.PI * 2 * time / Math.max(interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5;

            context.enableScissor(0, 0, entryWidth - offset - SCISSORS_OFFSET, entryHeight);
            context.drawTextWithShadow(font, name, SLOT_MARGIN - (int) MathHelper.lerp(interpolatedValue, 0.0, interpolateEnd), textY, -1);
            context.disableScissor();
        } else {
            context.drawTextWithShadow(font, name, SLOT_MARGIN, textY, -1);
        }
    }

    /**
     * Draws a tooltip if the mouse is hovering over the slot
     *
     * @param tooltip The tooltip which should be displayed
     * @param mouseX  The current mouse X position
     * @param mouseY  The current mouse Y position
     */
    public void renderTooltip(final @Nullable Text tooltip, final int mouseX, final int mouseY) {
        if (tooltip != null && mouseX >= x && mouseX <= x + entryWidth && mouseY >= y && mouseY <= y + entryHeight) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX - x, mouseY - y);
        }
    }

    /**
     * Automatically draws a background for the slot with the slot's dimension and calls the {@link #mappedRender(DrawContext, int, int, int, int, int, int, int, boolean, float)} method
     */
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // Allows cross-sharing of global variables between util methods
        this.context = context;
        this.x = x;
        this.y = y;
        this.entryWidth = entryWidth;
        this.entryHeight = entryHeight;

        final MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(x, y, 0);
        context.fill(0, 0, entryWidth - 4 /* int i = this.left + (this.width - entryWidth) / 2; int j = this.left + (this.width + entryWidth) / 2; */, entryHeight, Integer.MIN_VALUE);
        mappedRender(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        matrices.pop();
    }

}
