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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.screen.base.VFPScreen;
import java.net.URI;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

abstract class LegacyStyleScreen extends VFPScreen {

    private static final int TITLE_COLOR = 0xFFFFA500;
    private static final String PROJECT_URL = "https://github.com/ViaVersion/ViaFabricPlus";

    private final Component subtitle;
    private final boolean linkedSubtitle;

    protected LegacyStyleScreen(final Component title, final Component subtitle, final boolean linkedSubtitle) {
        super(title, false);
        this.subtitle = subtitle;
        this.linkedSubtitle = linkedSubtitle;
    }

    protected static Component projectSubtitle() {
        return Component.nullToEmpty(PROJECT_URL);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.nullToEmpty("<-"), _ -> this.onClose())
            .pos(5, 5).size(20, 20).build());
        if (this.linkedSubtitle) {
            final int subtitleWidth = this.font.width(this.subtitle);
            this.addRenderableWidget(new PlainTextButton((this.width - subtitleWidth) / 2,
                (this.font.lineHeight + 2) * 2 + 3, subtitleWidth, this.font.lineHeight + 2,
                this.subtitle, ConfirmLinkScreen.confirmLink(this, URI.create(PROJECT_URL)), this.font));
        }
    }

    @Override
    public void renderTitle(final GuiGraphicsExtractor graphics) {
        final Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.scale(2F, 2F);
        graphics.centeredText(this.font, "ViaFabricPlus", this.width / 4, 3, TITLE_COLOR);
        matrices.popMatrix();

        if (!this.linkedSubtitle) {
            graphics.centeredText(this.font, this.subtitle, this.width / 2,
                (this.font.lineHeight + 2) * 2 + 3, -1);
        }
    }

}
