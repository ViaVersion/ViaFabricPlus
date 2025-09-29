/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget.Entry} class.
 * Features included:
 * <ul>
 *     <li>Add wrapper function {@link #mappedRender(DrawContext, int, int, int, boolean, float)} for:
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

    public void mappedRender(DrawContext context, int index, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // To be overridden
    }

    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        // To be overridden
    }

    /**
     * Automatically plays a click sound and calls the {@link #mappedMouseClicked(double, double, int)} method
     */
    @Override
    public boolean mouseClicked(final Click input, final boolean doubled) {
        mappedMouseClicked(input.x(), input.y(), input.button());
        ClickableWidget.playClickSound(MinecraftClient.getInstance().getSoundManager());
        return super.mouseClicked(input, doubled);
    }

    public void renderScrollableText(final Text name, final int offset) {
        final TextRenderer font = MinecraftClient.getInstance().textRenderer;
        renderScrollableText(name, this.getHeight() / 2 - font.fontHeight / 2, offset);
    }

    /**
     * Automatically scrolls the text if it is too long to be displayed in the slot. The text will be scrolled from right to left
     *
     * @param text   The text which should be displayed
     * @param textY  The Y position of the text
     * @param offset The offset of the text from the left side of the slot, this is used to calculate the width of the text, which should be scrolled
     */
    public void renderScrollableText(final Text text, final int textY, final int offset) {
        final TextRenderer font = MinecraftClient.getInstance().textRenderer;
        final int fontWidth = font.getWidth(text);
        if (fontWidth > (this.getWidth() - offset)) {
            final double time = (double) Util.getMeasuringTimeMs() / 1000.0;
            final double interpolateEnd = fontWidth - (this.getWidth() - offset - (SCISSORS_OFFSET + SLOT_MARGIN));
            final double interpolatedValue = Math.sin((Math.PI / 2) * Math.cos(Math.PI * 2 * time / Math.max(interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5;
            context.enableScissor(0, 0, this.getWidth() - offset - SCISSORS_OFFSET, this.getHeight());
            context.drawTextWithShadow(font, text, SLOT_MARGIN - (int) MathHelper.lerp(interpolatedValue, 0.0, interpolateEnd), textY, -1);
            context.disableScissor();
        } else {
            context.drawTextWithShadow(font, text, SLOT_MARGIN, textY, -1);
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
        if (tooltip != null && mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, mouseX, mouseY);
        }
    }

    /**
     * Automatically draws a background for the slot with the slot's dimension and calls the {@link #mappedRender(DrawContext, int, int, int, boolean, float)} method
     */
    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
        // Allows cross-sharing of global variables between util methods
        this.context = context;
        final Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(this.getX(), this.getY());
        context.fill(0, 0, this.getWidth() - 4 /* int i = this.left + (this.width - entryWidth) / 2; int j = this.left + (this.width + entryWidth) / 2; */, this.getHeight(), Integer.MIN_VALUE);
        mappedRender(context, 0 /* TODO */, mouseX, mouseY, hovered, tickDelta);
        matrices.popMatrix();
    }
}
