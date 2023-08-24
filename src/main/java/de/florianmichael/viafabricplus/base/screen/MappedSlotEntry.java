/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.base.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry} class which provides some global
 * functions and features used in all screens which are added by ViaFabricPlus
 */
public abstract class MappedSlotEntry extends AlwaysSelectedEntryListWidget.Entry<MappedSlotEntry> {
    protected final static int SCISSORS_OFFSET = 4;
    public final static int SLOT_MARGIN = 3;

    public abstract void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);
    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
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

    /**
     * Automatically scrolls the text if it is too long to be displayed in the slot. The text will be scrolled from right to left
     *
     * @param context The {@link DrawContext} of the screen
     * @param name The text which should be displayed
     * @param x The x position of the slot
     * @param y The y position of the slot
     * @param entryWidth The width of the slot
     * @param entryHeight The height of the slot
     * @param offset The offset of the text from the left side of the slot, this is used to calculate the width of the text, which should be scrolled (Scrolling is enabled when entryWidth - offset < textWidth)
     */
    public void renderScrollableText(final DrawContext context, final Text name, final int x, final int y, final int entryWidth, final int entryHeight, final int offset) {
        final var font = MinecraftClient.getInstance().textRenderer;

        final var fontWidth = font.getWidth(name);
        final var textY = entryHeight / 2 - font.fontHeight / 2;

        if (fontWidth > (entryWidth - offset)) {
            final var time = (double) Util.getMeasuringTimeMs() / 1000.0;
            final var interpolateEnd = fontWidth - (entryWidth - offset - (SCISSORS_OFFSET + SLOT_MARGIN));

            final var interpolatedValue = Math.sin((Math.PI / 2) * Math.cos(Math.PI * 2 * time / Math.max((double) interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5;

            context.enableScissor(x, y, x + entryWidth - offset - SCISSORS_OFFSET, y + entryHeight);
            context.drawTextWithShadow(font, name, SLOT_MARGIN - (int) MathHelper.lerp(interpolatedValue, 0.0, interpolateEnd), textY, -1);
            context.disableScissor();
        } else {
            context.drawTextWithShadow(font, name, SLOT_MARGIN, textY, -1);
        }
    }

    /**
     * Automatically draws a background for the slot with the slot's dimension and calls the {@link #mappedRender(DrawContext, int, int, int, int, int, int, int, boolean, float)} method
     */
    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(x, y, 0);
        context.fill(0, 0, entryWidth - 4 /* int i = this.left + (this.width - entryWidth) / 2; int j = this.left + (this.width + entryWidth) / 2; */, entryHeight, Integer.MIN_VALUE);
        mappedRender(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        matrices.pop();
    }
}
