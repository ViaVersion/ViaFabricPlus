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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.components.ObjectSelectionList.Entry} class.
 * Features included:
 * <ul>
 *     <li>Add wrapper function {@link #mappedRender(GuiGraphics, int, int, int, int, int, int, boolean, float)} for:
 *     <ul>
 *         <li>cross-sharing entry position/dimension between other helper functions</li>
 *         <li>Setting the entry position as start inside the {@link PoseStack}</li>
 *         <li>rendering a default background</li>
 *     </ul>
 *     <li>Adds {@link #mappedMouseClicked(double, double, int)} to automatically play a click sound</li>
 *     <li>Adds some more utility functions, see {@link #renderScrollableText(Component, int, int)} and {@link #renderTooltip(Component, int, int)}</li>
 *     </li>
 * </ul>
 */
public abstract class VFPListEntry extends ObjectSelectionList.Entry<VFPListEntry> {

    protected static final int SCISSORS_OFFSET = 4;
    public static final int SLOT_MARGIN = 3;

    private GuiGraphics context;

    public void mappedRender(GuiGraphics context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        // To be overridden
    }

    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        // To be overridden
    }

    /**
     * Automatically plays a click sound and calls the {@link #mappedMouseClicked(double, double, int)} method
     */
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

    /**
     * Automatically scrolls the text if it is too long to be displayed in the slot. The text will be scrolled from right to left
     *
     * @param text   The text which should be displayed
     * @param textY  The Y position of the text
     * @param offset The offset of the text from the left side of the slot, this is used to calculate the width of the text, which should be scrolled
     */
    public void renderScrollableText(final Component text, final int textY, final int offset) {
        final Font font = Minecraft.getInstance().font;

        final int fontWidth = font.width(text);
        if (fontWidth > (getContentWidth() - offset)) {
            final double time = (double) Util.getMillis() / 1000.0;
            final double interpolateEnd = fontWidth - (getContentWidth() - offset - (SCISSORS_OFFSET + SLOT_MARGIN));

            final double interpolatedValue = Math.sin((Math.PI / 2) * Math.cos(Math.PI * 2 * time / Math.max(interpolateEnd * 0.5, 3.0))) / 2.0 + 0.5;

            context.enableScissor(0, 0, getContentWidth() - offset - SCISSORS_OFFSET, getContentHeight());
            context.drawString(font, text, SLOT_MARGIN - (int) Mth.lerp(interpolatedValue, 0.0, interpolateEnd), textY, -1);
            context.disableScissor();
        } else {
            context.drawString(font, text, SLOT_MARGIN, textY, -1);
        }
    }

    /**
     * Draws a tooltip if the mouse is hovering over the slot
     *
     * @param tooltip The tooltip which should be displayed
     * @param mouseX  The current mouse X position
     * @param mouseY  The current mouse Y position
     */
    public void renderTooltip(final @Nullable Component tooltip, final int mouseX, final int mouseY) {
        if (tooltip != null && mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()) {
            context.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
        }
    }

    /**
     * Automatically draws a background for the slot with the slot's dimension and calls the {@link #mappedRender(GuiGraphics, int, int, int, int, int, int, boolean, float)} method
     */
    @Override
    public void renderContent(final GuiGraphics context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
        this.context = context; // Allows cross-sharing between util methods

        final Matrix3x2fStack matrices = context.pose();

        matrices.pushMatrix();
        matrices.translate(getContentX(), getContentY());
        context.fill(0, 0, getContentWidth(), getContentHeight(), Integer.MIN_VALUE);
        mappedRender(context, getContentX(), getContentY(), getContentWidth(), getContentHeight(), mouseX, mouseY, hovered, deltaTicks);
        matrices.popMatrix();
    }

}
