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

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class VFPPopup extends VFPScreen {

    private static final int SPRITE_BORDER = 18; // Space the nine sliced background sprites need it around the content
    private static final int TITLE_HEIGHT = 20;

    private final int bodyWidth;
    private final int bodyHeight;

    private ScreenRectangle panel = ScreenRectangle.empty();

    protected VFPPopup(final Component title, final int bodyWidth, final int bodyHeight) {
        super(title, false);

        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
    }

    @Override
    protected final void init() {
        if (this.prevScreen != null) {
            this.prevScreen.init(this.width, this.height);
        }

        final int panelWidth = Math.min(this.bodyWidth, this.width - 2 * SPRITE_BORDER);
        final int panelHeight = Math.min(TITLE_HEIGHT + this.bodyHeight, this.height - 2 * SPRITE_BORDER);
        this.panel = new ScreenRectangle((this.width - panelWidth) / 2, (this.height - panelHeight) / 2, panelWidth, panelHeight);

        this.initBody(new ScreenRectangle(this.panel.left(), this.panel.top() + TITLE_HEIGHT, panelWidth, panelHeight - TITLE_HEIGHT));

        super.init();
    }

    protected abstract void initBody(final ScreenRectangle body);

    @Override
    protected void repositionElements() {
        if (this.prevScreen != null) {
            this.prevScreen.resize(this.width, this.height);
        }

        super.repositionElements();
    }

    @Override
    public void added() {
        super.added();

        if (this.prevScreen != null) {
            this.prevScreen.clearFocus();
        }
    }

    @Override
    public void extractBackground(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        if (this.prevScreen == null) {
            super.extractBackground(graphics, mouseX, mouseY, a);
        } else {
            // Keeps the screen the popup was opened from visible behind it, dimmed by the transparent background
            this.prevScreen.extractBackground(graphics, mouseX, mouseY, a);
            graphics.nextStratum();
            this.prevScreen.extractRenderState(graphics, -1, -1, a);
            graphics.nextStratum();
            this.extractTransparentBackground(graphics);
        }

        final int x = this.panel.left() - SPRITE_BORDER;
        final int y = this.panel.top() - SPRITE_BORDER;
        final int width = this.panel.width() + 2 * SPRITE_BORDER;
        final int height = this.panel.height() + 2 * SPRITE_BORDER;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PopupScreen.BACKGROUND_SPRITE, x, y, width, height);
    }

    @Override
    public void renderTitle(final GuiGraphicsExtractor context) {
        final int x = this.panel.left() + this.panel.width() / 2;
        final int y = this.panel.top() + (TITLE_HEIGHT - this.font.lineHeight) / 2;
        context.centeredText(this.font, this.title, x, y, ACCENT_COLOR);
    }

}
